package com.github.havlli.store.repository;

public class EnglishResourceRepository implements ResourceRepository {
    private final String[] firstNames = {
            "Sofia",
            "Charlotte",
            "Victoria",
            "Noah",
            "Mia",
            "Sophia",
            "Daniel",
            "Liam",
            "Olivia",
            "Ava",
            "Chloe",
            "Ethan",
            "William",
            "Logan",
            "Joseph",
            "Riley",
            "Benjamin",
            "Sebastian",
            "Madison",
            "Matthew",
            "Henry",
            "Emma",
            "Mason",
            "Alexander",
            "David",
            "Emily",
            "Scarlett",
            "Penelope",
            "Luna",
            "Evelyn",
            "Jackson",
            "Michael",
            "Ella",
            "Grace",
            "James"
    };

    private final String[] lastNames = {
            "Hernandez",
            "Campbell",
            "Jackson",
            "Williams",
            "Moore",
            "Nelson",
            "Lopez",
            "Brown",
            "Young",
            "Anderson",
            "Scott",
            "Lee",
            "Davis",
            "Wright",
            "Thomas",
            "Ramirez",
            "Wilson",
            "Adams",
            "Martinez",
            "Hill",
            "Baker",
            "Green",
            "Perez",
            "Robinson",
            "Thompson",
            "Gonzalez",
            "Mitchell",
            "Lewis",
            "King",
            "Allen",
            "Jones",
            "Carter",
            "Taylor",
            "Johnson",
            "Garcia"
    };

    String[] emailDomains = {
            "example.com",
            "mail.com",
            "inbox.com",
            "email.com",
            "post.com",
            "world.com",
            "myplace.com",
            "mymail.com",
            "ourmail.com",
            "cybermail.com"
    };

    String[] regionPhoneNumbers = {
            "+44", "+49", "+33", "+7", "+39", "+34", "+46", "+47", "+31", "+48", "+32", "+43", "+45", "+358", "+351", "+420", "+41", "+380", "+375"
    };

    String[] streets = {
            "Main St", "Elm St", "Oak St", "Maple Ave", "Pine St",
            "Cedar St", "Walnut St", "Sunset Blvd", "Lincoln Ave", "Washington St",
            "Park Ave", "5th Ave", "Broadway", "High St", "Market St",
            "King St", "Queen St", "Prince St", "Duke St", "Baker St",
            "Oxford St", "Regent St", "Victoria St", "Albert St", "Piccadilly",
            "Fleet St", "Strand", "Champs-Élysées", "Via Veneto", "La Rambla"
    };

    String[] cities = {
            "New York", "Los Angeles", "Chicago", "Houston", "Phoenix",
            "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose",
            "London", "Paris", "Berlin", "Rome", "Madrid",
            "Barcelona", "Amsterdam", "Prague", "Vienna", "Munich",
            "Dublin", "Brussels", "Stockholm", "Copenhagen", "Oslo",
            "Helsinki", "Warsaw", "Budapest", "Athens", "Lisbon"
    };

    String[] postalCodes = {
            "10001", "90001", "60601", "77001", "85001",
            "19101", "78201", "92101", "75201", "95101",
            "SW1A 1AA", "75001", "10115", "00118", "28001",
            "08001", "1011", "110 00", "1010", "80331",
            "D01 F5P2", "1000", "111 20", "1050", "00100",
            "00-001", "1011", "10431", "1100"
    };

    String[] countries = {
            "United States", "Canada", "Mexico",
            "United Kingdom", "France", "Germany", "Italy", "Spain",
            "Portugal", "Netherlands", "Belgium", "Switzerland",
            "Sweden", "Norway", "Denmark", "Finland",
            "Poland", "Czech Republic", "Austria", "Hungary",
            "Greece", "Ireland", "Romania", "Bulgaria",
            "Croatia", "Slovakia", "Slovenia", "Estonia",
            "Latvia", "Lithuania"
    };

    String[] states = {
            "CA", "TX", "NY", "FL", "IL",
            "PA", "OH", "GA", "NC", "MI",
            "ON", "QC", "BC", "AB", "MB",
            "DE", "FR", "IT", "ES", "GB",
            "NL", "BE", "SE", "NO", "DK",
            "FI", "PL", "CZ", "AT", "HU",
            "GR", "IE", "RO", "BG", "HR",
            "SK", "SI", "EE", "LV", "LT"
    };

    @Override
    public String[] getFirstNames() {
        return firstNames;
    }

    @Override
    public String[] getLastNames() {
        return lastNames;
    }

    @Override
    public String[] getEmailDomains() {
        return emailDomains;
    }

    @Override
    public String[] getRegionPhoneNumbers() {
        return regionPhoneNumbers;
    }

    @Override
    public String[] getStreets() {
        return streets;
    }

    @Override
    public String[] getCities() {
        return cities;
    }

    @Override
    public String[] getCountries() {
        return countries;
    }

    @Override
    public String[] getStates() {
        return states;
    }

    @Override
    public String[] getPostalCodes() {
        return postalCodes;
    }
}
