package org.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
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
}
