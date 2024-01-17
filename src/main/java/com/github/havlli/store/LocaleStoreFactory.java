package com.github.havlli.store;

import com.github.havlli.store.repository.ChineseResourceRepository;
import com.github.havlli.store.repository.EnglishResourceRepository;

import java.util.Locale;

public class LocaleStoreFactory {
    public static LocaleStore getLocaleStore(Locale locale) {
        if (locale.equals(Locale.ENGLISH)) {
            return new LocaleStore(new EnglishLocaleConstraint(), new EnglishResourceRepository());
        } else if (locale.equals(Locale.CHINESE)) {
            return new LocaleStore(new ChineseLocaleConstraint(), new ChineseResourceRepository());
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
