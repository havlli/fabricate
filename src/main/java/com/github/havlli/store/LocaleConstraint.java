package com.github.havlli.store;

import java.util.function.Function;

public interface LocaleConstraint {
    String getNameDelimiter();
    Function<String, String> getEmailLocalPart();
}
