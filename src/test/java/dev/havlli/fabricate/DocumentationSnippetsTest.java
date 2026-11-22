package dev.havlli.fabricate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Compiles the actual documented text, not copies maintained in test fixtures. */
class DocumentationSnippetsTest {
    private static final Path BASE = Path.of(System.getProperty("basedir", "."));
    private static final Pattern JAVA_FENCE = Pattern.compile("(?ms)^```java\\R(.*?)^```$");
    private static final Pattern JAVADOC_SNIPPET = Pattern.compile(
            "(?ms)^ \\* \\{@snippet :\\R(.*?)^ \\* }\\R(?= \\*(?:/|\\R))");
    private static final Pattern IMPORT = Pattern.compile("(?m)^import [^;]+;\\R?");

    @TempDir
    Path temp;

    @Test
    void readmeMigrationAndPublicJavadocExamplesCompile() throws Exception {
        List<Example> examples = new ArrayList<>();
        for (String name : List.of("README.md", "MIGRATION-0.2.md")) {
            String markdown = Files.readString(BASE.resolve(name));
            var blocks = JAVA_FENCE.matcher(markdown);
            int count = 0;
            while (blocks.find()) {
                String code = blocks.group(1);
                if (code.startsWith("// Before (0.1.x)")) continue;
                examples.add(new Example(name + ":" + markdown.substring(0, blocks.start()).lines().count(), code));
                count++;
            }
            assertTrue(count > 0, "No current Java examples found in " + name);
        }
        Path sourceRoot = BASE.resolve("src/main/java/dev/havlli/fabricate");
        try (var sources = Files.walk(sourceRoot)) {
            for (Path source : sources.filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !sourceRoot.relativize(path).startsWith("internal")).sorted().toList()) {
                String text = Files.readString(source);
                var snippets = JAVADOC_SNIPPET.matcher(text);
                int count = 0;
                while (snippets.find()) {
                    examples.add(new Example(source.getFileName() + " snippet " + ++count,
                            snippets.group(1).replaceAll("(?m)^ \\* ?", "")));
                }
                // A changed snippet layout must fail visibly, rather than silently evade compilation.
                assertEquals(text.split("\\Q{@snippet :\\E", -1).length - 1, count, source.toString());
            }
        }

        String dependencies = dependencyPath();
        List<String> compile = new ArrayList<>(List.of("--release", "21", "-Xlint:deprecation,removal",
                "-Werror", "-classpath", dependencies, "-d", temp.resolve("classes").toString()));
        StringBuilder locations = new StringBuilder();
        int index = 0;
        for (Example example : examples) {
            String name = "Example" + ++index;
            locations.append(name).append(": ").append(example.location()).append('\n');
            if (example.code().startsWith("module ")) {
                compileModule(example, name, dependencies);
                continue;
            }
            var imports = IMPORT.matcher(example.code());
            StringBuilder header = new StringBuilder("package documentation;\n");
            while (imports.find()) header.append(imports.group()).append('\n');
            String body = imports.replaceAll("");
            Path file = temp.resolve(name + ".java");
            Files.writeString(file, header + "class " + name + " {\n"
                    + "  void example() throws Exception {\n" + body + "\n  }\n}\n");
            compile.add(file.toString());
        }
        assertFalse(examples.isEmpty());
        javac(compile, locations.toString());
    }

    private void compileModule(Example example, String name, String dependencies) throws Exception {
        Path root = Files.createDirectories(temp.resolve(name));
        Path descriptor = root.resolve("module-info.java");
        Files.writeString(descriptor, example.code());
        // Supply the application DTO package referenced by the documentation's opens directive.
        Path dto = root.resolve("ExampleDto.java");
        Files.writeString(dto, "package my.app.dto; public record ExampleDto(String name) {}\n");
        javac(List.of("--release", "21", "--module-path", dependencies,
                "-d", root.resolve("classes").toString(), descriptor.toString(), dto.toString()),
                example.location());
    }

    private static String dependencyPath() {
        return List.of(Fabricate.class, org.jspecify.annotations.NullMarked.class, Test.class,
                org.opentest4j.AssertionFailedError.class, org.junit.platform.commons.JUnitException.class,
                org.apiguardian.api.API.class).stream().map(type -> {
                    try {
                        return Path.of(type.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
                    } catch (java.net.URISyntaxException e) {
                        throw new IllegalStateException(e);
                    }
                }).distinct().collect(Collectors.joining(File.pathSeparator));
    }

    private void javac(List<String> arguments, String locations) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "javac").toString());
        command.addAll(arguments);
        Path log = Files.createTempFile(temp, "javac", ".log");
        Process process = new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(log.toFile()).start();
        try {
            assertTrue(process.waitFor(45, TimeUnit.SECONDS), "Compilation timed out: " + locations);
            assertEquals(0, process.exitValue(), locations + "\n" + Files.readString(log));
        } finally {
            if (process.isAlive()) process.destroyForcibly();
        }
    }

    private record Example(String location, String code) {}
}
