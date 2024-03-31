package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.AnEnum;

/**
 * This interface must be separated from {@link PrimitiveScalarsWriter} because otherwise the boxed writers delegate to
 * the non-boxed ones.
 */
public interface BoxedScalarsWriter {
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
}
