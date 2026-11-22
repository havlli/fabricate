package dev.havlli.fabricate;

/**
 * A source of finance values, sharing its owning {@link Fabricate} provider.
 *
 * <p>This is a <strong>provider type</strong>: users obtain instances only from
 * {@link Fabricate} and must not implement this interface. Methods may be added
 * in future versions. Creating a generator consumes no random values.
 * Instances share their provider state and are not thread-safe.
 */
public interface FinanceGen {

    /** A Luhn-valid card number from a randomly chosen brand, formatted with hyphens. */
    String creditCard();

    /** A Luhn-valid card number for the given brand (e.g. {@code VISA}, {@code AMEX}). */
    String creditCard(String brand);

    /** Three-digit CVV, or four for AMEX-shaped requests. */
    String cvv();

    /** Four-digit CVV (AMEX). */
    String cvv4();

    /** {@code MM/YY} expiry in the next five years. */
    String cardExpiry();

    /** ISO-4217 currency code drawn from a curated list of widely used currencies. */
    String currencyCode();

    /** Random money string in the given currency, formatted like {@code $1,234.56}. */
    String money(String currencyCode);

    /** Random money in a random currency. */
    String money();

    /** A structurally-valid IBAN (correct length and check digits) for a random supported country. */
    String iban();

    /**
     * A structurally-valid IBAN for the given country code.
     * Length matches the official spec and the mod-97 check digits are computed.
     */
    String iban(String countryCode);
}
