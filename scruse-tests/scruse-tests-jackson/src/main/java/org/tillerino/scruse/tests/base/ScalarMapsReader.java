package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import java.util.Map;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.tests.model.AnEnum;

interface ScalarMapsReader {
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
