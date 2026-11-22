package dev.havlli.fabricate;

/**
 * A source of commerce values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface CommerceGen {

    /** A human-readable product name like {@code "Handcrafted Cotton Chair"}. */
    String productName();

    /** A retail department label. */
    String department();

    /** A SKU like {@code HND-1284-ZX}: 3-letter prefix, 4-digit lot, 2-letter suffix. */
    String sku();

    /** A 13-digit barcode (EAN-13) with a valid GS1 check digit. */
    String ean13();

    /** A 13-digit ISBN with a valid check digit, formatted with hyphens. */
    String isbn13();

    /** A color name from a curated palette. */
    String colorName();

    /** A hex color code like {@code #3a8fe1}. */
    String colorHex();

    /** A {@code rgb(r, g, b)} CSS color. */
    String colorRgb();
}
