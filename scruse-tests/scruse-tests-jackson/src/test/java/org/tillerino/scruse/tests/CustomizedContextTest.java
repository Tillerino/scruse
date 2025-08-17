package org.tillerino.scruse.tests;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CustomizedContextSerde.CustomDeserializationContext;
import org.tillerino.scruse.tests.CustomizedContextSerde.CustomSerializationContext;
import org.tillerino.scruse.tests.CustomizedContextSerde.MyObj;

class CustomizedContextTest extends ReferenceTest {
    CustomizedContextSerde serde = SerdeUtil.impl(CustomizedContextSerde.class);

    @Test
    void testCustomizedContextSerialization() throws Exception {
        String json = outputUtils.withJsonGenerator(generator ->
                serde.writeMyObj(List.of(new MyObj(10), new MyObj(20)), generator, new CustomSerializationContext()));
        Assertions.assertThat(json).isEqualTo("[{\"i\":10},{\"i\":21}]");
    }

    @Test
    void testCustomizedContextDeserialization() throws Exception {
        List<MyObj> myObjs = inputUtils.withJsonParser(
                "[{\"i\":10},{\"i\":20}]", parser -> serde.readMyObj(parser, new CustomDeserializationContext()));
        Assertions.assertThat(myObjs).containsExactly(new MyObj(10), new MyObj(21));
    }
}
