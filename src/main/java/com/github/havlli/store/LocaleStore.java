package com.github.havlli;

import java.util.Locale;

public interface LocaleStore {
    Locale getLocale();
    String[] getFirstNames();
    String[] getLastNames();
}
