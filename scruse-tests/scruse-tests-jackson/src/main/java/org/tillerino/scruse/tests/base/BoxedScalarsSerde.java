package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.AnEnum;

/**
 * This interface must be separated from {@link PrimitiveScalarsSerde} because otherwise the boxed writers delegate to
 * the non-boxed ones.
 */
public interface BoxedScalarsSerde {
    @JsonOutput
    void writeBoxedBoolean(Boolean b, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedByte(Byte b, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedShort(Short s, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedInt(Integer i, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedLong(Long l, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedChar(Character c, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedFloat(Float f, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedDouble(Double d, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeString(String s, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeEnum(AnEnum e, JsonGenerator generator) throws IOException;

    @JsonInput
    Boolean readBoxedBoolean(JsonParser parser) throws IOException;

    @JsonInput
    Byte readBoxedByte(JsonParser parser) throws IOException;

    @JsonInput
    Short readBoxedShort(JsonParser parser) throws IOException;

    @JsonInput
    Integer readBoxedInt(JsonParser parser) throws IOException;

    @JsonInput
    Long readBoxedLong(JsonParser parser) throws IOException;

    @JsonInput
    Character readBoxedChar(JsonParser parser) throws IOException;

    @JsonInput
    Float readBoxedFloat(JsonParser parser) throws IOException;

    @JsonInput
    Double readBoxedDouble(JsonParser parser) throws IOException;

    @JsonInput
    String readString(JsonParser parser) throws IOException;

    @JsonInput
    AnEnum readEnum(JsonParser parser) throws IOException;
}
