package org.fabricate.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class PasswordsTest {

    @Test
    void password_defaultLength_is16() {
        Passwords passwords = new Passwords();

        assertThat(passwords.password()).hasSize(16);
    }

    @Test
    void password_customLength_isHonoured() {
        Passwords passwords = new Passwords();

        assertThat(passwords.password(8)).hasSize(8);
        assertThat(passwords.password(64)).hasSize(64);
    }

    @Test
    void password_customAlphabet_restrictsCharset() {
        Passwords passwords = new Passwords();

        String pw = passwords.password(50, "ab");

        assertThat(pw).matches("[ab]+");
    }

    @Test
    void password_invalidArgs_throw() {
        Passwords passwords = new Passwords();

        assertThatThrownBy(() -> passwords.password(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> passwords.password(8, ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
