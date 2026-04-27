package org.fabricate.provider;

import java.util.List;
import java.util.Locale;
import org.fabricate.random.Rng;

/**
 * Commerce primitives: product names, SKUs, ISBNs, EAN-13 barcodes, colors, departments.
 *
 * {@snippet :
 * String name = fab.commerce().productName();   // "Handcrafted Cotton Lamp"
 * String sku  = fab.commerce().sku();           // "HND-1284-ZX"
 * String isbn = fab.commerce().isbn13();        // "978-3-16-148410-0"
 * String color = fab.commerce().colorName();    // "teal"
 * }
 */
public final class Commerce {

    private static final List<String> ADJECTIVES = List.of(
            "Handcrafted", "Refined", "Rustic", "Sleek", "Awesome", "Generic", "Practical",
            "Ergonomic", "Intelligent", "Gorgeous", "Incredible", "Fantastic", "Modern",
            "Vintage", "Luxurious", "Compact", "Premium", "Eco-friendly", "Wireless",
            "Silent", "Heavy-duty", "Lightweight");

    private static final List<String> MATERIALS = List.of(
            "Cotton", "Steel", "Wooden", "Leather", "Plastic", "Granite", "Rubber",
            "Concrete", "Ceramic", "Bronze", "Aluminum", "Silk", "Linen", "Bamboo",
            "Carbon-fiber", "Titanium", "Glass", "Marble");

    private static final List<String> PRODUCTS = List.of(
            "Chair", "Lamp", "Desk", "Bottle", "Backpack", "Watch", "Headphones", "Mouse",
            "Keyboard", "Monitor", "Camera", "Speaker", "Router", "Phone", "Tablet",
            "Wallet", "Mug", "Notebook", "Bicycle", "Helmet", "Jacket", "Shoes", "Sofa",
            "Table", "Pen", "Bag", "Pillow", "Blanket", "Knife", "Pan");

    private static final List<String> DEPARTMENTS = List.of(
            "Electronics", "Books", "Clothing", "Home & Kitchen", "Toys & Games",
            "Sports & Outdoors", "Health & Beauty", "Automotive", "Garden", "Grocery",
            "Music", "Movies & TV", "Pet Supplies", "Office Products", "Industrial",
            "Tools & Hardware", "Baby", "Jewelry");

    private static final List<String> COLOR_NAMES = List.of(
            "red", "crimson", "scarlet", "maroon",
            "orange", "amber", "coral",
            "yellow", "gold", "mustard",
            "green", "lime", "olive", "teal", "mint",
            "blue", "navy", "azure", "cyan", "indigo",
            "purple", "violet", "magenta", "lavender",
            "pink", "salmon", "rose",
            "brown", "tan", "beige", "chocolate",
            "black", "white", "gray", "silver");

    private final Rng rng;
    private final Numbers numbers;

    public Commerce(Rng rng) {
        this.rng = rng;
        this.numbers = new Numbers(rng);
    }

    /** A human-readable product name like {@code "Handcrafted Cotton Chair"}. */
    public String productName() {
        return rng.pick(ADJECTIVES) + " " + rng.pick(MATERIALS) + " " + rng.pick(PRODUCTS);
    }

    /** A retail department label. */
    public String department() {
        return rng.pick(DEPARTMENTS);
    }

    /** A SKU like {@code HND-1284-ZX}: 3-letter prefix, 4-digit lot, 2-letter suffix. */
    public String sku() {
        return randomLetters(3) + "-" + String.format(Locale.ROOT, "%04d", numbers.intBetween(0, 9999))
                + "-" + randomLetters(2);
    }

    /** A 13-digit barcode (EAN-13) with a valid GS1 check digit. */
    public String ean13() {
        StringBuilder digits = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            digits.append(numbers.intBetween(0, 9));
        }
        digits.append(ean13Check(digits.toString()));
        return digits.toString();
    }

    /** A 13-digit ISBN with a valid check digit, formatted with hyphens. */
    public String isbn13() {
        StringBuilder digits = new StringBuilder("978");
        for (int i = 0; i < 9; i++) {
            digits.append(numbers.intBetween(0, 9));
        }
        digits.append(ean13Check(digits.toString()));
        String s = digits.toString();
        return s.substring(0, 3) + "-" + s.charAt(3) + "-" + s.substring(4, 6) + "-"
                + s.substring(6, 12) + "-" + s.charAt(12);
    }

    /** A color name from a curated palette. */
    public String colorName() {
        return rng.pick(COLOR_NAMES);
    }

    /** A hex color code like {@code #3a8fe1}. */
    public String colorHex() {
        return String.format(Locale.ROOT, "#%06x", numbers.intBetween(0, 0xFFFFFF));
    }

    /** A {@code rgb(r, g, b)} CSS color. */
    public String colorRgb() {
        return "rgb(" + numbers.intBetween(0, 255) + ", " + numbers.intBetween(0, 255)
                + ", " + numbers.intBetween(0, 255) + ")";
    }

    private String randomLetters(int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append((char) ('A' + numbers.intBetween(0, 25)));
        }
        return sb.toString();
    }

    private static int ean13Check(String twelveDigits) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = twelveDigits.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : d * 3;
        }
        return (10 - (sum % 10)) % 10;
    }
}
