@org.jspecify.annotations.NullMarked
module dev.havlli.fabricate {
    requires static transitive org.jspecify;
    requires static transitive org.junit.jupiter.api;

    exports dev.havlli.fabricate;
    exports dev.havlli.fabricate.junit;
    exports dev.havlli.fabricate.model;
    exports dev.havlli.fabricate.sink;
    exports dev.havlli.fabricate.spi;

    uses dev.havlli.fabricate.spi.LocaleProvider;

    provides dev.havlli.fabricate.spi.LocaleProvider with
            dev.havlli.fabricate.internal.locale.EnglishLocaleProvider,
            dev.havlli.fabricate.internal.locale.ChineseLocaleProvider,
            dev.havlli.fabricate.internal.locale.RussianLocaleProvider,
            dev.havlli.fabricate.internal.locale.JapaneseLocaleProvider,
            dev.havlli.fabricate.internal.locale.KoreanLocaleProvider,
            dev.havlli.fabricate.internal.locale.ArabicLocaleProvider,
            dev.havlli.fabricate.internal.locale.GermanLocaleProvider,
            dev.havlli.fabricate.internal.locale.SpanishLocaleProvider,
            dev.havlli.fabricate.internal.locale.FrenchLocaleProvider,
            dev.havlli.fabricate.internal.locale.PortugueseLocaleProvider,
            dev.havlli.fabricate.internal.locale.HindiLocaleProvider;
}
