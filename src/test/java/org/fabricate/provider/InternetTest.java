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

    @Test
    void ipv6Compressed_canRoundTrip() {
        Internet internet = new Internet(Rng.seeded(7L));

        for (int i = 0; i < 100; i++) {
            String addr = internet.ipv6Compressed();
            // Either 8 colon-separated groups, or contains exactly one "::"
            assertThat(addr.matches("[0-9a-f:]+")).isTrue();
            int dcCount = addr.split("::", -1).length - 1;
            assertThat(dcCount).isLessThanOrEqualTo(1);
            String full = expandIpv6(addr);
            assertThat(full.split(":")).hasSize(8);
        }
    }

    @Test
    void urlWithQuery_includesQuestionMarkAndPair() {
        Internet internet = new Internet(Rng.seeded(8L));

        for (int i = 0; i < 30; i++) {
            String url = internet.urlWithQuery();
            assertThat(url).contains("?");
            String query = url.substring(url.indexOf('?') + 1);
            assertThat(query).matches("([a-z]+=[A-Za-z0-9]+)(&[a-z]+=[A-Za-z0-9]+){0,2}");
        }
    }

    @Test
    void apiKey_hasPrefixEnvAndBody() {
        Internet internet = new Internet(Rng.seeded(9L));

        for (int i = 0; i < 30; i++) {
            assertThat(internet.apiKey()).matches("(sk|pk|rk|whsec|ak)_(live|test)_[A-Za-z0-9]{24}");
        }
    }

    @Test
    void bearerToken_hasThreeBase64UrlSegments() {
        Internet internet = new Internet(Rng.seeded(10L));

        for (int i = 0; i < 30; i++) {
            assertThat(internet.bearerToken().split("\\.")).hasSize(3);
        }
    }

    @Test
    void imageUrl_pointsAtPicsumWithDimensions() {
        Internet internet = new Internet(Rng.seeded(11L));

        for (int i = 0; i < 30; i++) {
            assertThat(internet.imageUrl()).matches("https://picsum\\.photos/seed/[A-Za-z0-9]{8}/\\d+/\\d+");
        }
    }

    @Test
    void username_hasWordAndDigits() {
        Internet internet = new Internet(Rng.seeded(12L));

        for (int i = 0; i < 30; i++) {
            assertThat(internet.username()).matches("[a-z]+\\d{2,4}");
        }
    }

    private static String expandIpv6(String addr) {
        if (!addr.contains("::")) return addr;
        String[] sides = addr.split("::", -1);
        String[] left = sides[0].isEmpty() ? new String[0] : sides[0].split(":");
        String[] right = sides[1].isEmpty() ? new String[0] : sides[1].split(":");
        int missing = 8 - left.length - right.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < left.length; i++) {
            if (i > 0) sb.append(':');
            sb.append(left[i]);
        }
        for (int i = 0; i < missing; i++) {
            if (sb.length() > 0) sb.append(':');
            sb.append('0');
        }
        for (String s : right) {
            sb.append(':').append(s);
        }
        return sb.toString();
    }
}
