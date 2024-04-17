package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;

public interface PrimitiveScalarsSerde {
    @JsonOutput
    void writePrimitiveBooleanX(boolean b, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writePrimitiveByteX(byte b, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writePrimitiveShortX(short s, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writePrimitiveIntX(int i, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writePrimitiveLongX(long l, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writePrimitiveCharX(char c, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writePrimitiveFloatX(float f, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writePrimitiveDoubleX(double d, JsonGenerator generator) throws Exception;

    @JsonInput
    boolean readPrimitiveBooleanX(JsonParser parser) throws Exception;

    @JsonInput
    byte readPrimitiveByteX(JsonParser parser) throws Exception;

    @JsonInput
    short readPrimitiveShortX(JsonParser parser) throws Exception;

    @JsonInput
    int readPrimitiveIntX(JsonParser parser) throws Exception;

    @JsonInput
    long readPrimitiveLongX(JsonParser parser) throws Exception;

    @JsonInput
    char readPrimitiveCharX(JsonParser parser) throws Exception;

    @JsonInput
    float readPrimitiveFloatX(JsonParser parser) throws Exception;

    @JsonInput
    double readPrimitiveDoubleX(JsonParser parser) throws Exception;
}
