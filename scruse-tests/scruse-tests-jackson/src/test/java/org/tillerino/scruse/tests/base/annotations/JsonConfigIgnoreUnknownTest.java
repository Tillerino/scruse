package org.tillerino.scruse.tests.base.annotations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;

class JsonConfigIgnoreUnknownTest extends ReferenceTest {

    JsonConfigIgnoreUnknownSerde serde = new JsonConfigIgnoreUnknownSerdeImpl();

    @Test
    void ignoreStringProperty() throws Exception {
        inputUtils.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        inputUtils.assertIsEqualToDatabind(
                "{ \"name\": \"Moopsy\", \"unknown\": \"blergh\", \"value\": 123 }",
                serde::readJsonConfigIgnoreUnknown,
                new TypeReference<>() {});
    }

    @Test
    void ignoreObjectProperty() throws Exception {
        inputUtils.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        inputUtils.assertIsEqualToDatabind(
                "{ \"name\": \"Moopsy\", \"unknown\": { \"hello\": 456 }, \"value\": 123 }",
                serde::readJsonConfigIgnoreUnknown,
                new TypeReference<>() {});
    }

    @Test
    void ignoreArrayProperty() throws Exception {
        inputUtils.objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        inputUtils.assertIsEqualToDatabind(
                "{ \"name\": \"Moopsy\", \"unknown\": [ \"blergh\" ], \"value\": 123 }",
                serde::readJsonConfigIgnoreUnknown,
                new TypeReference<>() {});
    }
}
