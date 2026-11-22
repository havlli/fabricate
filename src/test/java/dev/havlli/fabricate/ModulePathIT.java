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
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Runs after package; the child JVM has only the built jar and an external module. */
class ModulePathIT {
    @TempDir
    Path temp;

    @Test
    void externalLocaleServiceWorksWithoutRuntimeDependenciesAndInternalsStayHidden() throws Exception {
        Path jar = Path.of(System.getProperty("fabricate.jar")).toAbsolutePath();
        assertTrue(Files.isRegularFile(jar), "The packaged library jar must exist");
        Path fixtures = Path.of(System.getProperty("basedir", "."), "src/test/resources/module-path");
        Path modules = temp.resolve("modules");
        // Static dependencies are needed by javac, but deliberately absent from the runtime path.
        String compilePath = jar + File.pathSeparator + List.of(
                org.jspecify.annotations.NullMarked.class, Test.class,
                org.opentest4j.AssertionFailedError.class, org.junit.platform.commons.JUnitException.class,
                org.apiguardian.api.API.class).stream()
                .map(type -> {
                    try {
                        return Path.of(type.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
                    } catch (java.net.URISyntaxException e) {
                        throw new IllegalStateException(e);
                    }
                }).collect(Collectors.joining(File.pathSeparator));
        List<String> compile = new ArrayList<>(List.of("--module-path", compilePath,
                "--module-source-path", fixtures.toString(), "-d", modules.toString(), "-m", "external.locale"));
        Result compiled = run("javac", compile);
        assertEquals(0, compiled.exitCode(), compiled.output());
        Result executed = run("java", List.of("--module-path", jar + File.pathSeparator + modules,
                "-m", "external.locale/example.Main"));
        assertEquals(0, executed.exitCode(), executed.output());
        assertTrue(executed.output().contains("External module SPI and encapsulation verified"), executed.output());

        // A separate consumer compilation must be refused access to every implementation package.
        for (String type : List.of("internal.DefaultGen", "internal.provider.Internet",
                "internal.locale.EnglishLocale", "internal.random.Rng", "internal.sink.DefaultCsvSink")) {
            Path source = temp.resolve("Probe.java");
            Files.writeString(source, "import dev.havlli.fabricate." + type + "; class Probe {}\n");
            Result denied = run("javac", List.of("--module-path", compilePath,
                    "--add-modules", "dev.havlli.fabricate", "-d", temp.resolve("probe").toString(),
                    source.toString()));
            assertFalse(denied.exitCode() == 0, "Internal import unexpectedly compiled: " + type);
            assertTrue(denied.output().contains("does not export"), denied.output());
        }
    }

    private Result run(String tool, List<String> arguments) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", tool).toString());
        command.addAll(arguments);
        Path output = Files.createTempFile(temp, tool, ".log");
        Process process = new ProcessBuilder(command).redirectErrorStream(true).redirectOutput(output.toFile()).start();
        try {
            assertTrue(process.waitFor(45, TimeUnit.SECONDS), "Timed out: " + command);
            return new Result(process.exitValue(), Files.readString(output));
        } finally {
            if (process.isAlive()) process.destroyForcibly();
        }
    }

    private record Result(int exitCode, String output) {}
}
