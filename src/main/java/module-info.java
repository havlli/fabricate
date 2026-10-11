@org.jspecify.annotations.NullMarked
module dev.havlli.fabricate {
    requires static transitive org.jspecify;
    requires static transitive org.junit.jupiter.api;

    exports dev.havlli.fabricate;
    exports dev.havlli.fabricate.junit;
    exports dev.havlli.fabricate.locale;
    exports dev.havlli.fabricate.model;
    exports dev.havlli.fabricate.provider;
    exports dev.havlli.fabricate.random;
    exports dev.havlli.fabricate.sink;
    exports dev.havlli.fabricate.spi;

    uses dev.havlli.fabricate.spi.LocaleProvider;

    provides dev.havlli.fabricate.spi.LocaleProvider with
            dev.havlli.fabricate.locale.EnglishLocaleProvider,
            dev.havlli.fabricate.locale.ChineseLocaleProvider,
            dev.havlli.fabricate.locale.RussianLocaleProvider,
            dev.havlli.fabricate.locale.JapaneseLocaleProvider,
            dev.havlli.fabricate.locale.KoreanLocaleProvider,
            dev.havlli.fabricate.locale.ArabicLocaleProvider,
            dev.havlli.fabricate.locale.GermanLocaleProvider,
            dev.havlli.fabricate.locale.SpanishLocaleProvider,
            dev.havlli.fabricate.locale.FrenchLocaleProvider,
            dev.havlli.fabricate.locale.PortugueseLocaleProvider,
            dev.havlli.fabricate.locale.HindiLocaleProvider;
}
