package org.fabricate.locale;

import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import org.fabricate.spi.LocaleData;

public final class EnglishLocale implements LocaleData {

    public static final EnglishLocale INSTANCE = new EnglishLocale();

    private static final List<String> FIRST_NAMES = List.of(
            "Sofia", "Charlotte", "Victoria", "Noah", "Mia",
            "Sophia", "Daniel", "Liam", "Olivia", "Ava",
            "Chloe", "Ethan", "William", "Logan", "Joseph",
            "Riley", "Benjamin", "Sebastian", "Madison", "Matthew",
            "Henry", "Emma", "Mason", "Alexander", "David",
            "Emily", "Scarlett", "Penelope", "Luna", "Evelyn",
            "Jackson", "Michael", "Ella", "Grace", "James"
    );

    private static final List<String> LAST_NAMES = List.of(
            "Hernandez", "Campbell", "Jackson", "Williams", "Moore",
            "Nelson", "Lopez", "Brown", "Young", "Anderson",
            "Scott", "Lee", "Davis", "Wright", "Thomas",
            "Ramirez", "Wilson", "Adams", "Martinez", "Hill",
            "Baker", "Green", "Perez", "Robinson", "Thompson",
            "Gonzalez", "Mitchell", "Lewis", "King", "Allen",
            "Jones", "Carter", "Taylor", "Johnson", "Garcia"
    );

    private static final List<String> EMAIL_DOMAINS = List.of(
            "example.com", "mail.com", "inbox.com", "email.com", "post.com",
            "world.com", "myplace.com", "mymail.com", "ourmail.com", "cybermail.com"
    );

    private static final List<String> PHONE_COUNTRY_CODES = List.of(
            "+44", "+49", "+33", "+7", "+39", "+34", "+46", "+47", "+31", "+48",
            "+32", "+43", "+45", "+358", "+351", "+420", "+41", "+380", "+375"
    );

    private static final List<String> STREETS = List.of(
            "Main St", "Elm St", "Oak St", "Maple Ave", "Pine St",
            "Cedar St", "Walnut St", "Sunset Blvd", "Lincoln Ave", "Washington St",
            "Park Ave", "5th Ave", "Broadway", "High St", "Market St",
            "King St", "Queen St", "Prince St", "Duke St", "Baker St",
            "Oxford St", "Regent St", "Victoria St", "Albert St", "Piccadilly",
            "Fleet St", "Strand", "Champs-Élysées", "Via Veneto", "La Rambla"
    );

    private static final List<String> CITIES = List.of(
            "New York", "Los Angeles", "Chicago", "Houston", "Phoenix",
            "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose",
            "London", "Paris", "Berlin", "Rome", "Madrid",
            "Barcelona", "Amsterdam", "Prague", "Vienna", "Munich",
            "Dublin", "Brussels", "Stockholm", "Copenhagen", "Oslo",
            "Helsinki", "Warsaw", "Budapest", "Athens", "Lisbon"
    );

    private static final List<String> POSTAL_CODES = List.of(
            "10001", "90001", "60601", "77001", "85001",
            "19101", "78201", "92101", "75201", "95101",
            "SW1A 1AA", "75001", "10115", "00118", "28001",
            "08001", "1011", "110 00", "1010", "80331",
            "D01 F5P2", "1000", "111 20", "1050", "00100",
            "00-001", "1011", "10431", "1100"
    );

    private static final List<String> COUNTRIES = List.of(
            "United States", "Canada", "Mexico",
            "United Kingdom", "France", "Germany", "Italy", "Spain",
            "Portugal", "Netherlands", "Belgium", "Switzerland",
            "Sweden", "Norway", "Denmark", "Finland",
            "Poland", "Czech Republic", "Austria", "Hungary",
            "Greece", "Ireland", "Romania", "Bulgaria",
            "Croatia", "Slovakia", "Slovenia", "Estonia",
            "Latvia", "Lithuania"
    );

    private static final List<String> STATES = List.of(
            "CA", "TX", "NY", "FL", "IL",
            "PA", "OH", "GA", "NC", "MI",
            "ON", "QC", "BC", "AB", "MB",
            "DE", "FR", "IT", "ES", "GB",
            "NL", "BE", "SE", "NO", "DK",
            "FI", "PL", "CZ", "AT", "HU",
            "GR", "IE", "RO", "BG", "HR",
            "SK", "SI", "EE", "LV", "LT"
    );

    private static final List<String> JOB_TITLES = List.of(
            "Software Developer", "Marketing Manager", "Financial Analyst", "Human Resources Coordinator",
            "Data Scientist", "Operations Manager", "Customer Service Representative", "Sales Executive",
            "Project Manager", "Graphic Designer", "Web Developer", "IT Support Specialist",
            "Content Writer", "UX/UI Designer", "Business Analyst", "Environmental Consultant",
            "Public Relations Specialist", "Supply Chain Analyst", "Healthcare Administrator", "Social Media Manager",
            "Civil Engineer", "Product Manager", "Quality Assurance Engineer", "Investment Banker",
            "Network Engineer", "Digital Marketing Specialist", "SEO Strategist", "Pharmaceutical Sales Representative",
            "Industrial Designer", "Logistics Coordinator", "Architectural Engineer", "Event Planner",
            "Software QA Tester", "Legal Assistant", "Pediatric Nurse Practitioner", "Construction Project Manager",
            "Art Director", "Compliance Officer", "Real Estate Agent", "Cloud Solutions Architect",
            "Cybersecurity Analyst", "Biotech Research Scientist", "AI Developer", "Veterinary Technician",
            "Mechanical Engineer", "Electrical Engineer", "Nutritionist", "Occupational Therapist",
            "Speech Language Pathologist", "Aerospace Engineer"
    );

    private EnglishLocale() {}

    @Override public Locale locale() { return Locale.ENGLISH; }
    @Override public String nameDelimiter() { return " "; }
    @Override public UnaryOperator<String> emailLocalPartTransform() { return String::toLowerCase; }
    @Override public List<String> firstNames() { return FIRST_NAMES; }
    @Override public List<String> lastNames() { return LAST_NAMES; }
    @Override public List<String> emailDomains() { return EMAIL_DOMAINS; }
    @Override public List<String> phoneCountryCodes() { return PHONE_COUNTRY_CODES; }
    @Override public List<String> streets() { return STREETS; }
    @Override public List<String> cities() { return CITIES; }
    @Override public List<String> states() { return STATES; }
    @Override public List<String> postalCodes() { return POSTAL_CODES; }
    @Override public List<String> countries() { return COUNTRIES; }
    @Override public List<String> jobTitles() { return JOB_TITLES; }
}
