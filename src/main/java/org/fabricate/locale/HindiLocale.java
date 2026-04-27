package org.fabricate.locale;

import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.fabricate.spi.LocaleData;

/** Hindi locale — Devanagari script. */
public final class HindiLocale implements LocaleData {

    public static final HindiLocale INSTANCE = new HindiLocale();

    private static final Locale LOCALE = Locale.of("hi");

    private static final List<String> FIRST_NAMES = List.of(
            "आर्यन", "विवान", "आदित्य", "अर्जुन", "रोहन", "ऋषभ", "करण", "विकास",
            "अनिल", "राहुल", "अमित", "सूर्य", "विशाल", "सिद्धार्थ", "नितिन", "मनोज",
            "आन्या", "दिव्या", "प्रिया", "नेहा", "सान्या", "रिया", "मीरा", "सीता",
            "ईशा", "अनन्या", "तनवी", "अदिति", "श्रद्धा", "लक्ष्मी", "पूजा", "काजल");

    private static final List<String> LAST_NAMES = List.of(
            "शर्मा", "वर्मा", "सिंह", "गुप्ता", "कुमार", "यादव", "पटेल", "मिश्रा",
            "जोशी", "अग्रवाल", "मेहता", "तिवारी", "पाण्डे", "त्रिपाठी", "देसाई",
            "नायर", "रेड्डी", "मेनन", "अय्यर", "बंसल", "चौधरी", "कपूर",
            "मल्होत्रा", "खन्ना", "सक्सेना", "श्रीवास्तव", "चटर्जी", "बनर्जी",
            "मुखर्जी", "सेन", "बोस", "दत्ता");

    private static final List<String> EMAIL_DOMAINS = List.of(
            "gmail.com", "yahoo.in", "rediffmail.com", "hotmail.com", "outlook.in",
            "live.in", "yahoo.co.in", "in.com");

    private static final List<String> PHONE_COUNTRY_CODES = List.of("+91");

    private static final List<String> STREETS = List.of(
            "मुख्य मार्ग", "गांधी मार्ग", "नेहरू मार्ग", "सरदार पटेल मार्ग",
            "महात्मा गांधी रोड", "स्टेशन रोड", "बाजार मार्ग", "मंदिर मार्ग",
            "स्कूल मार्ग", "पार्क रोड", "कॉलेज रोड", "तिलक मार्ग",
            "अशोक मार्ग", "विवेकानंद मार्ग", "रवींद्रनाथ टैगोर रोड", "जनकपुरी मार्ग");

    private static final List<String> CITIES = List.of(
            "दिल्ली", "मुंबई", "बेंगलुरु", "कोलकाता", "चेन्नई", "हैदराबाद",
            "अहमदाबाद", "पुणे", "जयपुर", "लखनऊ", "कानपुर", "नागपुर",
            "इंदौर", "भोपाल", "पटना", "वाराणसी", "अमृतसर", "चंडीगढ़",
            "सूरत", "रांची", "रायपुर", "गुवाहाटी");

    private static final List<String> STATES = List.of(
            "महाराष्ट्र", "उत्तर प्रदेश", "बिहार", "पश्चिम बंगाल", "मध्य प्रदेश",
            "तमिलनाडु", "राजस्थान", "कर्नाटक", "गुजरात", "आंध्र प्रदेश",
            "ओडिशा", "तेलंगाना", "केरल", "झारखंड", "असम", "पंजाब",
            "हरियाणा", "छत्तीसगढ़", "उत्तराखंड", "हिमाचल प्रदेश", "गोवा");

    private static final List<String> POSTAL_CODES = List.of(
            "110001", "400001", "560001", "700001", "600001", "500001",
            "380001", "411001", "302001", "226001", "208001", "440001",
            "452001", "462001", "800001", "221001");

    private static final List<String> COUNTRIES = List.of(
            "भारत", "नेपाल", "श्रीलंका", "बांग्लादेश", "पाकिस्तान",
            "भूटान", "म्यांमार", "मालदीव",
            "अमेरिका", "इंग्लैंड", "कनाडा", "ऑस्ट्रेलिया", "जर्मनी",
            "फ्रांस", "जापान", "चीन", "रूस");

    private static final List<String> JOB_TITLES = List.of(
            "सॉफ्टवेयर इंजीनियर", "परियोजना प्रबंधक", "मुख्य कार्यकारी अधिकारी",
            "विपणन निदेशक", "विक्रय निदेशक", "लेखाकार", "अधिवक्ता", "चिकित्सक",
            "नर्स", "शिक्षक", "वास्तुकार", "ग्राफिक डिजाइनर", "पत्रकार",
            "अनुवादक", "वैज्ञानिक", "डेटा वैज्ञानिक", "वित्तीय विश्लेषक",
            "सलाहकार", "उत्पाद प्रबंधक", "लेखा परीक्षक", "कर सलाहकार", "इंजीनियर");

    private static final UnaryOperator<String> EMAIL_LOCAL_PART = s ->
            s.codePoints().mapToObj(Integer::toString).collect(Collectors.joining());

    private HindiLocale() {}

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
