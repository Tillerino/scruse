package org.tillerino.scruse.tests;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CustomizedSerialization.*;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

class CustomizedSerializationTest {
    CustomizedSerialization serde = new CustomizedSerializationImpl();

    @Test
    void offsetDateTimeSerializationExample() throws IOException {
        String json = OutputUtils.withJacksonJsonGenerator(generator ->
                serde.writeMyObj(new MyObj(OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)), generator));
        Assertions.assertThat(json).isEqualTo("{\"t\":\"2021-01-01T00:00Z\"}");
    }

    @Test
    void offsetDateTimeDeserializationExample() throws IOException {
        MyObj myObj = InputUtils.withJacksonJsonParser("{\"t\":\"2021-01-01T00:00Z\"}", parser ->
                serde.readMyObj(parser));
        Assertions.assertThat(myObj).isEqualTo(new MyObj(OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)));
    }
}
