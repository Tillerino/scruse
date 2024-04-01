package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import java.util.List;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.AnEnum;

interface ScalarListsSerde {

    @JsonOutput
    void writeBooleanList(List<Boolean> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeByteList(List<Byte> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeShortList(List<Short> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeIntList(List<Integer> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeLongList(List<Long> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeCharList(List<Character> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeFloatList(List<Float> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeDoubleList(List<Double> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringList(List<String> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeEnumList(List<AnEnum> input, JsonGenerator generator) throws IOException;

    @JsonInput
    List<Boolean> readBooleanList(JsonParser parser) throws IOException;

    @JsonInput
    List<Byte> readByteList(JsonParser parser) throws IOException;

    @JsonInput
    List<Character> readCharacterList(JsonParser parser) throws IOException;

    @JsonInput
    List<Short> readShortList(JsonParser parser) throws IOException;

    @JsonInput
    List<Integer> readIntegerList(JsonParser parser) throws IOException;

    @JsonInput
    List<Long> readLongList(JsonParser parser) throws IOException;

    @JsonInput
    List<Float> readFloatList(JsonParser parser) throws IOException;

    @JsonInput
    List<Double> readDoubleList(JsonParser parser) throws IOException;

    @JsonInput
    List<String> readStringList(JsonParser parser) throws IOException;

    @JsonInput
    List<AnEnum> readEnumList(JsonParser parser) throws IOException;
}
