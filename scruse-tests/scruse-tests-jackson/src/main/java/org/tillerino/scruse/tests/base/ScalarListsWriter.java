package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.List;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.AnEnum;

interface ScalarListsWriter {

    @JsonOutput
    void writeBoxedBooleanList(List<Boolean> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedByteList(List<Byte> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedShortList(List<Short> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedIntList(List<Integer> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedLongList(List<Long> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedCharList(List<Character> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedFloatList(List<Float> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeBoxedDoubleList(List<Double> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeStringList(List<String> input, JsonGenerator generator) throws IOException;

    @JsonOutput
    void writeEnumList(List<AnEnum> input, JsonGenerator generator) throws IOException;
}
