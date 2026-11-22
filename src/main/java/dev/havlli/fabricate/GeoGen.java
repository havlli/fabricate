package dev.havlli.fabricate;

/**
 * A source of geographic values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface GeoGen {

    /** Latitude in degrees, in {@code [-90, 90]} with 4 decimal places. */
    double latitude();

    /** Longitude in degrees, in {@code (-180, 180]} with 4 decimal places. */
    double longitude();

    /** Latitude/longitude pair as {@code "lat,lng"}. */
    String coordinate();

    /** A common IANA timezone identifier. */
    String timezone();

    /** A two-letter ISO 3166-1 country code (e.g. {@code DE}). */
    String countryCodeIso2();

    /** A three-letter ISO 3166-1 country code (e.g. {@code DEU}). */
    String countryCodeIso3();

    /**
     * A pseudo-geohash of the requested length using the standard base-32 alphabet.
     * Not a true geohash of any specific lat/lng — just a string with the right shape.
     */
    String geohash(int length);
}
