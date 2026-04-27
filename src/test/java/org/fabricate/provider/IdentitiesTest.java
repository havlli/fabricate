package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class IdentitiesTest {

    @Test
    void uuid_isVersion4WithRfc4122Variant() {
        Identities ids = new Identities(Rng.seeded(0L));

        for (int i = 0; i < 50; i++) {
            UUID uuid = ids.uuid();
            assertThat(uuid.version()).isEqualTo(4);
            assertThat(uuid.variant()).isEqualTo(2);
        }
    }

    @Test
    void uuid_isReproducibleAcrossSeededInstances() {
        Identities a = new Identities(Rng.seeded(99L));
        Identities b = new Identities(Rng.seeded(99L));

        for (int i = 0; i < 20; i++) {
            assertThat(a.uuid()).isEqualTo(b.uuid());
        }
    }

    @Test
    void username_combinesNamePartsAndUuidPrefix() {
        Identities ids = new Identities(Rng.seeded(0L));
        UUID uuid = UUID.fromString("12345678-90ab-4cde-8f01-234567890abc");

        assertThat(ids.username("Ada", "Lovelace", uuid)).isEqualTo("adal12345678");
    }

    @Test
    void username_emptyLastName_throws() {
        Identities ids = new Identities(Rng.seeded(0L));

        assertThatThrownBy(() -> ids.username("Ada", "", UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
