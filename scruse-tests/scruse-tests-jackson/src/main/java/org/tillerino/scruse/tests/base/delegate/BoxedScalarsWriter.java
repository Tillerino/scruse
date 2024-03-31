package org.tillerino.scruse.tests.base.delegate;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.base.PrimitiveScalarsWriter;

/**
 * We make assertions about the generated code, so we mark methods with an X to make sure they don't collide with other
 * libraries' methods.
 */
@JsonConfig(uses = PrimitiveScalarsWriter.class)
public interface BoxedScalarsWriter {
    @JsonOutput
    void writeBoxedBooleanX(Boolean b, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedByteX(Byte b, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedShortX(Short s, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedIntX(Integer i, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedLongX(Long l, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedCharX(Character c, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedFloatX(Float f, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedDoubleX(Double d, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringX(String s, JsonGenerator generator) throws IOException;
}
