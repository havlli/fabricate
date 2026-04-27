package org.fabricate.locale;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import org.fabricate.spi.LocaleData;

/** Spanish locale — Latin script with accents and ñ; covers Spain plus major LatAm pools. */
public final class SpanishLocale implements LocaleData {

    public static final SpanishLocale INSTANCE = new SpanishLocale();

    private static final Locale LOCALE = Locale.of("es");

    private static final List<String> FIRST_NAMES = List.of(
            "Sofía", "Mateo", "Valentina", "Santiago", "Lucía", "Sebastián", "Camila", "Diego",
            "Isabella", "Daniel", "Martina", "Andrés", "Emma", "Tomás", "Victoria", "Julián",
            "María", "Carlos", "Ana", "Javier", "Carmen", "Pablo", "Elena", "Miguel",
            "Paula", "Adrián", "Lara", "Hugo", "Inés", "Álvaro", "Núria", "Iñaki");

    private static final List<String> LAST_NAMES = List.of(
            "García", "Rodríguez", "Martínez", "López", "González", "Pérez", "Sánchez", "Ramírez",
            "Cruz", "Flores", "Gómez", "Díaz", "Reyes", "Morales", "Jiménez", "Álvarez",
            "Hernández", "Torres", "Vargas", "Castillo", "Romero", "Suárez", "Navarro", "Domínguez",
            "Gutiérrez", "Ortiz", "Rubio", "Marín", "Sanz", "Iglesias");

    private static final List<String> EMAIL_DOMAINS = List.of(
            "gmail.com", "hotmail.com", "yahoo.es", "outlook.es", "telefonica.net",
            "movistar.es", "terra.com", "live.com.mx", "yahoo.com.mx");

    private static final List<String> PHONE_COUNTRY_CODES = List.of(
            "+34", "+52", "+54", "+57", "+56", "+51");

    private static final List<String> STREETS = List.of(
            "Calle Mayor", "Calle Real", "Avenida de la Constitución", "Plaza de España",
            "Calle del Sol", "Calle de Alcalá", "Paseo de Gracia", "Gran Vía",
            "Calle de Goya", "Calle de Bailén", "Avenida Reforma", "Calle Bolívar",
            "Calle San Martín", "Avenida 9 de Julio", "Calle Florida", "Avenida Insurgentes");

    private static final List<String> CITIES = List.of(
            "Madrid", "Barcelona", "Valencia", "Sevilla", "Zaragoza", "Málaga", "Bilbao",
            "Granada", "Salamanca", "Toledo", "Ciudad de México", "Guadalajara", "Monterrey",
            "Buenos Aires", "Córdoba", "Rosario", "Bogotá", "Medellín", "Santiago", "Lima",
            "Quito", "Caracas", "Montevideo");

    private static final List<String> STATES = List.of(
            "Madrid", "Cataluña", "Andalucía", "Comunidad Valenciana", "Galicia",
            "País Vasco", "Castilla y León", "Castilla-La Mancha", "Aragón", "Navarra",
            "Jalisco", "Nuevo León", "Buenos Aires", "Antioquia", "Cundinamarca");

    private static final List<String> POSTAL_CODES = List.of(
            "28001", "28013", "08001", "08025", "46001", "41001", "50001", "29001",
            "48001", "18001", "37001", "06800", "44600", "64000");

    private static final List<String> COUNTRIES = List.of(
            "España", "México", "Argentina", "Colombia", "Chile", "Perú", "Venezuela",
            "Ecuador", "Uruguay", "Paraguay", "Bolivia", "Cuba", "República Dominicana",
            "Guatemala", "Honduras", "El Salvador", "Costa Rica", "Panamá", "Puerto Rico",
            "Estados Unidos", "Francia", "Italia", "Portugal");

    private static final List<String> JOB_TITLES = List.of(
            "Ingeniero de Software", "Director de Proyecto", "Director Ejecutivo",
            "Jefe de Marketing", "Jefe de Ventas", "Contador", "Abogado", "Médico",
            "Enfermero", "Profesor", "Arquitecto", "Diseñador Gráfico", "Periodista",
            "Traductor", "Investigador", "Científico de Datos", "Analista Financiero",
            "Consultor", "Gerente de Producto", "Auditor", "Asesor Fiscal");

    private static final UnaryOperator<String> EMAIL_LOCAL_PART = s ->
            stripDiacritics(s).replace("ñ", "n").replace("Ñ", "N").toLowerCase(LOCALE);

    private static String stripDiacritics(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private SpanishLocale() {}

    @Override public Locale locale() { return LOCALE; }
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
