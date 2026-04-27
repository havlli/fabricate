package org.fabricate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Tiny mustache-flavoured template expander backed by provider methods.
 *
 * {@snippet :
 * String greeting = fab.expand("Hi {firstName} <{email}>");
 * String log      = fab.expand("{ipv4} - {httpStatus} {method} {url}");
 * }
 *
 * Tokens are simple identifiers between curly braces; they map to a fixed
 * registry of well-known names (see {@link #knownTokens()}). Unknown tokens
 * raise an {@link IllegalArgumentException} so typos surface immediately.
 *
 * Use {@link #with(String, Supplier)} to register additional tokens for
 * project-specific data:
 *
 * {@snippet :
 * String s = fab.templates()
 *               .with("orderId", () -> "ORD-" + Sequence.counter(1).next())
 *               .expand("[{orderId}] {firstName}");
 * }
 *
 * Doubled braces escape: {@code "{{not a token}}"} expands to {@code "{not a token}"}.
 */
public final class Templates {

    private final Fabricate fab;
    private final Map<String, Supplier<?>> tokens;

    Templates(Fabricate fab) {
        this.fab = fab;
        this.tokens = defaultTokens(fab);
    }

    private Templates(Fabricate fab, Map<String, Supplier<?>> tokens) {
        this.fab = fab;
        this.tokens = tokens;
    }

    /**
     * Returns a new {@code Templates} with {@code name} bound to {@code supplier}.
     * Existing bindings are preserved; rebinding an existing token replaces it.
     */
    public Templates with(String name, Supplier<?> supplier) {
        Map<String, Supplier<?>> copy = new HashMap<>(tokens);
        copy.put(name, supplier);
        return new Templates(fab, copy);
    }

    /** The set of token names this expander recognises. */
    public java.util.Set<String> knownTokens() {
        return java.util.Collections.unmodifiableSet(tokens.keySet());
    }

    /** Expands every {@code {token}} in {@code template}. */
    public String expand(String template) {
        if (template == null) throw new IllegalArgumentException("template must not be null");
        StringBuilder out = new StringBuilder(template.length());
        int i = 0;
        int n = template.length();
        while (i < n) {
            char c = template.charAt(i);
            if (c == '{' && i + 1 < n && template.charAt(i + 1) == '{') {
                out.append('{');
                i += 2;
                continue;
            }
            if (c == '}' && i + 1 < n && template.charAt(i + 1) == '}') {
                out.append('}');
                i += 2;
                continue;
            }
            if (c == '{') {
                int end = template.indexOf('}', i + 1);
                if (end < 0) {
                    throw new IllegalArgumentException("Unterminated token starting at index " + i + ": " + template);
                }
                String name = template.substring(i + 1, end).trim();
                Supplier<?> supplier = tokens.get(name);
                if (supplier == null) {
                    throw new IllegalArgumentException("Unknown template token: {" + name + "}");
                }
                Object value = supplier.get();
                out.append(value == null ? "" : value.toString());
                i = end + 1;
                continue;
            }
            out.append(c);
            i++;
        }
        return out.toString();
    }

    private static Map<String, Supplier<?>> defaultTokens(Fabricate fab) {
        Map<String, Supplier<?>> m = new HashMap<>();
        m.put("firstName", () -> fab.names().firstName());
        m.put("lastName", () -> fab.names().lastName());
        m.put("fullName", () -> fab.names().fullName());
        m.put("email", () -> fab.emails().email());
        m.put("phone", () -> fab.phones().phoneNumber());
        m.put("city", () -> fab.addresses().city());
        m.put("country", () -> fab.addresses().country());
        m.put("jobTitle", () -> fab.jobTitles().jobTitle());
        m.put("uuid", () -> fab.identities().uuid());
        m.put("dateOfBirth", () -> fab.datesOfBirth().birthdate());

        m.put("word", () -> fab.texts().word());
        m.put("sentence", () -> fab.texts().sentence());
        m.put("slug", () -> fab.texts().slug());

        m.put("url", () -> fab.internet().url());
        m.put("urlWithQuery", () -> fab.internet().urlWithQuery());
        m.put("domain", () -> fab.internet().domain());
        m.put("ipv4", () -> fab.internet().ipv4());
        m.put("ipv6", () -> fab.internet().ipv6());
        m.put("macAddress", () -> fab.internet().macAddress());
        m.put("userAgent", () -> fab.internet().userAgent());
        m.put("port", () -> fab.internet().port());
        m.put("apiKey", () -> fab.internet().apiKey());
        m.put("bearerToken", () -> fab.internet().bearerToken());
        m.put("imageUrl", () -> fab.internet().imageUrl());
        m.put("username", () -> fab.internet().username());

        m.put("latitude", () -> fab.geo().latitude());
        m.put("longitude", () -> fab.geo().longitude());
        m.put("coordinate", () -> fab.geo().coordinate());
        m.put("timezone", () -> fab.geo().timezone());
        m.put("countryCode", () -> fab.geo().countryCodeIso2());
        m.put("countryCodeIso3", () -> fab.geo().countryCodeIso3());

        m.put("creditCard", () -> fab.finance().creditCard());
        m.put("iban", () -> fab.finance().iban());
        m.put("currencyCode", () -> fab.finance().currencyCode());
        m.put("money", () -> fab.finance().money());

        m.put("productName", () -> fab.commerce().productName());
        m.put("sku", () -> fab.commerce().sku());
        m.put("isbn13", () -> fab.commerce().isbn13());
        m.put("colorName", () -> fab.commerce().colorName());
        m.put("colorHex", () -> fab.commerce().colorHex());

        m.put("semver", () -> fab.devops().semver());
        m.put("gitSha", () -> fab.devops().gitSha());
        m.put("environment", () -> fab.devops().environment());
        m.put("logLevel", () -> fab.devops().logLevel());
        m.put("httpStatus", () -> fab.devops().httpStatus());
        m.put("httpMethod", () -> fab.devops().httpMethod());
        m.put("dockerImage", () -> fab.devops().dockerImage());

        m.put("fileName", () -> fab.files().fileName());
        m.put("filePath", () -> fab.files().path());
        m.put("mimeType", () -> fab.files().mimeType());
        return m;
    }
}
