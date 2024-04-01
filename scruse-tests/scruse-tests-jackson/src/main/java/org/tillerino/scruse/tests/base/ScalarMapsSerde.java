package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import java.util.Map;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.AnEnum;

interface ScalarMapsSerde {
    @JsonOutput
    void writeStringBooleanMap(Map<String, Boolean> map, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringByteMap(Map<String, Byte> map, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringShortMap(Map<String, Short> map, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringIntMap(Map<String, Integer> map, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringLongMap(Map<String, Long> map, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringCharMap(Map<String, Character> map, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringFloatMap(Map<String, Float> map, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringDoubleMap(Map<String, Double> map, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringStringMap(Map<String, String> map, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringEnumMap(Map<String, AnEnum> map, JsonGenerator generator) throws IOException;

    @JsonInput
    Map<String, Boolean> readStringBooleanMap(JsonParser parser) throws IOException;

    @JsonInput
    Map<String, Byte> readStringByteMap(JsonParser parser) throws IOException;

    @JsonInput
    Map<String, Short> readStringShortMap(JsonParser parser) throws IOException;

    @JsonInput
    Map<String, Integer> readStringIntMap(JsonParser parser) throws IOException;

    @JsonInput
    Map<String, Long> readStringLongMap(JsonParser parser) throws IOException;

    @JsonInput
    Map<String, Character> readStringCharMap(JsonParser parser) throws IOException;

    @JsonInput
    Map<String, Float> readStringFloatMap(JsonParser parser) throws IOException;

    @JsonInput
    Map<String, Double> readStringDoubleMap(JsonParser parser) throws IOException;

    @JsonInput
    Map<String, String> readStringStringMap(JsonParser parser) throws IOException;

    @JsonInput
    Map<String, AnEnum> readStringEnumMap(JsonParser parser) throws IOException;
}
