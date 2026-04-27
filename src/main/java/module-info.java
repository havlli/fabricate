@org.jspecify.annotations.NullMarked
module org.fabricate {
    requires static transitive org.jspecify;
    requires static transitive org.junit.jupiter.api;

    exports org.fabricate;
    exports org.fabricate.junit;
    exports org.fabricate.locale;
    exports org.fabricate.model;
    exports org.fabricate.provider;
    exports org.fabricate.random;
    exports org.fabricate.sink;
    exports org.fabricate.spi;

    uses org.fabricate.spi.LocaleProvider;

    provides org.fabricate.spi.LocaleProvider with
            org.fabricate.locale.EnglishLocaleProvider,
            org.fabricate.locale.ChineseLocaleProvider,
            org.fabricate.locale.RussianLocaleProvider,
            org.fabricate.locale.JapaneseLocaleProvider,
            org.fabricate.locale.KoreanLocaleProvider,
            org.fabricate.locale.ArabicLocaleProvider,
            org.fabricate.locale.GermanLocaleProvider;
}
