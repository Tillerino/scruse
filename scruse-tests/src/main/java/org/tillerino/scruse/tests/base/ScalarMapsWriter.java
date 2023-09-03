package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;
import java.util.Map;

interface ScalarMapsWriter {
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
}
