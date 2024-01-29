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
}
