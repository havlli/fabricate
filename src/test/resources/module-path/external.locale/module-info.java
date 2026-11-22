module external.locale {
    requires dev.havlli.fabricate;
    uses dev.havlli.fabricate.spi.LocaleProvider;
    provides dev.havlli.fabricate.spi.LocaleProvider with example.ExternalLocaleProvider;
}
