package com.github.havlli.store;

import java.util.Locale;

public abstract class LocaleStore {

    private final LocaleConstraint localeConstraint;

    protected LocaleStore(LocaleConstraint localeConstraint) {
        this.localeConstraint = localeConstraint;
    }

    public abstract Locale getLocale();
    public abstract String[] getFirstNames();
    public abstract String[] getLastNames();

    public String getNameDelimiter() {
        return localeConstraint.getNameDelimiter();
    }
}
