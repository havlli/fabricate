package dev.havlli.fabricate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.module.ModuleDescriptor;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/** Guards the declarations owned by this library, without snapshotting inherited JDK APIs. */
class PublicApiSnapshotTest {

    private static final Path BASE = Path.of(System.getProperty("basedir", "."));
    private static final Path GOLDEN = BASE.resolve("src/test/resources/public-api.txt");
    private static final String UPDATE_PROPERTY = "fabricate.updateApiSnapshot";

    @Test
    void publicApiMatchesSnapshot() throws Exception {
        List<String> actual = snapshot();
        assertFalse(actual.isEmpty(), "No public API found; check class discovery");
        assertFalse(actual.stream().anyMatch(line -> line.contains("dev.havlli.fabricate.internal")),
                "Exported declarations must not expose implementation types");
        if (Boolean.getBoolean(UPDATE_PROPERTY)) {
            Files.createDirectories(GOLDEN.getParent());
            Files.writeString(GOLDEN, String.join("\n", actual) + "\n", StandardCharsets.UTF_8);
            return;
        }

        List<String> expected = Files.exists(GOLDEN)
                ? Files.readAllLines(GOLDEN, StandardCharsets.UTF_8) : List.of();
        if (!expected.equals(actual)) {
            fail("Public API snapshot differs. Review the changes, then run "
                    + "mvn -B -ntp -D" + UPDATE_PROPERTY + "=true -Dtest=PublicApiSnapshotTest test"
                    + " to rewrite src/test/resources/public-api.txt.\n"
                    + unifiedDiff(expected, actual));
        }
    }

    private static List<String> snapshot() throws Exception {
        Module module = Fabricate.class.getModule();
        Path classes = BASE.resolve("target/classes");
        ModuleDescriptor descriptor = module.getDescriptor();
        if (descriptor == null) {
            try (var input = Files.newInputStream(classes.resolve("module-info.class"))) {
                descriptor = ModuleDescriptor.read(input);
            }
        }
        // Scan production output even on the module path: patched modules can contain test fixtures.
        // The descriptor supplies the exports, so no package list can drift from module-info.java.
        List<String> names;
        try (Stream<Path> files = Files.walk(classes)) {
            names = classNames(files.filter(Files::isRegularFile)
                    .map(classes::relativize)
                    .map(path -> path.toString().replace('\\', '/')),
                    exportedPackages(descriptor));
        }

        List<String> lines = new ArrayList<>();
        for (String name : names) {
            Class<?> type = Class.forName(name, false, Fabricate.class.getClassLoader());
            if (type.isSynthetic() || !visible(type.getModifiers())) {
                continue;
            }
            lines.add("TYPE " + kind(type) + " " + type.toGenericString());
            if (type.getGenericSuperclass() != null) {
                lines.add("SUPER " + name + " extends " + type.getGenericSuperclass().getTypeName());
            }
            for (var supertype : type.getGenericInterfaces()) {
                lines.add("SUPER " + name + (type.isInterface() ? " extends " : " implements ")
                        + supertype.getTypeName());
            }
            for (var constructor : type.getDeclaredConstructors()) {
                if (!constructor.isSynthetic() && visible(constructor.getModifiers())) {
                    lines.add("CONSTRUCTOR " + constructor.toGenericString());
                }
            }
            for (var method : type.getDeclaredMethods()) {
                if (!method.isSynthetic() && !method.isBridge() && visible(method.getModifiers())) {
                    lines.add("METHOD " + method.toGenericString());
                }
            }
            for (var field : type.getDeclaredFields()) {
                if (!field.isSynthetic() && visible(field.getModifiers())) {
                    lines.add("FIELD " + field.toGenericString());
                }
            }
        }
        Collections.sort(lines);
        return lines;
    }

    private static Set<String> exportedPackages(ModuleDescriptor descriptor) {
        return descriptor.exports().stream().map(ModuleDescriptor.Exports::source)
                .collect(Collectors.toSet());
    }

    private static List<String> classNames(Stream<String> resources, Set<String> packages) {
        return resources.filter(name -> name.endsWith(".class"))
                .map(name -> name.substring(0, name.length() - ".class".length()).replace('/', '.'))
                .filter(name -> packages.contains(name.substring(0, Math.max(0, name.lastIndexOf('.')))))
                .filter(name -> !name.endsWith(".package-info"))
                .sorted().toList();
    }

    private static boolean visible(int modifiers) {
        return Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers);
    }

    private static String kind(Class<?> type) {
        if (type.isAnnotation()) return "annotation";
        if (type.isEnum()) return "enum";
        if (type.isRecord()) return "record";
        if (type.isInterface()) return "interface";
        return "class";
    }

    /** A single unified hunk, with up to three unchanged lines at either end. */
    private static String unifiedDiff(List<String> expected, List<String> actual) {
        int prefix = 0;
        while (prefix < Math.min(expected.size(), actual.size())
                && expected.get(prefix).equals(actual.get(prefix))) {
            prefix++;
        }
        int suffix = 0;
        while (suffix < Math.min(expected.size(), actual.size()) - prefix
                && expected.get(expected.size() - suffix - 1)
                        .equals(actual.get(actual.size() - suffix - 1))) {
            suffix++;
        }
        int start = Math.max(0, prefix - 3);
        int oldEnd = expected.size() - Math.max(0, suffix - 3);
        int newEnd = actual.size() - Math.max(0, suffix - 3);
        int oldCount = oldEnd - start;
        int newCount = newEnd - start;
        StringBuilder diff = new StringBuilder("--- src/test/resources/public-api.txt\n")
                .append("+++ current public API\n@@ -")
                .append(oldCount == 0 ? start : start + 1).append(',').append(oldCount)
                .append(" +").append(newCount == 0 ? start : start + 1).append(',').append(newCount)
                .append(" @@\n");
        int oldIndex = start;
        int newIndex = start;
        // Both snapshots are sorted, so a merge gives the additions and removals directly.
        while (oldIndex < oldEnd || newIndex < newEnd) {
            if (oldIndex < oldEnd && newIndex < newEnd
                    && expected.get(oldIndex).equals(actual.get(newIndex))) {
                diff.append(' ').append(expected.get(oldIndex++)).append('\n');
                newIndex++;
            } else if (oldIndex < oldEnd && (newIndex == newEnd
                    || expected.get(oldIndex).compareTo(actual.get(newIndex)) < 0)) {
                diff.append('-').append(expected.get(oldIndex++)).append('\n');
            } else {
                diff.append('+').append(actual.get(newIndex++)).append('\n');
            }
        }
        return diff.toString();
    }
}
