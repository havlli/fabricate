package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class CommerceTest {

    @Test
    void productName_hasThreeWords() {
        Commerce commerce = new Commerce(Rng.seeded(0L));

        for (int i = 0; i < 30; i++) {
            String name = commerce.productName();
            assertThat(name.split(" ")).hasSize(3);
        }
    }

    @Test
    void department_isFromCuratedList() {
        Commerce commerce = new Commerce(Rng.seeded(1L));

        for (int i = 0; i < 20; i++) {
            assertThat(commerce.department()).isNotBlank();
        }
    }

    @Test
    void sku_matchesExpectedShape() {
        Commerce commerce = new Commerce(Rng.seeded(2L));

        for (int i = 0; i < 30; i++) {
            assertThat(commerce.sku()).matches("[A-Z]{3}-\\d{4}-[A-Z]{2}");
        }
    }

    @Test
    void ean13_isThirteenDigitsWithValidCheckDigit() {
        Commerce commerce = new Commerce(Rng.seeded(3L));

        for (int i = 0; i < 50; i++) {
            String ean = commerce.ean13();
            assertThat(ean).matches("\\d{13}");
            assertThat(ean13Valid(ean)).as("EAN-13 %s", ean).isTrue();
        }
    }

    @Test
    void isbn13_startsWith978AndHasValidCheckDigit() {
        Commerce commerce = new Commerce(Rng.seeded(4L));

        for (int i = 0; i < 50; i++) {
            String isbn = commerce.isbn13();
            assertThat(isbn).matches("978-\\d-\\d{2}-\\d{6}-\\d");
            assertThat(ean13Valid(isbn.replace("-", ""))).as("ISBN-13 %s", isbn).isTrue();
        }
    }

    @Test
    void colorName_isLowercaseToken() {
        Commerce commerce = new Commerce(Rng.seeded(5L));

        for (int i = 0; i < 30; i++) {
            assertThat(commerce.colorName()).matches("[a-z]+");
        }
    }

    @Test
    void colorHex_isSixHexChars() {
        Commerce commerce = new Commerce(Rng.seeded(6L));

        for (int i = 0; i < 30; i++) {
            assertThat(commerce.colorHex()).matches("#[0-9a-f]{6}");
        }
    }

    @Test
    void colorRgb_hasThreeChannelsInRange() {
        Commerce commerce = new Commerce(Rng.seeded(7L));

        for (int i = 0; i < 30; i++) {
            String rgb = commerce.colorRgb();
            assertThat(rgb).matches("rgb\\(\\d{1,3}, \\d{1,3}, \\d{1,3}\\)");
        }
    }

    private static boolean ean13Valid(String digits) {
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            int d = digits.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : d * 3;
        }
        return sum % 10 == 0;
    }
}
