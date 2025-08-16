package org.tillerino.scruse.tests.base.annotations;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;

class JsonIgnorePropertiesTest {

    JsonIgnorePropertiesSerde serde = new JsonIgnorePropertiesSerdeImpl();

    @Test
    void ignoreStringProperty() throws Exception {
        InputUtils.assertIsEqualToDatabind(
                "{ \"name\": \"Moopsy\", \"unknown\": \"blergh\", \"value\": 123 }",
                serde::readJsonIgnorePropertiesIgnoreUnknown,
                new TypeReference<>() {});
    }

    @Test
    void ignoreObjectProperty() throws Exception {
        InputUtils.assertIsEqualToDatabind(
                "{ \"name\": \"Moopsy\", \"unknown\": { \"hello\": 456 }, \"value\": 123 }",
                serde::readJsonIgnorePropertiesIgnoreUnknown,
                new TypeReference<>() {});
    }

    @Test
    void ignoreArrayProperty() throws Exception {
        InputUtils.assertIsEqualToDatabind(
                "{ \"name\": \"Moopsy\", \"unknown\": [ \"blergh\" ], \"value\": 123 }",
                serde::readJsonIgnorePropertiesIgnoreUnknown,
                new TypeReference<>() {});
    }
}
