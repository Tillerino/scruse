package org.tillerino.scruse.tests.base.features;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.model.features.IgnorePropertiesModel.JsonIgnorePropertiesValue;

class IgnorePropertiesTest extends ReferenceTest {

    IgnorePropertiesSerde serde = new IgnorePropertiesSerdeImpl();

    @Test
    void ignoreSpecificPropertiesInInput() throws Exception {
        inputUtils.assertIsEqualToDatabind(
                """
                        { "name": "Moopsy", "value": 123, "ignoredValue": "ignored", "renamed": "not ignored",
                          "ignoredArray": [ "a", "b" ], "ignoredObject": { "a": "b" } }
                    """,
                serde::readJsonIgnorePropertiesValue,
                new TypeReference<>() {});
    }

    @Test
    void ignoreSpecificPropertiesInOutput() throws Exception {
        JsonIgnorePropertiesValue value =
                new JsonIgnorePropertiesValue("Moopsy", 123, "ignored", "not actually ignored");
        outputUtils.assertIsEqualToDatabind(value, serde::writeJsonIgnorePropertiesValue);
    }
}
