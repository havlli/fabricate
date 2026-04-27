@org.jspecify.annotations.NullMarked
module org.fabricate {
    requires static org.jspecify;

    exports org.fabricate;
    exports org.fabricate.locale;
    exports org.fabricate.model;
    exports org.fabricate.provider;
    exports org.fabricate.random;
    exports org.fabricate.spi;

    uses org.fabricate.spi.LocaleProvider;

    provides org.fabricate.spi.LocaleProvider with
            org.fabricate.locale.EnglishLocaleProvider,
            org.fabricate.locale.ChineseLocaleProvider;
}
