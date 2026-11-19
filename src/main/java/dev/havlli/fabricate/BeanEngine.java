package dev.havlli.fabricate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import dev.havlli.fabricate.model.Address;
import dev.havlli.fabricate.model.Person;
import org.jspecify.annotations.Nullable;

final class BeanEngine {

    private final Fabricate fab;

    BeanEngine(Fabricate fab) {
        this.fab = fab;
    }

    <T> T fill(Class<T> type) {
        return fill(type, Map.of());
    }

    <T> T fill(Class<T> type, Map<String, Supplier<?>> overrides) {
        validateType(type);
        if (type.isRecord()) return fillRecord(type, overrides);
        return fillPojo(type, overrides);
    }

    static void validateType(Class<?> type) {
        Objects.requireNonNull(type, "type");
        if (!type.isRecord() && !canFillPojo(type)) {
            throw new IllegalArgumentException(
                    "BeanFiller can fill only records or POJOs with a no-arg constructor — got "
                            + type.getName());
        }
    }

    // Use the same property names and targets as the generation loops below.
    static Map<String, List<Class<?>>> properties(Class<?> type) {
        Map<String, List<Class<?>>> properties = new LinkedHashMap<>();
        if (type.isRecord()) {
            for (RecordComponent component : type.getRecordComponents()) {
                properties.put(component.getName(), List.of(component.getType()));
            }
        } else {
            for (Method method : type.getMethods()) {
                @Nullable String name = setterProperty(method);
                if (name != null) {
                    properties.computeIfAbsent(name, ignored -> new ArrayList<>())
                            .add(method.getParameterTypes()[0]);
                }
            }
            for (Field field : type.getDeclaredFields()) {
                if (writable(field)) {
                    properties.putIfAbsent(field.getName(), List.of(field.getType()));
                }
            }
        }
        properties.replaceAll((name, types) -> List.copyOf(types));
        return Collections.unmodifiableMap(properties);
    }

    static void validateValue(String name, Class<?> type, @Nullable Object value) {
        if (value == null ? type.isPrimitive() : !boxed(type).isInstance(value)) {
            throw new IllegalArgumentException("Invalid value for component/property '" + name
                    + "': expected " + type.getTypeName() + ", got "
                    + (value == null ? "null" : value.getClass().getTypeName()));
        }
    }

    private static Class<?> boxed(Class<?> type) {
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == short.class) return Short.class;
        if (type == byte.class) return Byte.class;
        if (type == boolean.class) return Boolean.class;
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == char.class) return Character.class;
        return type;
    }

    private static @Nullable String setterProperty(Method method) {
        String name = method.getName();
        if (method.getParameterCount() != 1 || Modifier.isStatic(method.getModifiers())
                || name.length() <= 3 || !name.startsWith("set")) return null;
        return Character.toLowerCase(name.charAt(3)) + name.substring(4);
    }

    private static boolean writable(Field field) {
        int modifiers = field.getModifiers();
        return !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers);
    }

    private @Nullable Object valueFor(String name, Class<?> type, Map<String, Supplier<?>> overrides) {
        @Nullable Supplier<?> override = overrides.get(name);
        if (override == null) return valueFor(name, type);
        @Nullable Object value = override.get();
        validateValue(name, type, value);
        return value;
    }

    private <T> T fillRecord(Class<T> type, Map<String, Supplier<?>> overrides) {
        RecordComponent[] components = type.getRecordComponents();
        Class<?>[] paramTypes = new Class<?>[components.length];
        Object[] args = new Object[components.length];
        for (int i = 0; i < components.length; i++) {
            paramTypes[i] = components[i].getType();
            args[i] = valueFor(components[i].getName(), components[i].getType(), overrides);
        }
        try {
            Constructor<T> ctor = type.getDeclaredConstructor(paramTypes);
            ctor.setAccessible(true);
            return ctor.newInstance(args);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(
                    "Unable to instantiate " + type.getName() + " — record without canonical constructor?", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(
                    "Constructor of " + type.getName() + " threw", e.getCause());
        }
    }

    private <T> T fillPojo(Class<T> type, Map<String, Supplier<?>> overrides) {
        T instance;
        try {
            Constructor<T> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);
            instance = ctor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to instantiate POJO " + type.getName(), e);
        }

        Set<String> filled = new HashSet<>();
        Set<String> filledExact = new HashSet<>();
        for (Method m : type.getMethods()) {
            @Nullable String prop = setterProperty(m);
            if (prop == null) continue;
            try {
                m.invoke(instance, valueFor(prop, m.getParameterTypes()[0], overrides));
                filled.add(normalize(prop));
                filledExact.add(prop);
            } catch (ReflectiveOperationException ignored) {
                // skip setters that don't accept generated values
            }
        }

        for (Field f : type.getDeclaredFields()) {
            if (!writable(f)) continue;
            // Preserve heuristic field suppression, but allow an explicit exact-name
            // override for a distinct field such as first_name alongside setFirstName.
            if (filled.contains(normalize(f.getName()))
                    && (!overrides.containsKey(f.getName()) || filledExact.contains(f.getName()))) continue;
            try {
                f.setAccessible(true);
                f.set(instance, valueFor(f.getName(), f.getType(), overrides));
            } catch (ReflectiveOperationException ignored) {
                // skip fields we can't write
            }
        }
        return instance;
    }

    private static boolean canFillPojo(Class<?> type) {
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) return false;
        if (type.isPrimitive() || type.isArray() || type.isEnum()) return false;
        String pkg = type.getPackageName();
        if (pkg.startsWith("java.") || pkg.startsWith("javax.") || pkg.startsWith("jdk.")) return false;
        try {
            type.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private Object valueFor(String rawName, Class<?> type) {
        String name = normalize(rawName);

        @Nullable Object special = nameAware(name, type);
        if (special != null) return special;

        if (type == String.class)         return stringValue(name);
        if (type == int.class || type == Integer.class)       return fab.numbers().intBetween(0, 1_000_000);
        if (type == long.class || type == Long.class)         return fab.rng().delegate().nextLong();
        if (type == short.class || type == Short.class)       return (short) fab.numbers().intBetween(0, Short.MAX_VALUE);
        if (type == byte.class || type == Byte.class)         return (byte) fab.numbers().intBetween(0, 127);
        if (type == boolean.class || type == Boolean.class)   return fab.rng().delegate().nextBoolean();
        if (type == double.class || type == Double.class)     return fab.rng().delegate().nextDouble();
        if (type == float.class || type == Float.class)       return (float) fab.rng().delegate().nextDouble();
        if (type == char.class || type == Character.class)    return (char) ('a' + fab.numbers().intBetween(0, 25));

        if (type == UUID.class)           return fab.identities().uuid();
        if (type == LocalDate.class)      return fab.datesOfBirth().birthdate();
        if (type == LocalDateTime.class)  return fab.dates().localDateTimeBetween(
                LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));
        if (type == Instant.class)        return fab.dates().recentInstant(60 * 24 * 30);
        if (type == BigDecimal.class)     return new BigDecimal(String.format(Locale.ROOT,
                "%d.%02d", fab.numbers().intBetween(0, 9999), fab.numbers().intBetween(0, 99)));
        if (type == URI.class)            return URI.create(fab.internet().url());
        if (type == Address.class)        return fab.addresses().address();
        if (type == Person.class)         return fab.persons().person();
        if (type.isRecord())              return fillRecord(type, Map.of());
        if (canFillPojo(type))            return fillPojo(type, Map.of());

        throw new IllegalArgumentException(
                "BeanFiller does not know how to populate " + rawName
                        + " (" + type.getName() + ")");
    }

    private @Nullable Object nameAware(String name, Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return switch (name) {
                case "port" -> fab.internet().port();
                case "httpstatus", "statuscode", "status" -> fab.devops().httpStatus();
                case "age" -> fab.numbers().intBetween(0, 99);
                default -> null;
            };
        }
        if (type == long.class || type == Long.class) {
            return switch (name) {
                case "filesize", "filesizebytes", "size", "bytes" -> fab.files().fileSizeBytes();
                case "latencyms", "latency" -> fab.devops().latencyMs();
                default -> null;
            };
        }
        if (type == double.class || type == Double.class) {
            return switch (name) {
                case "latitude", "lat" -> fab.geo().latitude();
                case "longitude", "lng", "lon" -> fab.geo().longitude();
                default -> null;
            };
        }
        return null;
    }

    private String stringValue(String name) {
        return switch (name) {
            case "firstname"           -> fab.names().firstName();
            case "lastname"            -> fab.names().lastName();
            case "fullname", "name"    -> fab.names().fullName();
            case "email"               -> fab.emails().email();
            case "phone", "phonenumber" -> fab.phones().phoneNumber();
            case "street"              -> fab.addresses().street();
            case "city"                -> fab.addresses().city();
            case "state"               -> fab.addresses().state();
            case "country"             -> fab.addresses().country();
            case "countrycode"         -> fab.geo().countryCodeIso2();
            case "timezone", "tz"      -> fab.geo().timezone();
            case "postalcode", "zip"   -> fab.addresses().postalCode();
            case "jobtitle", "title"   -> fab.jobTitles().jobTitle();
            case "id", "uid"           -> fab.identities().uuid().toString();
            case "username"            -> fab.internet().username();
            case "url", "website", "homepage" -> fab.internet().url();
            case "domain"              -> fab.internet().domain();
            case "ipv", "ip", "ipv4", "ipaddress" -> fab.internet().ipv4();
            case "ipv6"                -> fab.internet().ipv6();
            case "macaddress", "mac"   -> fab.internet().macAddress();
            case "useragent"           -> fab.internet().userAgent();
            case "apikey"              -> fab.internet().apiKey();
            case "token", "bearertoken", "accesstoken" -> fab.internet().bearerToken();
            case "imageurl", "avatar", "avatarurl" -> fab.internet().imageUrl();
            case "iban"                -> fab.finance().iban();
            case "creditcard", "cardnumber" -> fab.finance().creditCard();
            case "currencycode", "currency" -> fab.finance().currencyCode();
            case "money", "amount", "price" -> fab.finance().money();
            case "sku"                 -> fab.commerce().sku();
            case "isbn", "isbn13"      -> fab.commerce().isbn13();
            case "productname", "product" -> fab.commerce().productName();
            case "color", "colorname"  -> fab.commerce().colorName();
            case "colorhex", "hexcolor" -> fab.commerce().colorHex();
            case "environment", "env"  -> fab.devops().environment();
            case "loglevel", "level"   -> fab.devops().logLevel();
            case "semver", "version"   -> fab.devops().semver();
            case "gitsha", "sha", "commitsha" -> fab.devops().gitSha();
            case "branch", "branchname" -> fab.devops().branchName();
            case "dockerimage", "image" -> fab.devops().dockerImage();
            case "filename"            -> fab.files().fileName();
            case "filepath", "path"    -> fab.files().path();
            case "mimetype", "contenttype" -> fab.files().mimeType();
            case "slug"                -> fab.texts().slug();
            case "description", "summary", "bio" -> fab.texts().sentence();
            case "password"            -> fab.passwords().password();
            default -> fab.names().firstName();
        };
    }

    private static String normalize(String name) {
        StringBuilder sb = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isLetter(c)) {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString().toLowerCase(Locale.ROOT);
    }
}
