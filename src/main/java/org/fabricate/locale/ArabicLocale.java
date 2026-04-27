package org.fabricate.locale;

import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.fabricate.spi.LocaleData;

/** Arabic locale — Arabic script (RTL) test data. */
public final class ArabicLocale implements LocaleData {

    public static final ArabicLocale INSTANCE = new ArabicLocale();

    private static final Locale LOCALE = Locale.of("ar");

    private static final List<String> FIRST_NAMES = List.of(
            "محمد", "أحمد", "علي", "حسن", "حسين",
            "عمر", "خالد", "يوسف", "إبراهيم", "عبدالله",
            "عبدالرحمن", "عبدالعزيز", "سعيد", "سعد", "ماجد",
            "فاطمة", "عائشة", "مريم", "زينب", "خديجة",
            "نور", "هدى", "ليلى", "سارة", "سلمى",
            "رنا", "رهف", "ندى", "هناء", "وفاء");

    private static final List<String> LAST_NAMES = List.of(
            "الحسن", "العلي", "المحمد", "السيد", "الإبراهيم",
            "الأحمد", "الخالد", "اليوسف", "العمر", "الحسين",
            "البكري", "الحموي", "الدمشقي", "البغدادي", "القاهري",
            "الفهد", "العتيبي", "الشمري", "القحطاني", "المطيري",
            "الرشيد", "الزهراني", "الغامدي", "البلوي", "الحربي");

    private static final List<String> EMAIL_DOMAINS = List.of(
            "yahoo.com", "gmail.com", "hotmail.com", "outlook.com",
            "maktoob.com", "stc.com.sa", "etisalat.ae", "orange.eg");

    private static final List<String> PHONE_COUNTRY_CODES = List.of(
            "+966", "+971", "+20", "+962", "+961", "+963", "+964", "+965", "+968", "+973", "+974");

    private static final List<String> STREETS = List.of(
            "شارع الملك فهد", "شارع الملك عبدالله", "شارع الملك فيصل",
            "شارع التحلية", "شارع العليا", "شارع التخصصي",
            "شارع المعز", "شارع الأزهر", "شارع رمسيس",
            "شارع الحمرا", "شارع بدارو", "شارع المتنبي",
            "شارع الحبيب بورقيبة", "شارع محمد الخامس", "شارع الاستقلال",
            "شارع الزهراء", "شارع النور", "شارع الأمل", "شارع السلام", "شارع الورود");

    private static final List<String> CITIES = List.of(
            "الرياض", "جدة", "مكة", "المدينة المنورة", "الدمام",
            "دبي", "أبوظبي", "الشارقة", "العين", "رأس الخيمة",
            "القاهرة", "الإسكندرية", "الجيزة", "أسيوط", "أسوان",
            "بيروت", "طرابلس", "صيدا", "بغداد", "البصرة",
            "عمان", "الزرقاء", "إربد", "الكويت", "الدوحة");

    private static final List<String> STATES = List.of(
            "منطقة الرياض", "منطقة مكة", "المنطقة الشرقية", "منطقة المدينة",
            "منطقة عسير", "إمارة دبي", "إمارة أبوظبي", "إمارة الشارقة",
            "محافظة القاهرة", "محافظة الجيزة", "محافظة الإسكندرية",
            "محافظة بيروت", "محافظة جبل لبنان", "محافظة عمان", "محافظة إربد");

    private static final List<String> POSTAL_CODES = List.of(
            "11564", "11321", "11431", "11543", "11623",
            "21411", "21421", "21442", "31411", "31432",
            "11211", "11311", "11421", "11521", "11611");

    private static final List<String> COUNTRIES = List.of(
            "المملكة العربية السعودية", "الإمارات العربية المتحدة", "مصر", "الأردن",
            "لبنان", "سوريا", "العراق", "الكويت", "قطر", "البحرين",
            "عمان", "اليمن", "فلسطين", "ليبيا", "تونس",
            "الجزائر", "المغرب", "السودان", "موريتانيا", "الصومال");

    private static final List<String> JOB_TITLES = List.of(
            "مهندس برمجيات", "مدير مشروع", "مصمم", "مدير مبيعات",
            "مسوق", "محاسب", "محامي", "طبيب", "ممرض",
            "معلم", "مهندس", "باحث", "محرر", "مترجم",
            "صحفي", "مهندس معماري", "كاتب", "عالم بيانات",
            "مهندس تعلم آلي", "مطور ويب", "مدير تنفيذي",
            "مدير موارد بشرية", "مستشار قانوني", "محلل مالي", "أخصائي تسويق");

    private static final UnaryOperator<String> EMAIL_LOCAL_PART = s ->
            s.codePoints().mapToObj(Integer::toString).collect(Collectors.joining());

    private ArabicLocale() {}

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
