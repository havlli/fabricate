package dev.havlli.fabricate;

/**
 * A source of internet values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface InternetGen {

    /** Draws an email address using the configured email provider's locale. */
    String email();

    /** Draws an email address for the given names using the configured locale. */
    String email(String firstName, String lastName);

    /** Random domain like {@code orange-laboris.io}. */
    String domain();

    /** Random http(s) URL with a slug path. */
    String url();

    /** IPv4 address in dotted-decimal form. */
    String ipv4();

    /** Eight-group lowercase IPv6 address. */
    String ipv6();

    /** MAC address in colon-separated lowercase hex. */
    String macAddress();

    /** A realistic curated User-Agent string. */
    String userAgent();

    /** Hyphenated URL slug — alias for {@link TextGen#slug()}. */
    String slug();

    /** TCP/UDP-style port number in the user range {@code [1024, 65535]}. */
    int port();

    /**
     * IPv6 address with the longest run of zero groups compressed using {@code ::}.
     * For example {@code 2001:0db8:0000:0000:0000:0000:1428:57ab → 2001:db8::1428:57ab}.
     */
    String ipv6Compressed();

    /** A URL with a 1–3 query parameters appended. */
    String urlWithQuery();

    /**
     * A Stripe-style API secret/key of the form {@code <prefix>_<env>_<random>}.
     * Example shape: {@code sk_test_XXXXXXXXXXXXXXXXXXXXXXXX}.
     */
    String apiKey();

    /** A bearer-style JWT-shaped token (3 base64url-ish parts). */
    String bearerToken();

    /** A picsum.photos placeholder image URL with random dimensions. */
    String imageUrl();

    /** A `username` style identifier — slug joined with a 2–4 digit suffix. */
    String username();
}
