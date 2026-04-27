package org.fabricate.provider;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.Supplier;
import org.fabricate.model.Address;
import org.fabricate.model.Person;
import org.jspecify.annotations.Nullable;

/**
 * Generates {@link Person} instances by composing the underlying providers.
 *
 * Consumers who want to override one field while randomizing the rest can
 * use {@link #builder()}: any unset field is filled lazily on
 * {@link Builder#build()}.
 */
public final class Persons {

    private final Names names;
    private final Emails emails;
    private final Phones phones;
    private final Addresses addresses;
    private final DatesOfBirth datesOfBirth;
    private final JobTitles jobTitles;
    private final Identities identities;

    public Persons(
            Names names,
            Emails emails,
            Phones phones,
            Addresses addresses,
            DatesOfBirth datesOfBirth,
            JobTitles jobTitles,
            Identities identities
    ) {
        this.names = names;
        this.emails = emails;
        this.phones = phones;
        this.addresses = addresses;
        this.datesOfBirth = datesOfBirth;
        this.jobTitles = jobTitles;
        this.identities = identities;
    }

    public Person person() {
        return builder().build();
    }

    public Builder builder() {
        return new Builder(this);
    }

    public static final class Builder {

        private final Persons owner;

        private @Nullable String firstName;
        private @Nullable String lastName;
        private @Nullable String email;
        private @Nullable UUID uid;
        private @Nullable String username;
        private @Nullable String phoneNumber;
        private @Nullable Address address;
        private @Nullable LocalDate dateOfBirth;
        private @Nullable String jobTitle;

        private Builder(Persons owner) {
            this.owner = owner;
        }

        public Builder firstName(String value)         { this.firstName = value; return this; }
        public Builder lastName(String value)          { this.lastName = value; return this; }
        public Builder email(String value)             { this.email = value; return this; }
        public Builder uid(UUID value)                 { this.uid = value; return this; }
        public Builder username(String value)          { this.username = value; return this; }
        public Builder phoneNumber(String value)       { this.phoneNumber = value; return this; }
        public Builder address(Address value)          { this.address = value; return this; }
        public Builder dateOfBirth(LocalDate value)    { this.dateOfBirth = value; return this; }
        public Builder jobTitle(String value)          { this.jobTitle = value; return this; }

        public Person build() {
            String fn  = orElse(firstName, owner.names::firstName);
            String ln  = orElse(lastName, owner.names::lastName);
            String em  = email != null ? email : owner.emails.email(fn, ln);
            UUID id    = uid != null ? uid : owner.identities.uuid();
            String un  = username != null ? username : owner.identities.username(fn, ln, id);
            String ph  = orElse(phoneNumber, owner.phones::phoneNumber);
            Address ad = address != null ? address : owner.addresses.address();
            LocalDate dob = dateOfBirth != null ? dateOfBirth : owner.datesOfBirth.birthdate();
            String jt  = orElse(jobTitle, owner.jobTitles::jobTitle);

            return new Person(fn, ln, em, id.toString(), un, ph, ad, dob, jt);
        }

        private static <T> T orElse(@Nullable T value, Supplier<T> fallback) {
            return value != null ? value : fallback.get();
        }
    }
}
