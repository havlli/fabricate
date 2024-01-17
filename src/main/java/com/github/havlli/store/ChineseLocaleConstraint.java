package com.github.havlli.store;

import java.util.function.Function;
import java.util.stream.Collectors;

public class ChineseLocaleConstraint implements LocaleConstraint {
    @Override
    public String getNameDelimiter() {
        return "";
    }

    @Override
    public Function<String, String> getEmailLocalPart() {
        return s -> s.chars()
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }
}
