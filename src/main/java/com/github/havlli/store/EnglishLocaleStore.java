package com.github.havlli.store;

import com.github.havlli.store.repository.EnglishResourceRepository;

import java.util.Locale;

public class EnglishLocaleStore extends LocaleStore {

    public EnglishLocaleStore() {
        super(new EnglishLocaleConstraint(), new EnglishResourceRepository());
    }
    @Override
    public Locale getLocale() {
        return Locale.ENGLISH;
    }

    @Override
    public String[] getFirstNames() {
        return firstNames;
    }

    @Override
    public String[] getLastNames() {
        return lastNames;
    }


}
