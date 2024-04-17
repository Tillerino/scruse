package org.tillerino.scruse.tests.base.delegate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.base.PrimitiveScalarsSerde;

/**
 * We make assertions about the generated code, so we mark methods with an X to make sure they don't collide with other
 * libraries' methods.
 */
@JsonConfig(uses = PrimitiveScalarsSerde.class)
public interface BoxedScalarsWriter {
    @JsonOutput
    void writeBoxedBooleanX(Boolean b, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedByteX(Byte b, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedShortX(Short s, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedIntX(Integer i, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedLongX(Long l, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedCharX(Character c, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedFloatX(Float f, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedDoubleX(Double d, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeStringX(String s, JsonGenerator generator) throws Exception;

    @JsonInput
    Boolean readBoxedBooleanX(JsonParser parser) throws Exception;

    @JsonInput
    Byte readBoxedByteX(JsonParser parser) throws Exception;

    @JsonInput
    Short readBoxedShortX(JsonParser parser) throws Exception;

    @JsonInput
    Integer readBoxedIntX(JsonParser parser) throws Exception;

    @JsonInput
    Long readBoxedLongX(JsonParser parser) throws Exception;

    @JsonInput
    Character readBoxedCharX(JsonParser parser) throws Exception;

    @JsonInput
    Float readBoxedFloatX(JsonParser parser) throws Exception;

    @JsonInput
    Double readBoxedDoubleX(JsonParser parser) throws Exception;

    @JsonInput
    String readStringX(JsonParser parser) throws Exception;
}
