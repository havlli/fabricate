package org.fabricate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TemplatesTest {

    @Test
    void expand_substitutesKnownTokens() {
        Fabricate fab = Fabricate.builder().seed(42L).build();

        String result = fab.expand("Hi {firstName} <{email}>");

        assertThat(result).startsWith("Hi ");
        assertThat(result).contains(" <");
        assertThat(result).endsWith(">");
        assertThat(result).matches("Hi [^<]+ <[^>]+@[^>]+>");
    }

    @Test
    void expand_logLineLikeFormat() {
        Fabricate fab = Fabricate.builder().seed(7L).build();

        String line = fab.expand("{ipv4} {httpMethod} {url} {httpStatus}");

        assertThat(line).matches("\\d+\\.\\d+\\.\\d+\\.\\d+ [A-Z]+ https?://\\S+ \\d{3}");
    }

    @Test
    void expand_withCustomToken() {
        Fabricate fab = Fabricate.builder().seed(1L).build();

        String s = fab.templates()
                .with("orderId", () -> "ORD-42")
                .expand("[{orderId}] {firstName}");

        assertThat(s).startsWith("[ORD-42] ");
    }

    @Test
    void expand_withSameSeed_isDeterministic() {
        String a = Fabricate.builder().seed(99L).build().expand("{firstName}/{email}/{ipv4}");
        String b = Fabricate.builder().seed(99L).build().expand("{firstName}/{email}/{ipv4}");

        assertThat(a).isEqualTo(b);
    }

    @Test
    void expand_unknownToken_throws() {
        Fabricate fab = Fabricate.builder().seed(0L).build();

        assertThatThrownBy(() -> fab.expand("{nopeNotReal}"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nopeNotReal");
    }

    @Test
    void expand_unterminatedToken_throws() {
        Fabricate fab = Fabricate.builder().seed(0L).build();

        assertThatThrownBy(() -> fab.expand("hello {firstName"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unterminated");
    }

    @Test
    void expand_doubledBraces_areLiteral() {
        Fabricate fab = Fabricate.builder().seed(0L).build();

        assertThat(fab.expand("{{not a token}}")).isEqualTo("{not a token}");
        assertThat(fab.expand("a {{x}} b {firstName}")).startsWith("a {x} b ");
    }

    @Test
    void expand_emptyString_returnsEmpty() {
        Fabricate fab = Fabricate.builder().seed(0L).build();

        assertThat(fab.expand("")).isEmpty();
    }

    @Test
    void expand_noTokens_returnsLiteral() {
        Fabricate fab = Fabricate.builder().seed(0L).build();

        assertThat(fab.expand("just a plain string")).isEqualTo("just a plain string");
    }

    @Test
    void with_doesNotMutateOriginal() {
        Fabricate fab = Fabricate.builder().seed(0L).build();
        Templates extended = fab.templates().with("color", () -> "red");

        assertThat(extended.knownTokens()).contains("color");
        assertThat(fab.templates().knownTokens()).doesNotContain("color");
    }

    @Test
    void knownTokens_containsCommonNames() {
        Fabricate fab = Fabricate.builder().seed(0L).build();

        assertThat(fab.templates().knownTokens())
                .contains("firstName", "email", "ipv4", "url", "uuid",
                        "creditCard", "iban", "semver", "gitSha", "latitude");
    }
}
