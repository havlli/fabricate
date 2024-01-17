package com.github.havlli.store;

import com.github.havlli.store.repository.EnglishResourceRepository;

import java.util.Locale;

public class LocaleStoreFactory {
    public LocaleStore getLocaleStore(Locale locale) {
        if (locale.equals(Locale.ENGLISH)) {
            return new LocaleStore(EnglishLocaleConstraint, EnglishResourceRepository);
        } else if (locale.equals(Locale.CHINESE)) {

        } else {
            throw new UnsupportedOperationException();
        }
    }
}
