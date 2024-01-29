package com.github.havlli.store;

import com.github.havlli.store.repository.ResourceRepository;

public class LocaleStore {

    private final LocaleConstraint localeConstraint;
    private final ResourceRepository repository;

    protected LocaleStore(
            LocaleConstraint localeConstraint,
            ResourceRepository repository
    ) {
        this.localeConstraint = localeConstraint;
        this.repository = repository;
    }

    public String getNameDelimiter() {
        return localeConstraint.getNameDelimiter();
    }

    public String getEmailLocalPart(String value) {
        return localeConstraint.getEmailLocalPart()
                .apply(value);
    }

    public String[] getFirstNames() {
        return repository.getFirstNames();
    }

    public String[] getLastNames() {
        return repository.getLastNames();
    }

    public String[] getEmailDomains() {
        return repository.getEmailDomains();
    }

    public String[] getRegionPhoneNumbers() {
        return repository.getRegionPhoneNumbers();
    }
}
