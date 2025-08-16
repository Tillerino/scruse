package org.tillerino.scruse.tests;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CustomizedSerialization.MyObj;

class CustomizedSerializationTest extends ReferenceTest {
    CustomizedSerialization serde = new CustomizedSerializationImpl();

    @Test
    void offsetDateTimeSerializationExample() throws Exception {
        String json = outputUtils.withJsonGenerator(generator ->
                serde.writeMyObj(new MyObj(OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)), generator));
        Assertions.assertThat(json).isEqualTo("{\"t\":\"2021-01-01T00:00Z\"}");
    }

    @Test
    void offsetDateTimeDeserializationExample() throws Exception {
        MyObj myObj = inputUtils.withJsonParser("{\"t\":\"2021-01-01T00:00Z\"}", parser -> serde.readMyObj(parser));
        Assertions.assertThat(myObj).isEqualTo(new MyObj(OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)));
    }
}
