package org.fabricate.provider;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.fabricate.random.Rng;

/**
 * Finance primitives: credit cards (Luhn-valid), CVVs, IBANs, currency codes, money.
 *
 * {@snippet :
 * String visa = fab.finance().creditCard("VISA");      // 4539-...
 * String iban = fab.finance().iban("DE");              // DE89 3704 ...
 * String mny  = fab.finance().money("USD");            // $1,234.56
 * String cur  = fab.finance().currencyCode();          // EUR
 * }
 */
public final class Finance {

    /** Issuer prefixes and total length for common card brands. */
    private record CardSpec(String brand, List<String> prefixes, int length) {}

    private static final List<CardSpec> CARD_SPECS = List.of(
            new CardSpec("VISA", List.of("4"), 16),
            new CardSpec("MASTERCARD",
                    List.of("51", "52", "53", "54", "55", "2221", "2222", "2720"), 16),
            new CardSpec("AMEX", List.of("34", "37"), 15),
            new CardSpec("DISCOVER", List.of("6011", "65"), 16),
            new CardSpec("JCB", List.of("3528", "3529", "3530", "3589"), 16),
            new CardSpec("DINERS", List.of("300", "301", "302", "303", "36", "38"), 14));

    private static final List<String> CURRENCIES = List.of(
            "USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "NZD", "SEK", "NOK",
            "DKK", "PLN", "CZK", "HUF", "CNY", "HKD", "SGD", "INR", "BRL", "MXN",
            "ZAR", "TRY", "RUB", "KRW", "AED", "ILS");

    private static final Map<String, String> SYMBOLS = Map.ofEntries(
            Map.entry("USD", "$"),
            Map.entry("EUR", "€"),
            Map.entry("GBP", "£"),
            Map.entry("JPY", "¥"),
            Map.entry("CNY", "¥"),
            Map.entry("KRW", "₩"),
            Map.entry("INR", "₹"),
            Map.entry("CHF", "Fr"),
            Map.entry("PLN", "zł"),
            Map.entry("RUB", "₽"));

    /** Country code -> total IBAN length. Covers the most common SEPA + EU + a few outliers. */
    private static final Map<String, Integer> IBAN_LENGTHS = Map.ofEntries(
            Map.entry("AD", 24), Map.entry("AT", 20), Map.entry("BE", 16), Map.entry("BG", 22),
            Map.entry("CH", 21), Map.entry("CY", 28), Map.entry("CZ", 24), Map.entry("DE", 22),
            Map.entry("DK", 18), Map.entry("EE", 20), Map.entry("ES", 24), Map.entry("FI", 18),
            Map.entry("FR", 27), Map.entry("GB", 22), Map.entry("GR", 27), Map.entry("HR", 21),
            Map.entry("HU", 28), Map.entry("IE", 22), Map.entry("IS", 26), Map.entry("IT", 27),
            Map.entry("LT", 20), Map.entry("LU", 20), Map.entry("LV", 21), Map.entry("MT", 31),
            Map.entry("NL", 18), Map.entry("NO", 15), Map.entry("PL", 28), Map.entry("PT", 25),
            Map.entry("RO", 24), Map.entry("SE", 24), Map.entry("SI", 19), Map.entry("SK", 24));

    private static final List<String> IBAN_COUNTRIES = List.copyOf(IBAN_LENGTHS.keySet());

    private final Rng rng;
    private final Numbers numbers;

    public Finance(Rng rng) {
        this.rng = rng;
        this.numbers = new Numbers(rng);
    }

    /** A Luhn-valid card number from a randomly chosen brand, formatted with hyphens. */
    public String creditCard() {
        return creditCard(rng.pick(CARD_SPECS).brand());
    }

    /** A Luhn-valid card number for the given brand (e.g. {@code VISA}, {@code AMEX}). */
    public String creditCard(String brand) {
        CardSpec spec = findBrand(brand);
        String prefix = rng.pick(spec.prefixes());
        StringBuilder digits = new StringBuilder(prefix);
        while (digits.length() < spec.length() - 1) {
            digits.append(numbers.intBetween(0, 9));
        }
        digits.append(luhnCheckDigit(digits.toString()));
        return formatCard(digits.toString());
    }

    /** Three-digit CVV, or four for AMEX-shaped requests. */
    public String cvv() {
        return String.format(Locale.ROOT, "%03d", numbers.intBetween(0, 999));
    }

    /** Four-digit CVV (AMEX). */
    public String cvv4() {
        return String.format(Locale.ROOT, "%04d", numbers.intBetween(0, 9999));
    }

    /** {@code MM/YY} expiry in the next five years. */
    public String cardExpiry() {
        int month = numbers.intBetween(1, 12);
        int year = (java.time.Year.now().getValue() + numbers.intBetween(0, 5)) % 100;
        return String.format(Locale.ROOT, "%02d/%02d", month, year);
    }

    /** ISO-4217 currency code drawn from a curated list of widely used currencies. */
    public String currencyCode() {
        return rng.pick(CURRENCIES);
    }

    /** Random money string in the given currency, formatted like {@code $1,234.56}. */
    public String money(String currencyCode) {
        long cents = numbers.longBetween(100, 9_999_99);
        long whole = cents / 100;
        long frac = cents % 100;
        String symbol = SYMBOLS.getOrDefault(currencyCode.toUpperCase(Locale.ROOT), currencyCode + " ");
        String wholeStr = String.format(Locale.US, "%,d", whole);
        return symbol + wholeStr + "." + String.format(Locale.ROOT, "%02d", frac);
    }

    /** Random money in a random currency. */
    public String money() {
        return money(currencyCode());
    }

    /** A structurally-valid IBAN (correct length and check digits) for a random supported country. */
    public String iban() {
        return iban(rng.pick(IBAN_COUNTRIES));
    }

    /**
     * A structurally-valid IBAN for the given country code.
     * Length matches the official spec and the mod-97 check digits are computed.
     */
    public String iban(String countryCode) {
        String cc = countryCode.toUpperCase(Locale.ROOT);
        Integer length = IBAN_LENGTHS.get(cc);
        if (length == null) {
            throw new IllegalArgumentException("Unsupported IBAN country: " + countryCode);
        }
        int bbanLen = length - 4;
        StringBuilder bban = new StringBuilder(bbanLen);
        for (int i = 0; i < bbanLen; i++) {
            bban.append(numbers.intBetween(0, 9));
        }
        String check = ibanCheckDigits(cc, bban.toString());
        return cc + check + bban;
    }

    private static CardSpec findBrand(String brand) {
        String upper = brand.toUpperCase(Locale.ROOT);
        for (CardSpec s : CARD_SPECS) {
            if (s.brand().equals(upper)) return s;
        }
        throw new IllegalArgumentException("Unknown card brand: " + brand);
    }

    private static int luhnCheckDigit(String partial) {
        int sum = 0;
        boolean doubleIt = true;
        for (int i = partial.length() - 1; i >= 0; i--) {
            int d = partial.charAt(i) - '0';
            if (doubleIt) {
                d *= 2;
                if (d > 9) d -= 9;
            }
            sum += d;
            doubleIt = !doubleIt;
        }
        return (10 - (sum % 10)) % 10;
    }

    private static String formatCard(String digits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digits.length(); i++) {
            if (i > 0 && i % 4 == 0) sb.append('-');
            sb.append(digits.charAt(i));
        }
        return sb.toString();
    }

    private static String ibanCheckDigits(String country, String bban) {
        // Move country + "00" to the end, convert letters to digits, compute mod 97.
        String rearranged = bban + country + "00";
        java.math.BigInteger n = new java.math.BigInteger(lettersToDigits(rearranged));
        int check = 98 - n.mod(java.math.BigInteger.valueOf(97)).intValue();
        return String.format(Locale.ROOT, "%02d", check);
    }

    private static String lettersToDigits(String s) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                sb.append(c);
            } else if (c >= 'A' && c <= 'Z') {
                sb.append(c - 'A' + 10);
            } else {
                throw new IllegalArgumentException("invalid IBAN char: " + c);
            }
        }
        return sb.toString();
    }
}
