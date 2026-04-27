package org.fabricate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;
import org.fabricate.model.Address;
import org.fabricate.model.Person;

/**
 * Fills arbitrary record types with sensible random values inferred from
 * each component's declared type and name.
 *
 * Component-name heuristics for {@code String} components:
 * <ul>
 *   <li>{@code firstName}, {@code lastName}, {@code email}, {@code username}</li>
 *   <li>{@code phoneNumber}, {@code phone}</li>
 *   <li>{@code street}, {@code city}, {@code state}, {@code country},
 *       {@code postalCode}, {@code zip}</li>
 *   <li>{@code jobTitle}, {@code title}</li>
 *   <li>{@code id}, {@code uid}</li>
 * </ul>
 * Names are matched case-insensitively after stripping non-letter characters,
 * so {@code first_name}, {@code FIRST_NAME}, and {@code firstName} all match.
 *
 * Supported component types beyond {@link String}: {@code int}, {@code long},
 * {@code boolean}, {@code double}, {@link UUID}, {@link LocalDate},
 * {@link Address}, {@link Person}, and any nested record (filled recursively).
 *
 * For unsupported component types, throws {@link IllegalArgumentException}
 * with the offending field and type included.
 */
public final class BeanFiller {

    private final Fabricate fab;

    BeanFiller(Fabricate fab) {
        this.fab = fab;
    }

    /** Fills a record type by invoking its canonical constructor with generated values. */
    public <T> T fill(Class<T> type) {
        if (!type.isRecord()) {
            throw new IllegalArgumentException(
                    "BeanFiller currently supports only record types, got " + type.getName());
        }
        RecordComponent[] components = type.getRecordComponents();
        Class<?>[] paramTypes = new Class<?>[components.length];
        Object[] args = new Object[components.length];
        for (int i = 0; i < components.length; i++) {
            paramTypes[i] = components[i].getType();
            args[i] = valueFor(components[i].getName(), components[i].getType());
        }
        try {
            Constructor<T> ctor = type.getDeclaredConstructor(paramTypes);
            ctor.setAccessible(true);
            return ctor.newInstance(args);
        } catch (NoSuchMethodException | InstantiationException
                | IllegalAccessException e) {
            throw new IllegalStateException(
                    "Unable to instantiate " + type.getName() + " — record without canonical constructor?", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(
                    "Constructor of " + type.getName() + " threw", e.getCause());
        }
    }

    private Object valueFor(String rawName, Class<?> type) {
        String name = normalize(rawName);

        if (type == String.class)      return stringValue(name);
        if (type == int.class)         return fab.rng().nextInt(1_000_000);
        if (type == long.class)        return fab.rng().delegate().nextLong();
        if (type == boolean.class)     return fab.rng().delegate().nextBoolean();
        if (type == double.class)      return fab.rng().delegate().nextDouble();
        if (type == UUID.class)        return fab.identities().uuid();
        if (type == LocalDate.class)   return fab.datesOfBirth().birthdate();
        if (type == Address.class)     return fab.addresses().address();
        if (type == Person.class)      return fab.persons().person();
        if (type.isRecord())           return fill(type);

        throw new IllegalArgumentException(
                "BeanFiller does not know how to populate " + rawName
                        + " (" + type.getName() + ")");
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
            case "postalcode", "zip"   -> fab.addresses().postalCode();
            case "jobtitle", "title"   -> fab.jobTitles().jobTitle();
            case "id", "uid"           -> fab.identities().uuid().toString();
            case "username" -> fab.identities().username(
                    fab.names().firstName(), fab.names().lastName(), fab.identities().uuid());
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
