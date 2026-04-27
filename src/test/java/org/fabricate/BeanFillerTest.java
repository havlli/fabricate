package org.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.fabricate.model.Address;
import org.junit.jupiter.api.Test;

class BeanFillerTest {

    record UserDto(
            String firstName,
            String lastName,
            String email,
            String phoneNumber,
            String city,
            String country,
            String jobTitle,
            UUID id,
            LocalDate dateOfBirth,
            int loginCount,
            boolean active
    ) {}

    record CompanyDto(String name, Address headquarters, UserDto founder) {}

    record SnakeCase(String first_name, String last_name) {}

    record UnsupportedType(java.io.File file) {}

    @Test
    void fill_populatesRecordByComponentNameAndType() {
        Fabricate fab = Fabricate.builder().seed(1L).build();

        UserDto u = fab.fill(UserDto.class);

        assertThat(u.firstName()).isNotBlank();
        assertThat(u.lastName()).isNotBlank();
        assertThat(u.email()).contains("@");
        assertThat(u.phoneNumber()).startsWith("+");
        assertThat(u.id()).isNotNull();
        assertThat(u.dateOfBirth()).isNotNull();
        assertThat(u.loginCount()).isBetween(0, 1_000_000);
        assertThat(u.city()).isNotBlank();
        assertThat(u.country()).isNotBlank();
        assertThat(u.jobTitle()).isNotBlank();
    }

    @Test
    void fill_recursesIntoNestedRecords() {
        Fabricate fab = Fabricate.builder().seed(2L).build();

        CompanyDto c = fab.fill(CompanyDto.class);

        assertThat(c.name()).isNotBlank();
        assertThat(c.headquarters()).isNotNull();
        assertThat(c.founder()).isNotNull();
        assertThat(c.founder().email()).contains("@");
    }

    @Test
    void fill_isReproducibleUnderFixedSeed() {
        UserDto a = Fabricate.builder().seed(99L).build().fill(UserDto.class);
        UserDto b = Fabricate.builder().seed(99L).build().fill(UserDto.class);

        assertThat(a).isEqualTo(b);
    }

    @Test
    void fill_supportsSnakeCaseComponentNames() {
        Fabricate fab = Fabricate.builder().seed(3L).build();

        SnakeCase s = fab.fill(SnakeCase.class);

        assertThat(s.first_name()).isNotBlank();
        assertThat(s.last_name()).isNotBlank();
    }

    @Test
    void fill_rejectsNonRecordType() {
        Fabricate fab = Fabricate.builder().seed(4L).build();

        assertThatThrownBy(() -> fab.fill(String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("record");
    }

    @Test
    void fill_reportsUnsupportedComponentType() {
        Fabricate fab = Fabricate.builder().seed(5L).build();

        assertThatThrownBy(() -> fab.fill(UnsupportedType.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("File");
    }

    record RichTypes(
            BigDecimal amount,
            URI website,
            Instant lastSeen,
            LocalDateTime createdAt,
            int port,
            int httpStatus,
            double latitude,
            double longitude,
            String email,
            String iban,
            String creditCard,
            String semver,
            String slug
    ) {}

    @Test
    void fill_supportsRichJdkTypesAndNameHeuristics() {
        Fabricate fab = Fabricate.builder().seed(11L).build();

        RichTypes r = fab.fill(RichTypes.class);

        assertThat(r.amount()).isNotNull();
        assertThat(r.website()).isNotNull();
        assertThat(r.website().toString()).matches("https?://.+");
        assertThat(r.lastSeen()).isNotNull();
        assertThat(r.createdAt()).isNotNull();
        assertThat(r.port()).isBetween(1024, 65535);
        assertThat(r.httpStatus()).isBetween(100, 599);
        assertThat(r.latitude()).isBetween(-90.0, 90.0);
        assertThat(r.longitude()).isBetween(-180.0, 180.0);
        assertThat(r.email()).contains("@");
        assertThat(r.iban()).matches("[A-Z]{2}\\d{2}.+");
        assertThat(r.creditCard().replaceAll("\\D", "")).matches("\\d{13,19}");
        assertThat(r.semver()).matches("\\d+\\.\\d+\\.\\d+");
        assertThat(r.slug()).matches("[a-z]+(-[a-z]+)*");
    }

    public static class UserPojo {
        private String firstName;
        private String email;
        private int age;
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
    }

    @Test
    void fill_populatesPojoViaSetters() {
        Fabricate fab = Fabricate.builder().seed(12L).build();

        UserPojo p = fab.fill(UserPojo.class);

        assertThat(p.getFirstName()).isNotBlank();
        assertThat(p.getEmail()).contains("@");
        assertThat(p.getAge()).isBetween(0, 99);
    }

    public static class FieldOnlyPojo {
        public String city;
        public String country;
        public int port;
    }

    @Test
    void fill_populatesPojoFieldsWhenNoSetters() {
        Fabricate fab = Fabricate.builder().seed(13L).build();

        FieldOnlyPojo p = fab.fill(FieldOnlyPojo.class);

        assertThat(p.city).isNotBlank();
        assertThat(p.country).isNotBlank();
        assertThat(p.port).isBetween(1024, 65535);
    }
}
