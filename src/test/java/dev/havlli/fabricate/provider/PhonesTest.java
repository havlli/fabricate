package dev.havlli.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;

import dev.havlli.fabricate.locale.EnglishLocale;
import dev.havlli.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class PhonesTest {

    @Test
    void phoneNumber_startsWithLocaleCountryCode() {
        Phones phones = new Phones(EnglishLocale.INSTANCE, Rng.seeded(0L));

        for (int i = 0; i < 50; i++) {
            String number = phones.phoneNumber();

            assertThat(EnglishLocale.INSTANCE.phoneCountryCodes())
                    .anyMatch(number::startsWith);
            String subscriber = number.substring(
                    EnglishLocale.INSTANCE.phoneCountryCodes()
                            .stream()
                            .filter(number::startsWith)
                            .findFirst()
                            .orElseThrow()
                            .length());
            assertThat(subscriber)
                    .hasSize(9)
                    .matches("\\d+");
        }
    }
}
