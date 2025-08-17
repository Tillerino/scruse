package org.tillerino.scruse.tests.base.features;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.model.features.PropertyNameModel.JsonPropertyCustomName;
import org.tillerino.scruse.tests.model.features.PropertyNameModel.JsonPropertyCustomNameWithSetter;

class PropertyNameTest extends ReferenceTest {
    PropertyNameSerde serde = new PropertyNameSerdeImpl();

    @Test
    void customName() throws Exception {
        outputUtils.roundTrip(
                new JsonPropertyCustomName("x"),
                serde::writeCustomName,
                serde::readCustomName,
                new TypeReference<>() {});
    }

    @Test
    void customNameWithSetter() throws Exception {
        outputUtils.roundTrip(
                new JsonPropertyCustomNameWithSetter("x"),
                serde::writeCustomNameWithSetter,
                serde::readCustomNameWithSetter,
                new TypeReference<>() {});
    }
}
