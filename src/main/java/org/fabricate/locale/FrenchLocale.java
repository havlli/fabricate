package org.fabricate.locale;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import org.fabricate.spi.LocaleData;

/** French locale — Latin script with accents and ç. */
public final class FrenchLocale implements LocaleData {

    public static final FrenchLocale INSTANCE = new FrenchLocale();

    private static final List<String> FIRST_NAMES = List.of(
            "Léa", "Hugo", "Chloé", "Lucas", "Emma", "Louis", "Manon", "Théo",
            "Camille", "Antoine", "Margaux", "Maxime", "Sarah", "Nicolas", "Sophie", "Mathieu",
            "Élodie", "Jérôme", "Amélie", "Éric", "Céline", "François", "Hélène", "Étienne",
            "Aurélie", "Bastien", "Noémie", "Pierre", "Solène", "Quentin", "Inès", "Raphaël");

    private static final List<String> LAST_NAMES = List.of(
            "Martin", "Bernard", "Dubois", "Thomas", "Robert", "Petit", "Richard", "Durand",
            "Leroy", "Moreau", "Simon", "Laurent", "Lefebvre", "Michel", "Garcia", "David",
            "Bertrand", "Roux", "Vincent", "Fournier", "Morel", "Girard", "André", "Mercier",
            "Boyer", "Blanc", "Guérin", "Boucher", "Hubert", "Charpentier");

    private static final List<String> EMAIL_DOMAINS = List.of(
            "gmail.com", "orange.fr", "free.fr", "wanadoo.fr", "laposte.net",
            "sfr.fr", "yahoo.fr", "hotmail.fr", "outlook.fr");

    private static final List<String> PHONE_COUNTRY_CODES = List.of(
            "+33", "+32", "+41", "+352");

    private static final List<String> STREETS = List.of(
            "Rue de la Paix", "Avenue des Champs-Élysées", "Boulevard Saint-Germain",
            "Rue de Rivoli", "Place Vendôme", "Rue du Faubourg", "Avenue Foch",
            "Rue de la République", "Boulevard Voltaire", "Rue Lafayette", "Quai d'Orsay",
            "Cours Mirabeau", "Place de la Concorde", "Rue du Bac", "Avenue Montaigne",
            "Rue Saint-Honoré");

    private static final List<String> CITIES = List.of(
            "Paris", "Marseille", "Lyon", "Toulouse", "Nice", "Nantes", "Strasbourg",
            "Montpellier", "Bordeaux", "Lille", "Rennes", "Reims", "Le Havre", "Saint-Étienne",
            "Toulon", "Grenoble", "Dijon", "Angers", "Nîmes", "Villeurbanne",
            "Bruxelles", "Genève", "Lausanne", "Luxembourg", "Liège");

    private static final List<String> STATES = List.of(
            "Île-de-France", "Provence-Alpes-Côte d'Azur", "Auvergne-Rhône-Alpes",
            "Nouvelle-Aquitaine", "Occitanie", "Hauts-de-France", "Grand Est",
            "Pays de la Loire", "Bretagne", "Normandie", "Bourgogne-Franche-Comté",
            "Centre-Val de Loire", "Corse");

    private static final List<String> POSTAL_CODES = List.of(
            "75001", "75008", "13001", "69001", "31000", "06000", "44000",
            "67000", "34000", "33000", "59000", "35000", "21000", "1000", "1200");

    private static final List<String> COUNTRIES = List.of(
            "France", "Belgique", "Suisse", "Luxembourg", "Canada", "Maroc", "Tunisie",
            "Algérie", "Sénégal", "Côte d'Ivoire", "Cameroun", "Madagascar",
            "Allemagne", "Italie", "Espagne", "Portugal", "Royaume-Uni", "Pays-Bas");

    private static final List<String> JOB_TITLES = List.of(
            "Développeur Logiciel", "Chef de Projet", "Directeur Général",
            "Directeur Marketing", "Directeur Commercial", "Comptable", "Avocat", "Médecin",
            "Infirmier", "Enseignant", "Architecte", "Designer Graphique", "Journaliste",
            "Traducteur", "Chercheur", "Data Scientist", "Analyste Financier", "Consultant",
            "Chef de Produit", "Auditeur", "Conseiller Fiscal", "Ingénieur");

    private static final UnaryOperator<String> EMAIL_LOCAL_PART = s ->
            stripDiacritics(s).replace("œ", "oe").replace("Œ", "Oe")
                    .replace("æ", "ae").replace("Æ", "Ae")
                    .toLowerCase(Locale.FRENCH);

    private static String stripDiacritics(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private FrenchLocale() {}

    @Override public Locale locale() { return Locale.FRENCH; }
    @Override public String nameDelimiter() { return " "; }
    @Override public UnaryOperator<String> emailLocalPartTransform() { return EMAIL_LOCAL_PART; }
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
