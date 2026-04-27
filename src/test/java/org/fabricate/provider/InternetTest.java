package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.fabricate.random.Rng;
import org.junit.jupiter.api.Test;

class InternetTest {

    @Test
    void domain_hasSlugAndTld() {
        Internet internet = new Internet(Rng.seeded(0L));

        for (int i = 0; i < 50; i++) {
            String d = internet.domain();
            assertThat(d).matches("[a-z]+(-[a-z]+){0,3}\\.[a-z]+");
        }
    }

    @Test
    void url_isWellFormed() {
        Internet internet = new Internet(Rng.seeded(1L));

        String url = internet.url();

        assertThat(url).matches("https?://[a-z]+(-[a-z]+){0,3}\\.[a-z]+/.+");
    }

    @Test
    void ipv4_hasFourOctetsInRange() {
        Internet internet = new Internet(Rng.seeded(2L));

        for (int i = 0; i < 100; i++) {
            String ip = internet.ipv4();
            String[] parts = ip.split("\\.");
            assertThat(parts).hasSize(4);
            for (String part : parts) {
                int v = Integer.parseInt(part);
                assertThat(v).isBetween(0, 255);
            }
        }
    }

    @Test
    void ipv6_hasEightHexGroups() {
        Internet internet = new Internet(Rng.seeded(3L));

        String ip = internet.ipv6();

        assertThat(ip.split(":")).hasSize(8);
        assertThat(ip).matches("([0-9a-f]{4}:){7}[0-9a-f]{4}");
    }

    @Test
    void macAddress_hasSixHexBytes() {
        Internet internet = new Internet(Rng.seeded(4L));

        String mac = internet.macAddress();

        assertThat(mac).matches("([0-9a-f]{2}:){5}[0-9a-f]{2}");
    }

    @Test
    void userAgent_isFromCuratedList() {
        Internet internet = new Internet(Rng.seeded(5L));

        String ua = internet.userAgent();

        assertThat(ua).isNotBlank();
        assertThat(ua).matches(".*(Mozilla|curl|Wget|Postman|Java|Go-http|python-requests|okhttp).*");
    }

    @Test
    void port_isInUserRange() {
        Internet internet = new Internet(Rng.seeded(6L));

        for (int i = 0; i < 100; i++) {
            assertThat(internet.port()).isBetween(1024, 65535);
        }
    }
}
