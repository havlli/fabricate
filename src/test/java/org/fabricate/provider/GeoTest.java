package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class GeoTest {

    @Test
    void latitude_isInRange() {
        Geo geo = new Geo(Rng.seeded(0L));

        for (int i = 0; i < 200; i++) {
            double lat = geo.latitude();
            assertThat(lat).isBetween(-90.0, 90.0);
        }
    }

    @Test
    void longitude_isInRange() {
        Geo geo = new Geo(Rng.seeded(1L));

        for (int i = 0; i < 200; i++) {
            double lng = geo.longitude();
            assertThat(lng).isBetween(-180.0, 180.0);
        }
    }

    @Test
    void coordinate_isLatCommaLng() {
        Geo geo = new Geo(Rng.seeded(2L));

        for (int i = 0; i < 50; i++) {
            String coord = geo.coordinate();
            assertThat(coord).matches("-?\\d+\\.\\d{4},-?\\d+\\.\\d{4}");
            String[] parts = coord.split(",");
            double lat = Double.parseDouble(parts[0]);
            double lng = Double.parseDouble(parts[1]);
            assertThat(lat).isBetween(-90.0, 90.0);
            assertThat(lng).isBetween(-180.0, 180.0);
        }
    }

    @Test
    void timezone_looksLikeIana() {
        Geo geo = new Geo(Rng.seeded(3L));

        for (int i = 0; i < 50; i++) {
            String tz = geo.timezone();
            assertThat(tz).isNotBlank();
            assertThat(tz).matches("UTC|[A-Z][A-Za-z_]+/[A-Z][A-Za-z_]+");
        }
    }

    @Test
    void countryCodeIso2_isTwoUppercaseLetters() {
        Geo geo = new Geo(Rng.seeded(4L));

        for (int i = 0; i < 50; i++) {
            assertThat(geo.countryCodeIso2()).matches("[A-Z]{2}");
        }
    }

    @Test
    void countryCodeIso3_isThreeUppercaseLetters() {
        Geo geo = new Geo(Rng.seeded(5L));

        for (int i = 0; i < 50; i++) {
            assertThat(geo.countryCodeIso3()).matches("[A-Z]{3}");
        }
    }

    @Test
    void geohash_usesBase32AlphabetAndRequestedLength() {
        Geo geo = new Geo(Rng.seeded(6L));

        for (int len : new int[]{1, 5, 7, 12}) {
            for (int i = 0; i < 20; i++) {
                String h = geo.geohash(len);
                assertThat(h).hasSize(len);
                assertThat(h).matches("[0-9bcdefghjkmnpqrstuvwxyz]+");
            }
        }
    }

    @Test
    void geohash_zeroOrNegative_throws() {
        Geo geo = new Geo(Rng.seeded(7L));

        assertThatThrownBy(() -> geo.geohash(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> geo.geohash(-3)).isInstanceOf(IllegalArgumentException.class);
    }
}
