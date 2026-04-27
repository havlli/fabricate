package org.fabricate.locale;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import org.fabricate.spi.LocaleData;

/** Portuguese locale — Latin script with accents and ç; covers Portugal and Brazil. */
public final class PortugueseLocale implements LocaleData {

    public static final PortugueseLocale INSTANCE = new PortugueseLocale();

    private static final Locale LOCALE = Locale.of("pt");

    private static final List<String> FIRST_NAMES = List.of(
            "João", "Maria", "Pedro", "Ana", "Miguel", "Sofia", "Tiago", "Beatriz",
            "Rui", "Inês", "André", "Catarina", "Bruno", "Carolina", "Gonçalo", "Mariana",
            "Lucas", "Júlia", "Mateus", "Larissa", "Davi", "Manuela", "Felipe", "Helena",
            "Rafael", "Letícia", "Henrique", "Camila", "Vinícius", "Bárbara", "Bernardo", "Margarida");

    private static final List<String> LAST_NAMES = List.of(
            "Silva", "Santos", "Pereira", "Costa", "Rodrigues", "Ferreira", "Almeida", "Oliveira",
            "Sousa", "Carvalho", "Ribeiro", "Alves", "Marques", "Gomes", "Lopes", "Martins",
            "Cardoso", "Teixeira", "Fonseca", "Cunha", "Moreira", "Pinto", "Mendes", "Macedo",
            "Pacheco", "Vieira", "Cavalcanti", "Araújo", "Andrade", "Barros");

    private static final List<String> EMAIL_DOMAINS = List.of(
            "gmail.com", "hotmail.com", "yahoo.com.br", "outlook.com.br", "uol.com.br",
            "terra.com.br", "sapo.pt", "portugalmail.pt", "live.com.pt", "globo.com");

    private static final List<String> PHONE_COUNTRY_CODES = List.of(
            "+351", "+55", "+244", "+258", "+238");

    private static final List<String> STREETS = List.of(
            "Rua das Flores", "Avenida da Liberdade", "Rua Augusta", "Avenida Paulista",
            "Rua do Ouro", "Rua dos Clérigos", "Avenida Almirante Reis", "Rua Garrett",
            "Rua de Santa Catarina", "Avenida da República", "Rua Aurea", "Avenida Atlântica",
            "Rua Oscar Freire", "Rua das Laranjeiras", "Avenida Brasil", "Rua das Carmelitas",
            "Avenida Beira Mar", "Rua do Bonfim");

    private static final List<String> CITIES = List.of(
            "Lisboa", "Porto", "Braga", "Coimbra", "Aveiro", "Faro", "Setúbal",
            "Évora", "Funchal", "Ponta Delgada",
            "São Paulo", "Rio de Janeiro", "Salvador", "Brasília", "Fortaleza",
            "Belo Horizonte", "Manaus", "Curitiba", "Recife", "Porto Alegre",
            "Goiânia", "Belém", "Florianópolis");

    private static final List<String> STATES = List.of(
            "Lisboa", "Porto", "Braga", "Setúbal", "Aveiro", "Faro",
            "São Paulo", "Rio de Janeiro", "Bahia", "Minas Gerais", "Paraná",
            "Santa Catarina", "Rio Grande do Sul", "Pernambuco", "Ceará", "Pará",
            "Goiás", "Distrito Federal", "Amazonas");

    private static final List<String> POSTAL_CODES = List.of(
            "1000-001", "1100-148", "1200-035", "1300-456", "4000-123", "4050-203",
            "01310-100", "20040-020", "30130-110", "40020-010", "70040-010",
            "60035-110", "80020-300", "50030-220", "90020-002");

    private static final List<String> COUNTRIES = List.of(
            "Portugal", "Brasil", "Angola", "Moçambique", "Cabo Verde", "Guiné-Bissau",
            "São Tomé e Príncipe", "Timor-Leste", "Macau",
            "Espanha", "França", "Itália", "Alemanha", "Estados Unidos", "Reino Unido");

    private static final List<String> JOB_TITLES = List.of(
            "Engenheiro de Software", "Gestor de Projeto", "Diretor Executivo",
            "Diretor de Marketing", "Diretor Comercial", "Contador", "Advogado", "Médico",
            "Enfermeiro", "Professor", "Arquiteto", "Designer Gráfico", "Jornalista",
            "Tradutor", "Pesquisador", "Cientista de Dados", "Analista Financeiro", "Consultor",
            "Gerente de Produto", "Auditor", "Consultor Fiscal", "Engenheiro Civil");

    private static final UnaryOperator<String> EMAIL_LOCAL_PART = s ->
            stripDiacritics(s).toLowerCase(LOCALE);

    private static String stripDiacritics(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private PortugueseLocale() {}

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
