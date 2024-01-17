package com.github.havlli.store;

import java.util.function.Function;

public class EnglishLocaleConstraint implements LocaleConstraint {
    @Override
    public String getNameDelimiter() {
        return " ";
    }

    @Override
    public Function<String, String> getEmailLocalPart() {
        return s -> s;
    }
}
