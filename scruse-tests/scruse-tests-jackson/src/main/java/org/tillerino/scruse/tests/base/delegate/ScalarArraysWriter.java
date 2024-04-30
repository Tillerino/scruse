package org.tillerino.scruse.tests.base.delegate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;

// methods are marked with X to make sure no methods of the backend are named identically
@JsonConfig(uses = BoxedScalarsWriter.class)
interface ScalarArraysWriter {
    @JsonOutput
    void writeBooleanArrayX(boolean[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeByteArrayX(byte[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeShortArrayX(short[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeIntArrayX(int[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeLongArrayX(long[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeCharArrayX(char[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeFloatArrayX(float[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeDoubleArrayX(double[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedBooleanArrayX(Boolean[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedByteArrayX(Byte[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedShortArrayX(Short[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedIntArrayX(Integer[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedLongArrayX(Long[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedCharArrayX(Character[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedFloatArrayX(Float[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeBoxedDoubleArrayX(Double[] input, JsonGenerator generator) throws Exception;

    @JsonOutput
    void writeStringArrayX(String[] input, JsonGenerator generator) throws Exception;

    @JsonInput
    boolean[] readBooleanArrayX(JsonParser parser) throws Exception;

    @JsonInput
    byte[] readByteArrayX(JsonParser parser) throws Exception;

    @JsonInput
    char[] readCharArrayX(JsonParser parser) throws Exception;

    @JsonInput
    short[] readShortArrayX(JsonParser parser) throws Exception;

    @JsonInput
    int[] readIntArrayX(JsonParser parser) throws Exception;

    @JsonInput
    long[] readLongArrayX(JsonParser parser) throws Exception;

    @JsonInput
    float[] readFloatArrayX(JsonParser parser) throws Exception;

    @JsonInput
    double[] readDoubleArrayX(JsonParser parser) throws Exception;

    @JsonInput
    Boolean[] readBoxedBooleanArrayX(JsonParser parser) throws Exception;

    @JsonInput
    Byte[] readBoxedByteArrayX(JsonParser parser) throws Exception;

    @JsonInput
    Short[] readBoxedShortArrayX(JsonParser parser) throws Exception;

    @JsonInput
    Integer[] readBoxedIntArrayX(JsonParser parser) throws Exception;

    @JsonInput
    Long[] readBoxedLongArrayX(JsonParser parser) throws Exception;

    @JsonInput
    Float[] readBoxedFloatArrayX(JsonParser parser) throws Exception;

    @JsonInput
    Double[] readBoxedDoubleArrayX(JsonParser parser) throws Exception;

    @JsonInput
    String[] readStringArrayX(JsonParser parser) throws Exception;
}
