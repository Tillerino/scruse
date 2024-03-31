package org.tillerino.scruse.tests;

import java.io.IOException;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CustomizedContextSerde.CustomDeserializationContext;
import org.tillerino.scruse.tests.CustomizedContextSerde.CustomSerializationContext;
import org.tillerino.scruse.tests.CustomizedContextSerde.MyObj;

class CustomizedContextTest {
    CustomizedContextSerde serde = new CustomizedContextSerdeImpl();

    @Test
    void testCustomizedContextSerialization() throws IOException {
        String json = OutputUtils.withJacksonJsonGenerator(generator ->
                serde.writeMyObj(List.of(new MyObj(10), new MyObj(20)), generator, new CustomSerializationContext()));
        Assertions.assertThat(json).isEqualTo("[{\"i\":10},{\"i\":21}]");
    }

    @Test
    void testCustomizedContextDeserialization() throws IOException {
        List<MyObj> myObjs = InputUtils.withJacksonJsonParser(
                "[{\"i\":10},{\"i\":20}]", parser -> serde.readMyObj(parser, new CustomDeserializationContext()));
        Assertions.assertThat(myObjs).containsExactly(new MyObj(10), new MyObj(21));
    }
}
