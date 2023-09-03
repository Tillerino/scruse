package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.stream.JsonWriter;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;
import java.util.List;
import java.util.Map;

interface NestedScalarsWriter {
	@JsonOutput
	void writeDoubleArrayArray(Double[][] array, JsonGenerator output) throws IOException;

	@JsonOutput
	void writeDoubleArrayArray(Double[][] array, JsonWriter output) throws IOException;

	@JsonOutput
	JsonNode writeDoubleArrayArray(Double[][] array);

	@JsonOutput
	void writeStringDoubleArrayMap(Map<String, Double[]> map, JsonGenerator output) throws IOException;

	@JsonOutput
	void writeStringDoubleArrayMap(Map<String, Double[]> map, JsonWriter output) throws IOException;

	@JsonOutput
	JsonNode writeStringDoubleArrayMap(Map<String, Double[]> map);

	@JsonOutput
	void writeStringDoubleMapList(List<Map<String, Double>> list, JsonGenerator output) throws IOException;

	@JsonOutput
	void writeStringDoubleMapList(List<Map<String, Double>> list, JsonWriter output) throws IOException;

	@JsonOutput
	JsonNode writeStringDoubleMapList(List<Map<String, Double>> list);

	@JsonOutput
	void writeStringDoubleMapMap(Map<String, Map<String, Double>> map, JsonGenerator output) throws IOException;

	@JsonOutput
	void writeStringDoubleMapMap(Map<String, Map<String, Double>> map, JsonWriter output) throws IOException;

	@JsonOutput
	JsonNode writeStringDoubleMapMap(Map<String, Map<String, Double>> map);
}
