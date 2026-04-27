package org.fabricate.locale;

import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.fabricate.spi.LocaleData;

/** Russian locale — Cyrillic test data. */
public final class RussianLocale implements LocaleData {

    public static final RussianLocale INSTANCE = new RussianLocale();

    private static final Locale LOCALE = Locale.of("ru");

    private static final List<String> FIRST_NAMES = List.of(
            "Александр", "Дмитрий", "Максим", "Сергей", "Андрей",
            "Алексей", "Иван", "Михаил", "Никита", "Артём",
            "Анна", "Мария", "Елена", "Ольга", "Татьяна",
            "Наталья", "Ирина", "Светлана", "Екатерина", "Юлия",
            "Виктор", "Павел", "Роман", "Денис", "Игорь",
            "Дарья", "Полина", "Анастасия", "Кристина", "Алина");

    private static final List<String> LAST_NAMES = List.of(
            "Иванов", "Смирнов", "Кузнецов", "Попов", "Васильев",
            "Петров", "Соколов", "Михайлов", "Новиков", "Фёдоров",
            "Морозов", "Волков", "Алексеев", "Лебедев", "Семёнов",
            "Егоров", "Павлов", "Козлов", "Степанов", "Николаев",
            "Орлов", "Андреев", "Макаров", "Никитин", "Захаров",
            "Зайцев", "Соловьёв", "Борисов", "Яковлев", "Григорьев");

    private static final List<String> EMAIL_DOMAINS = List.of(
            "yandex.ru", "mail.ru", "rambler.ru", "list.ru", "bk.ru",
            "inbox.ru", "yandex.com", "ya.ru");

    private static final List<String> PHONE_COUNTRY_CODES = List.of("+7");

    private static final List<String> STREETS = List.of(
            "улица Ленина", "улица Пушкина", "улица Мира", "проспект Победы",
            "Тверская улица", "улица Гагарина", "улица Кирова", "улица Чехова",
            "Невский проспект", "улица Горького", "улица Маяковского",
            "улица Достоевского", "улица Толстого", "улица Чайковского",
            "Садовая улица", "улица Космонавтов", "улица Революции",
            "улица Советская", "улица Молодёжная", "улица Заречная");

    private static final List<String> CITIES = List.of(
            "Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург", "Казань",
            "Нижний Новгород", "Челябинск", "Самара", "Омск", "Ростов-на-Дону",
            "Уфа", "Красноярск", "Воронеж", "Пермь", "Волгоград",
            "Краснодар", "Саратов", "Тюмень", "Тольятти", "Ижевск");

    private static final List<String> STATES = List.of(
            "Московская область", "Ленинградская область", "Свердловская область",
            "Краснодарский край", "Новосибирская область", "Татарстан",
            "Башкортостан", "Челябинская область", "Самарская область",
            "Ростовская область", "Нижегородская область", "Ставропольский край",
            "Красноярский край", "Воронежская область", "Пермский край");

    private static final List<String> POSTAL_CODES = List.of(
            "101000", "190000", "630000", "620000", "420000",
            "603000", "454000", "443000", "644000", "344000",
            "450000", "660000", "394000", "614000", "400000");

    private static final List<String> COUNTRIES = List.of(
            "Россия", "Беларусь", "Казахстан", "Украина", "Узбекистан",
            "Киргизия", "Таджикистан", "Туркменистан", "Армения",
            "Азербайджан", "Грузия", "Молдавия", "Латвия", "Литва", "Эстония");

    private static final List<String> JOB_TITLES = List.of(
            "разработчик программного обеспечения", "руководитель проекта",
            "финансовый аналитик", "менеджер по продажам", "дизайнер",
            "системный администратор", "архитектор баз данных",
            "учёный по данным", "консультант", "юрист",
            "бухгалтер", "маркетолог", "директор", "инженер",
            "врач", "учитель", "переводчик", "редактор",
            "журналист", "архитектор");

    private static final UnaryOperator<String> EMAIL_LOCAL_PART = s ->
            s.codePoints().mapToObj(Integer::toString).collect(Collectors.joining());

    private RussianLocale() {}

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
