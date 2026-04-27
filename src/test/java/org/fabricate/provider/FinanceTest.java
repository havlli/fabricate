package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigInteger;
import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class FinanceTest {

    @Test
    void creditCard_isLuhnValid_forEachBrand() {
        Finance finance = new Finance(Rng.seeded(0L));

        for (String brand : new String[] {"VISA", "MASTERCARD", "AMEX", "DISCOVER", "JCB", "DINERS"}) {
            for (int i = 0; i < 30; i++) {
                String number = finance.creditCard(brand).replace("-", "");
                assertThat(luhnValid(number)).as("Luhn check for %s: %s", brand, number).isTrue();
            }
        }
    }

    @Test
    void creditCard_visaStartsWith4AndIsSixteenDigits() {
        Finance finance = new Finance(Rng.seeded(1L));

        for (int i = 0; i < 20; i++) {
            String visa = finance.creditCard("VISA").replace("-", "");
            assertThat(visa).hasSize(16).startsWith("4");
        }
    }

    @Test
    void creditCard_amexStartsWith34or37AndIsFifteenDigits() {
        Finance finance = new Finance(Rng.seeded(2L));

        for (int i = 0; i < 20; i++) {
            String amex = finance.creditCard("AMEX").replace("-", "");
            assertThat(amex).hasSize(15);
            assertThat(amex.startsWith("34") || amex.startsWith("37")).isTrue();
        }
    }

    @Test
    void creditCard_unknownBrandThrows() {
        Finance finance = new Finance(Rng.seeded(3L));

        assertThatThrownBy(() -> finance.creditCard("BITCOIN"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cvv_isThreeDigits() {
        Finance finance = new Finance(Rng.seeded(4L));

        for (int i = 0; i < 30; i++) {
            assertThat(finance.cvv()).matches("\\d{3}");
            assertThat(finance.cvv4()).matches("\\d{4}");
        }
    }

    @Test
    void cardExpiry_hasMonthSlashYearFormat() {
        Finance finance = new Finance(Rng.seeded(5L));

        for (int i = 0; i < 30; i++) {
            String exp = finance.cardExpiry();
            assertThat(exp).matches("(0[1-9]|1[0-2])/\\d{2}");
        }
    }

    @Test
    void currencyCode_isThreeUppercaseLetters() {
        Finance finance = new Finance(Rng.seeded(6L));

        for (int i = 0; i < 20; i++) {
            assertThat(finance.currencyCode()).matches("[A-Z]{3}");
        }
    }

    @Test
    void money_includesSymbolWholeAndFractional() {
        Finance finance = new Finance(Rng.seeded(7L));

        String usd = finance.money("USD");
        assertThat(usd).startsWith("$").matches("\\$[\\d,]+\\.\\d{2}");

        String eur = finance.money("EUR");
        assertThat(eur).startsWith("€");

        String unknown = finance.money("XYZ");
        assertThat(unknown).startsWith("XYZ ");
    }

    @Test
    void iban_isValidPerCountry() {
        Finance finance = new Finance(Rng.seeded(8L));

        for (String cc : new String[] {"DE", "FR", "GB", "IT", "ES", "NL", "PL", "CZ", "SE", "NO"}) {
            for (int i = 0; i < 20; i++) {
                String iban = finance.iban(cc);
                assertThat(iban).startsWith(cc);
                assertThat(ibanValid(iban)).as("IBAN %s", iban).isTrue();
            }
        }
    }

    @Test
    void iban_unknownCountryThrows() {
        Finance finance = new Finance(Rng.seeded(9L));

        assertThatThrownBy(() -> finance.iban("ZZ"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void iban_anyCountryIsValid() {
        Finance finance = new Finance(Rng.seeded(10L));

        for (int i = 0; i < 100; i++) {
            String iban = finance.iban();
            assertThat(ibanValid(iban)).as("IBAN %s", iban).isTrue();
        }
    }

    private static boolean luhnValid(String digits) {
        int sum = 0;
        boolean doubleIt = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int d = digits.charAt(i) - '0';
            if (doubleIt) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            sum += d;
            doubleIt = !doubleIt;
        }
        return sum % 10 == 0;
    }

    private static boolean ibanValid(String iban) {
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rearranged.length(); i++) {
            char c = rearranged.charAt(i);
            if (Character.isDigit(c)) sb.append(c);
            else if (c >= 'A' && c <= 'Z') sb.append(c - 'A' + 10);
            else return false;
        }
        return new BigInteger(sb.toString()).mod(BigInteger.valueOf(97)).intValue() == 1;
    }
}
