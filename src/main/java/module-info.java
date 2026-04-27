@org.jspecify.annotations.NullMarked
module org.fabricate {
    requires static org.jspecify;

    exports org.fabricate.model;
    exports org.fabricate.generators;
    exports org.fabricate.generators.address;
    exports org.fabricate.generators.person;
    exports org.fabricate.generators.strategies;
    exports org.fabricate.store;
    exports org.fabricate.store.repository;
}
