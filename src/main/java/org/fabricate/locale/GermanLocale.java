package org.fabricate.locale;

import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import org.fabricate.spi.LocaleData;

/** German locale — Latin script with umlauts and ß. */
public final class GermanLocale implements LocaleData {

    public static final GermanLocale INSTANCE = new GermanLocale();

    private static final List<String> FIRST_NAMES = List.of(
            "Müller", "Lukas", "Jürgen", "Björn", "Günther",
            "Sören", "Hänsel", "Tobias", "Stefan", "Maximilian",
            "Sebastian", "Andreas", "Thomas", "Christoph", "Matthias",
            "Anna", "Lena", "Sophie", "Hannah", "Mia",
            "Lara", "Jana", "Sarah", "Marie", "Lina",
            "Lieselotte", "Käthe", "Ursula", "Renate", "Brünhilde");

    private static final List<String> LAST_NAMES = List.of(
            "Müller", "Schmidt", "Schneider", "Fischer", "Weber",
            "Meyer", "Wagner", "Becker", "Schulz", "Hoffmann",
            "Schäfer", "Koch", "Bauer", "Richter", "Klein",
            "Wolf", "Schröder", "Neumann", "Schwarz", "Zimmermann",
            "Braun", "Krüger", "Hofmann", "Hartmann", "Lange",
            "Schmitt", "Werner", "Schmitz", "Krause", "Meier");

    private static final List<String> EMAIL_DOMAINS = List.of(
            "gmx.de", "web.de", "t-online.de", "freenet.de", "1und1.de",
            "arcor.de", "online.de", "yahoo.de", "outlook.de", "gmail.com");

    private static final List<String> PHONE_COUNTRY_CODES = List.of(
            "+49", "+43", "+41");

    private static final List<String> STREETS = List.of(
            "Hauptstraße", "Schulstraße", "Bahnhofstraße", "Gartenstraße", "Dorfstraße",
            "Bergstraße", "Kirchstraße", "Waldstraße", "Lindenstraße", "Mühlenweg",
            "Goethestraße", "Schillerstraße", "Beethovenstraße", "Mozartstraße", "Wagnerweg",
            "Friedrichstraße", "Königsallee", "Maximilianstraße", "Kurfürstendamm", "Jägerstraße",
            "Säntisstraße", "Mönchengladbacher Straße", "Düsseldorfer Straße", "Kölner Platz");

    private static final List<String> CITIES = List.of(
            "Berlin", "Hamburg", "München", "Köln", "Frankfurt",
            "Stuttgart", "Düsseldorf", "Leipzig", "Dortmund", "Essen",
            "Bremen", "Dresden", "Hannover", "Nürnberg", "Würzburg",
            "Lübeck", "Saarbrücken", "Tübingen", "Göttingen", "Münster",
            "Wien", "Salzburg", "Innsbruck", "Zürich", "Basel");

    private static final List<String> STATES = List.of(
            "Bayern", "Baden-Württemberg", "Nordrhein-Westfalen", "Hessen",
            "Niedersachsen", "Sachsen", "Rheinland-Pfalz", "Berlin", "Hamburg",
            "Schleswig-Holstein", "Brandenburg", "Sachsen-Anhalt", "Thüringen",
            "Mecklenburg-Vorpommern", "Saarland", "Bremen");

    private static final List<String> POSTAL_CODES = List.of(
            "10115", "20095", "80331", "50667", "60311",
            "70173", "40213", "04109", "44135", "45127",
            "28195", "01067", "30159", "90402", "97070");

    private static final List<String> COUNTRIES = List.of(
            "Deutschland", "Österreich", "Schweiz", "Frankreich", "Italien",
            "Spanien", "Portugal", "Niederlande", "Belgien", "Luxemburg",
            "Polen", "Tschechien", "Slowakei", "Ungarn", "Dänemark",
            "Schweden", "Norwegen", "Finnland", "Großbritannien", "Irland");

    private static final List<String> JOB_TITLES = List.of(
            "Softwareentwickler", "Projektmanager", "Geschäftsführer",
            "Marketingleiter", "Vertriebsleiter", "Buchhalter", "Rechtsanwalt",
            "Arzt", "Krankenpfleger", "Lehrer", "Ingenieur", "Wissenschaftler",
            "Redakteur", "Übersetzer", "Dolmetscher", "Journalist", "Architekt",
            "Schriftsteller", "Datenwissenschaftler", "Maschinenbauingenieur",
            "Elektroingenieur", "Wirtschaftsprüfer", "Steuerberater",
            "Personalreferent", "Produktmanager", "Geschäftsanalyst",
            "Qualitätsprüfer", "Vermögensberater");

    private static final UnaryOperator<String> EMAIL_LOCAL_PART = s ->
            normalize(s).toLowerCase(Locale.ROOT);

    private static String normalize(String s) {
        return s
                .replace("ä", "ae").replace("Ä", "Ae")
                .replace("ö", "oe").replace("Ö", "Oe")
                .replace("ü", "ue").replace("Ü", "Ue")
                .replace("ß", "ss");
    }

    private GermanLocale() {}

    @Override public Locale locale() { return Locale.GERMAN; }
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
