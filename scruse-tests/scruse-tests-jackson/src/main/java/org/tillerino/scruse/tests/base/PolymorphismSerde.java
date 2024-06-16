package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.api.DeserializationContext;
import org.tillerino.scruse.api.SerializationContext;

public interface PolymorphismSerde {
    @JsonOutput
    void writePolymorphism(SealedInterface sealedInterface, JsonGenerator generator) throws Exception;

    @JsonInput
    SealedInterface readPolymorphism(JsonParser generator) throws Exception;

    interface WithDelegate {
        @JsonOutput
        void writePolymorphism(SealedInterface sealedInterface, JsonGenerator generator, SerializationContext context)
                throws Exception;

        @JsonOutput
        void writeOne(RecordOne recordOne, JsonGenerator generator, SerializationContext context) throws Exception;

        @JsonInput
        SealedInterface readPolymorphism(JsonParser parser, DeserializationContext context) throws Exception;

        @JsonInput
        RecordOne readOne(JsonParser parser, DeserializationContext context) throws Exception;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    sealed interface SealedInterface {}

    record RecordOne(String s) implements SealedInterface {}

    record RecordTwo(int i) implements SealedInterface {}
}
