package com.github.havlli;

import java.util.Locale;

public class EnglishLocaleStore implements LocaleStore {
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
}
