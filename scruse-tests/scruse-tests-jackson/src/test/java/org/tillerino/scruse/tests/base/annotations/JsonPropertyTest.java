package org.tillerino.scruse.tests.base.annotations;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.model.annotations.JsonPropertyCustomName;
import org.tillerino.scruse.tests.model.annotations.JsonPropertyCustomNameWithSetter;

class JsonPropertyTest extends ReferenceTest {
    JsonPropertySerde serde = new JsonPropertySerdeImpl();

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
