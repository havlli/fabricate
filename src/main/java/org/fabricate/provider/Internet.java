package org.fabricate.provider;

import java.util.List;
import java.util.Locale;
import org.fabricate.random.Rng;

/**
 * Internet/network primitives: URLs, domains, IPs, MAC addresses, user agents.
 *
 * {@snippet :
 * String url   = fab.internet().url();          // https://example.org/lorem-ipsum
 * String ip4   = fab.internet().ipv4();         // 203.45.12.78
 * String ua    = fab.internet().userAgent();    // realistic curated UA string
 * String mac   = fab.internet().macAddress();   // 02:42:ac:11:00:02
 * }
 */
public final class Internet {

    private static final List<String> SCHEMES = List.of("https", "http");

    private static final List<String> TLDS = List.of(
            "com", "org", "net", "io", "dev", "app", "ai", "co", "tech", "cloud",
            "de", "uk", "fr", "jp", "cn", "br", "ca", "au", "nl", "es", "it", "se", "no");

    private static final List<String> USER_AGENTS = List.of(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_2) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:121.0) Gecko/20100101 Firefox/121.0",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:120.0) Gecko/20100101 Firefox/120.0",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36",
            "Mozilla/5.0 (iPad; CPU OS 17_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2 Mobile/15E148 Safari/604.1",
            "curl/8.5.0",
            "Wget/1.21.4 (linux-gnu)",
            "PostmanRuntime/7.36.0",
            "Java/21.0.0",
            "Go-http-client/1.1",
            "python-requests/2.31.0",
            "okhttp/4.12.0",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
            "Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0",
            "Mozilla/5.0 (X11; FreeBSD amd64; rv:120.0) Gecko/20100101 Firefox/120.0",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0"
    );

    private final Rng rng;
    private final Numbers numbers;
    private final Texts texts;

    public Internet(Rng rng) {
        this.rng = rng;
        this.numbers = new Numbers(rng);
        this.texts = new Texts(rng);
    }

    /** Random domain like {@code orange-laboris.io}. */
    public String domain() {
        return texts.slug() + "." + rng.pick(TLDS);
    }

    /** Random http(s) URL with a slug path. */
    public String url() {
        return rng.pick(SCHEMES) + "://" + domain() + "/" + texts.slug();
    }

    /** IPv4 address in dotted-decimal form. */
    public String ipv4() {
        return numbers.intBetween(1, 254) + "." + numbers.intBetween(0, 255)
                + "." + numbers.intBetween(0, 255) + "." + numbers.intBetween(1, 254);
    }

    /** Eight-group lowercase IPv6 address. */
    public String ipv6() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            if (i > 0) sb.append(':');
            sb.append(String.format(Locale.ROOT, "%04x", numbers.intBetween(0, 0xFFFF)));
        }
        return sb.toString();
    }

    /** MAC address in colon-separated lowercase hex. */
    public String macAddress() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            if (i > 0) sb.append(':');
            sb.append(String.format(Locale.ROOT, "%02x", numbers.intBetween(0, 0xFF)));
        }
        return sb.toString();
    }

    /** A realistic curated User-Agent string. */
    public String userAgent() {
        return rng.pick(USER_AGENTS);
    }

    /** Hyphenated URL slug — alias for {@link Texts#slug()}. */
    public String slug() {
        return texts.slug();
    }

    /** TCP/UDP-style port number in the user range {@code [1024, 65535]}. */
    public int port() {
        return numbers.intBetween(1024, 65535);
    }

    /**
     * IPv6 address with the longest run of zero groups compressed using {@code ::}.
     * For example {@code 2001:0db8:0000:0000:0000:0000:1428:57ab → 2001:db8::1428:57ab}.
     */
    public String ipv6Compressed() {
        String[] groups = new String[8];
        for (int i = 0; i < 8; i++) {
            int v = (i < 2) ? numbers.intBetween(0, 0xFFFF) : numbers.intBetween(0, 0xFF);
            // Bias half the groups toward zero so compression has something to do.
            if (i >= 2 && i <= 5) v = numbers.intBetween(0, 1) == 0 ? 0 : v;
            groups[i] = Integer.toHexString(v);
        }
        return compressIpv6(groups);
    }

    /** A URL with a 1–3 query parameters appended. */
    public String urlWithQuery() {
        StringBuilder sb = new StringBuilder(url()).append('?');
        int params = numbers.intBetween(1, 3);
        for (int i = 0; i < params; i++) {
            if (i > 0) sb.append('&');
            sb.append(texts.word()).append('=').append(texts.alphanumeric(numbers.intBetween(4, 12)));
        }
        return sb.toString();
    }

    /**
     * A Stripe-style API secret/key of the form {@code <prefix>_<env>_<hex>}.
     * Example: {@code sk_live_4eC39HqLyjWDarjtT1zdp7dc}.
     */
    public String apiKey() {
        String[] prefixes = {"sk", "pk", "rk", "whsec", "ak"};
        String[] envs = {"live", "test"};
        return prefixes[numbers.intBetween(0, prefixes.length - 1)]
                + "_" + envs[numbers.intBetween(0, envs.length - 1)]
                + "_" + texts.alphanumeric(24);
    }

    /** A bearer-style JWT-shaped token (3 base64url-ish parts). */
    public String bearerToken() {
        return texts.alphanumeric(numbers.intBetween(20, 30))
                + "." + texts.alphanumeric(numbers.intBetween(40, 60))
                + "." + texts.alphanumeric(numbers.intBetween(40, 60));
    }

    /** A picsum.photos placeholder image URL with random dimensions. */
    public String imageUrl() {
        int w = numbers.intBetween(200, 1600);
        int h = numbers.intBetween(200, 1200);
        return "https://picsum.photos/seed/" + texts.alphanumeric(8) + "/" + w + "/" + h;
    }

    /** A `username` style identifier — slug joined with a 2–4 digit suffix. */
    public String username() {
        return texts.word() + numbers.intBetween(10, 9999);
    }

    private static String compressIpv6(String[] groups) {
        int bestStart = -1, bestLen = 0;
        int curStart = -1, curLen = 0;
        for (int i = 0; i < 8; i++) {
            if ("0".equals(groups[i])) {
                if (curStart < 0) { curStart = i; curLen = 1; }
                else curLen++;
                if (curLen > bestLen) { bestLen = curLen; bestStart = curStart; }
            } else {
                curStart = -1;
                curLen = 0;
            }
        }
        if (bestLen < 2) {
            return String.join(":", groups);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bestStart; i++) {
            if (i > 0) sb.append(':');
            sb.append(groups[i]);
        }
        sb.append("::");
        for (int i = bestStart + bestLen; i < 8; i++) {
            sb.append(groups[i]);
            if (i < 7) sb.append(':');
        }
        return sb.toString();
    }
}
