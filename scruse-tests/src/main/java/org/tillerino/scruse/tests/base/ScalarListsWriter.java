package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.stream.JsonWriter;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;
import java.util.List;

interface ScalarListsWriter {

	@JsonOutput
	void writeBoxedBooleanList(List<Boolean> input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedBooleanList(List<Boolean> input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedBooleanList(List<Boolean> input);

	@JsonOutput
	void writeBoxedByteList(List<Byte> input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedByteList(List<Byte> input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedByteList(List<Byte> input);

	@JsonOutput
	void writeBoxedShortList(List<Short> input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedShortList(List<Short> input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedShortList(List<Short> input);

	@JsonOutput
	void writeBoxedIntList(List<Integer> input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedIntList(List<Integer> input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedIntList(List<Integer> input);

	@JsonOutput
	void writeBoxedLongList(List<Long> input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedLongList(List<Long> input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedLongList(List<Long> input);

	@JsonOutput
	void writeBoxedCharList(List<Character> input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedCharList(List<Character> input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedCharList(List<Character> input);

	@JsonOutput
	void writeBoxedFloatList(List<Float> input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedFloatList(List<Float> input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedFloatList(List<Float> input);

	@JsonOutput
	void writeBoxedDoubleList(List<Double> input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedDoubleList(List<Double> input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedDoubleList(List<Double> input);

	@JsonOutput
	void writeStringList(List<String> input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeStringList(List<String> input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeStringList(List<String> input);
}
