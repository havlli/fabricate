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
}
