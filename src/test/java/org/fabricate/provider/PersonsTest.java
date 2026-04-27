package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.fabricate.Fabricate;
import org.fabricate.model.Address;
import org.fabricate.model.Person;
import org.junit.jupiter.api.Test;

class PersonsTest {

    @Test
    void builder_overridesOnlySpecifiedField_andRandomizesTheRest() {
        Fabricate fab = Fabricate.builder().seed(7L).build();

        Person p = fab.persons().builder()
                .firstName("Ada")
                .jobTitle("CEO")
                .build();

        assertThat(p.firstName()).isEqualTo("Ada");
        assertThat(p.jobTitle()).isEqualTo("CEO");
        assertThat(p.lastName()).isNotEqualTo("Ada");
        assertThat(p.email()).contains("@");
        assertThat(p.address()).isNotNull();
        assertThat(p.dateOfBirth()).isNotNull();
    }

    @Test
    void builder_canFullyOverrideEveryField() {
        Fabricate fab = Fabricate.builder().seed(7L).build();
        Address custom = new Address("1 Test St", "Testville", "TS", "00000", "Testland");
        LocalDate dob = LocalDate.of(1990, 1, 1);

        Person p = fab.persons().builder()
                .firstName("First")
                .lastName("Last")
                .email("a@b.co")
                .username("flast")
                .phoneNumber("+10000000000")
                .address(custom)
                .dateOfBirth(dob)
                .jobTitle("CTO")
                .build();

        assertThat(p.firstName()).isEqualTo("First");
        assertThat(p.lastName()).isEqualTo("Last");
        assertThat(p.email()).isEqualTo("a@b.co");
        assertThat(p.username()).isEqualTo("flast");
        assertThat(p.phoneNumber()).isEqualTo("+10000000000");
        assertThat(p.address()).isEqualTo(custom);
        assertThat(p.dateOfBirth()).isEqualTo(dob);
        assertThat(p.jobTitle()).isEqualTo("CTO");
    }

    @Test
    void person_emailDefault_correlatesWithRandomizedName() {
        Fabricate fab = Fabricate.builder().seed(7L).build();

        Person p = fab.persons().builder().firstName("Ada").lastName("Lovelace").build();

        assertThat(p.email()).startsWith("ada.lovelace@");
    }
}
