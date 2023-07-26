package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.stream.JsonWriter;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;
import java.util.Map;

public interface ScalarMapsWriter {
	@JsonOutput
	void writeStringBooleanMap(Map<String, Boolean> map, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeStringBooleanMap(Map<String, Boolean> map, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeStringBooleanMap(Map<String, Boolean> map);

	@JsonOutput
	void writeStringByteMap(Map<String, Byte> map, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeStringByteMap(Map<String, Byte> map, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeStringByteMap(Map<String, Byte> map);

	@JsonOutput
	void writeStringShortMap(Map<String, Short> map, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeStringShortMap(Map<String, Short> map, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeStringShortMap(Map<String, Short> map);

	@JsonOutput
	void writeStringIntMap(Map<String, Integer> map, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeStringIntMap(Map<String, Integer> map, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeStringIntMap(Map<String, Integer> map);

	@JsonOutput
	void writeStringLongMap(Map<String, Long> map, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeStringLongMap(Map<String, Long> map, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeStringLongMap(Map<String, Long> map);

	@JsonOutput
	void writeStringCharMap(Map<String, Character> map, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeStringCharMap(Map<String, Character> map, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeStringCharMap(Map<String, Character> map);

	@JsonOutput
	void writeStringFloatMap(Map<String, Float> map, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeStringFloatMap(Map<String, Float> map, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeStringFloatMap(Map<String, Float> map);

	@JsonOutput
	void writeStringDoubleMap(Map<String, Double> map, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeStringDoubleMap(Map<String, Double> map, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeStringDoubleMap(Map<String, Double> map);

	@JsonOutput
	void writeStringStringMap(Map<String, String> map, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeStringStringMap(Map<String, String> map, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeStringStringMap(Map<String, String> map);
}
