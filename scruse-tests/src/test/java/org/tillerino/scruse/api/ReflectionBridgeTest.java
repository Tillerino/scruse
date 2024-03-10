package org.tillerino.scruse.api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.api.ReflectionBridge.DeserializerDescription;
import org.tillerino.scruse.api.ReflectionBridge.ReturningSerializerDescription;
import org.tillerino.scruse.api.ReflectionBridge.SerializerDescription;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.base.ScalarFieldsRecord;
import org.tillerino.scruse.tests.base.ScalarFieldsRecord$SerdeImpl;
import org.tillerino.scruse.tests.model.AnEnum;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReflectionBridgeTest {
    Type type = JaxRsInterfaceOrSomething.class.getMethods()[0].getGenericReturnType();

    @Test
    void testFindDeserializer() throws IOException {
        ReflectionBridge reflectionBridge = new ReflectionBridge(List.of(new ScalarFieldsRecord$SerdeImpl()));

        Optional<DeserializerDescription<JsonParser>> deserializerMaybe = reflectionBridge.findDeserializer(type, JsonParser.class);

        assertThat(deserializerMaybe).isNotEmpty();
        InputUtils.withJacksonJsonParser("{}", parser -> {
            assertThat(deserializerMaybe.get().invoke(parser)).isInstanceOf(ScalarFieldsRecord.class);
            return null;
        });
    }

    @Test
    void testFindSerializer() throws IOException {
        ReflectionBridge reflectionBridge = new ReflectionBridge(List.of(new ScalarFieldsRecord$SerdeImpl()));

        Optional<SerializerDescription<JsonGenerator>> serializerMaybe = reflectionBridge.findSerializer(type, JsonGenerator.class);

        assertThat(serializerMaybe).isNotEmpty();
        assertThat(OutputUtils.withJacksonJsonGenerator(generator -> {
            ScalarFieldsRecord value = new ScalarFieldsRecord(false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, "six", AnEnum.SOME_VALUE);
            serializerMaybe.get().invoke(value, generator);
        })).isNotEmpty();
    }

    @Test
    void testFindReturningSerializer() {
        ReflectionBridge reflectionBridge = new ReflectionBridge(List.of(new org.tillerino.scruse.tests.alt.jsonnode.ScalarFieldsRecord$SerdeImpl()));

        Type type = JaxRsInterfaceOrSomethingForJsonNode.class.getMethods()[0].getGenericReturnType();
        Optional<ReturningSerializerDescription<JsonNode>> serializerMaybe = reflectionBridge.findReturningSerializer(type, JsonNode.class);

        assertThat(serializerMaybe).isNotEmpty();
        org.tillerino.scruse.tests.alt.jsonnode.ScalarFieldsRecord value = new org.tillerino.scruse.tests.alt.jsonnode.ScalarFieldsRecord(
                false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, false, (byte) 1, (short) 2, 3, 4L, 'c', 4f, 5.0d, "six", AnEnum.SOME_VALUE);
        assertThat(serializerMaybe.get().invoke(value)).isNotEmpty();
    }

    interface JaxRsInterfaceOrSomething {
        // we just need the return type
        ScalarFieldsRecord someRoute();
    }

    interface JaxRsInterfaceOrSomethingForJsonNode {
        // we just need the return type
        org.tillerino.scruse.tests.alt.jsonnode.ScalarFieldsRecord someRoute();
    }
}
