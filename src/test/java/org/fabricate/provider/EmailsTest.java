package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.fabricate.locale.ChineseLocale;
import org.fabricate.locale.EnglishLocale;
import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class EmailsTest {

    @Test
    void english_emailLocalPart_isLowercased() {
        Emails emails = new Emails(EnglishLocale.INSTANCE, Rng.seeded(0L));

        String email = emails.email("Ada", "Lovelace");

        assertThat(email).startsWith("ada.lovelace@");
        assertThat(EnglishLocale.INSTANCE.emailDomains())
                .contains(email.substring(email.indexOf('@') + 1));
    }

    @Test
    void chinese_emailLocalPart_encodesNonAsciiAsCodePoints() {
        Emails emails = new Emails(ChineseLocale.INSTANCE, Rng.seeded(0L));

        String email = emails.email("伟", "建国");

        String localPart = email.substring(0, email.indexOf('@'));
        assertThat(localPart).matches("\\d+");
        assertThat(ChineseLocale.INSTANCE.emailDomains())
                .contains(email.substring(email.indexOf('@') + 1));
    }
}
