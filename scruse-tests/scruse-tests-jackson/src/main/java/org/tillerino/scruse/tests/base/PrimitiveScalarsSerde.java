package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;

public interface PrimitiveScalarsSerde {
    @JsonOutput
    void writePrimitiveBooleanX(boolean b, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writePrimitiveByteX(byte b, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writePrimitiveShortX(short s, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writePrimitiveIntX(int i, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writePrimitiveLongX(long l, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writePrimitiveCharX(char c, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writePrimitiveFloatX(float f, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writePrimitiveDoubleX(double d, JsonGenerator generator) throws IOException;

    @JsonInput
    boolean readPrimitiveBooleanX(JsonParser parser) throws IOException;

    @JsonInput
    byte readPrimitiveByteX(JsonParser parser) throws IOException;

    @JsonInput
    short readPrimitiveShortX(JsonParser parser) throws IOException;

    @JsonInput
    int readPrimitiveIntX(JsonParser parser) throws IOException;

    @JsonInput
    long readPrimitiveLongX(JsonParser parser) throws IOException;

    @JsonInput
    char readPrimitiveCharX(JsonParser parser) throws IOException;

    @JsonInput
    float readPrimitiveFloatX(JsonParser parser) throws IOException;

    @JsonInput
    double readPrimitiveDoubleX(JsonParser parser) throws IOException;
}
