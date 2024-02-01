package com.github.havlli.store.repository;

public interface ResourceRepository {
    String[] getFirstNames();
    String[] getLastNames();
    String[] getEmailDomains();
    String[] getRegionPhoneNumbers();
    String[] getStreets();
    String[] getCities();
    String[] getCountries();
    String[] getStates();
    String[] getPostalCodes();
    String[] getJobTitles();
}
