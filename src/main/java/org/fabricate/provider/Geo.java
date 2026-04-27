package org.fabricate.provider;

import java.util.List;
import java.util.Locale;
import org.fabricate.random.Rng;

/**
 * Geographic primitives: latitude, longitude, IANA timezones, country codes, geohashes.
 *
 * {@snippet :
 * double  lat = fab.geo().latitude();           // -33.452 ... 64.18
 * double  lng = fab.geo().longitude();
 * String  tz  = fab.geo().timezone();           // "Europe/Berlin"
 * String  cc  = fab.geo().countryCodeIso2();    // "DE"
 * String  geo = fab.geo().geohash(7);           // "u33dc07"
 * }
 */
public final class Geo {

    private static final List<String> TIMEZONES = List.of(
            "UTC",
            "Europe/London", "Europe/Berlin", "Europe/Paris", "Europe/Madrid",
            "Europe/Rome", "Europe/Amsterdam", "Europe/Stockholm", "Europe/Warsaw",
            "Europe/Moscow", "Europe/Istanbul",
            "America/New_York", "America/Chicago", "America/Denver", "America/Los_Angeles",
            "America/Toronto", "America/Vancouver", "America/Mexico_City",
            "America/Sao_Paulo", "America/Buenos_Aires",
            "Africa/Cairo", "Africa/Lagos", "Africa/Johannesburg", "Africa/Nairobi",
            "Asia/Dubai", "Asia/Tehran", "Asia/Karachi", "Asia/Kolkata",
            "Asia/Bangkok", "Asia/Singapore", "Asia/Hong_Kong", "Asia/Shanghai",
            "Asia/Tokyo", "Asia/Seoul",
            "Australia/Sydney", "Australia/Perth", "Pacific/Auckland", "Pacific/Honolulu");

    private static final List<String> COUNTRY_CODES_ISO2 = List.of(
            "US", "CA", "MX", "BR", "AR", "GB", "FR", "DE", "IT", "ES", "PT", "NL", "BE",
            "CH", "AT", "SE", "NO", "DK", "FI", "PL", "CZ", "HU", "RO", "GR", "TR", "RU",
            "UA", "IE", "IS", "EE", "LV", "LT",
            "EG", "ZA", "NG", "KE", "MA", "GH",
            "AE", "SA", "QA", "KW", "IL", "JO",
            "IN", "PK", "BD", "TH", "VN", "ID", "PH", "MY", "SG", "HK", "JP", "KR", "CN", "TW",
            "AU", "NZ");

    private static final List<String> COUNTRY_CODES_ISO3 = List.of(
            "USA", "CAN", "MEX", "BRA", "ARG", "GBR", "FRA", "DEU", "ITA", "ESP", "PRT",
            "NLD", "BEL", "CHE", "AUT", "SWE", "NOR", "DNK", "FIN", "POL", "CZE", "HUN",
            "ROU", "GRC", "TUR", "RUS", "UKR", "IRL", "ISL", "EST", "LVA", "LTU",
            "EGY", "ZAF", "NGA", "KEN", "MAR", "GHA",
            "ARE", "SAU", "QAT", "KWT", "ISR", "JOR",
            "IND", "PAK", "BGD", "THA", "VNM", "IDN", "PHL", "MYS", "SGP", "HKG", "JPN",
            "KOR", "CHN", "TWN", "AUS", "NZL");

    private static final char[] GEOHASH_ALPHABET = "0123456789bcdefghjkmnpqrstuvwxyz".toCharArray();

    private final Rng rng;
    private final Numbers numbers;

    public Geo(Rng rng) {
        this.rng = rng;
        this.numbers = new Numbers(rng);
    }

    /** Latitude in degrees, in {@code [-90, 90]} with 4 decimal places. */
    public double latitude() {
        return roundTo(numbers.doubleBetween(-90.0, 90.0), 4);
    }

    /** Longitude in degrees, in {@code (-180, 180]} with 4 decimal places. */
    public double longitude() {
        return roundTo(numbers.doubleBetween(-180.0, 180.0), 4);
    }

    /** Latitude/longitude pair as {@code "lat,lng"}. */
    public String coordinate() {
        return String.format(Locale.ROOT, "%.4f,%.4f", latitude(), longitude());
    }

    /** A common IANA timezone identifier. */
    public String timezone() {
        return rng.pick(TIMEZONES);
    }

    /** A two-letter ISO 3166-1 country code (e.g. {@code DE}). */
    public String countryCodeIso2() {
        return rng.pick(COUNTRY_CODES_ISO2);
    }

    /** A three-letter ISO 3166-1 country code (e.g. {@code DEU}). */
    public String countryCodeIso3() {
        return rng.pick(COUNTRY_CODES_ISO3);
    }

    /**
     * A pseudo-geohash of the requested length using the standard base-32 alphabet.
     * Not a true geohash of any specific lat/lng — just a string with the right shape.
     */
    public String geohash(int length) {
        if (length <= 0) throw new IllegalArgumentException("length must be > 0");
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(GEOHASH_ALPHABET[numbers.intBetween(0, GEOHASH_ALPHABET.length - 1)]);
        }
        return sb.toString();
    }

    private static double roundTo(double v, int places) {
        double scale = Math.pow(10, places);
        return Math.round(v * scale) / scale;
    }
}
