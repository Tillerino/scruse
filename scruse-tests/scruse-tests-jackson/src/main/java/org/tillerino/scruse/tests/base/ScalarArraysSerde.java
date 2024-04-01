package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.AnEnum;

interface ScalarArraysSerde {
    @JsonOutput
    void writeBooleanArray(boolean[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeByteArray(byte[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeShortArray(short[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeIntArray(int[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeLongArray(long[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeCharArray(char[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeFloatArray(float[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeDoubleArray(double[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedBooleanArray(Boolean[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedByteArray(Byte[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedShortArray(Short[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedIntArray(Integer[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedLongArray(Long[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedCharArray(Character[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedFloatArray(Float[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedDoubleArray(Double[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringArray(String[] input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeEnumArray(AnEnum[] input, JsonGenerator generator) throws IOException;

    @JsonInput
    boolean[] readBooleanArray(JsonParser parser) throws IOException;

    @JsonInput
    byte[] readByteArray(JsonParser parser) throws IOException;

    @JsonInput
    char[] readCharArray(JsonParser parser) throws IOException;

    @JsonInput
    short[] readShortArray(JsonParser parser) throws IOException;

    @JsonInput
    int[] readIntArray(JsonParser parser) throws IOException;

    @JsonInput
    long[] readLongArray(JsonParser parser) throws IOException;

    @JsonInput
    float[] readFloatArray(JsonParser parser) throws IOException;

    @JsonInput
    double[] readDoubleArray(JsonParser parser) throws IOException;

    @JsonInput
    Boolean[] readBoxedBooleanArray(JsonParser parser) throws IOException;

    @JsonInput
    Byte[] readBoxedByteArray(JsonParser parser) throws IOException;

    @JsonInput
    Short[] readBoxedShortArray(JsonParser parser) throws IOException;

    @JsonInput
    Integer[] readBoxedIntArray(JsonParser parser) throws IOException;

    @JsonInput
    Long[] readBoxedLongArray(JsonParser parser) throws IOException;

    @JsonInput
    Float[] readBoxedFloatArray(JsonParser parser) throws IOException;

    @JsonInput
    Double[] readBoxedDoubleArray(JsonParser parser) throws IOException;

    @JsonInput
    String[] readStringArray(JsonParser parser) throws IOException;

    @JsonInput
    AnEnum[] readEnumArray(JsonParser parser) throws IOException;
}
