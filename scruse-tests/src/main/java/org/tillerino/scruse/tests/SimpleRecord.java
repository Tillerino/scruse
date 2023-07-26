package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.stream.JsonWriter;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public record SimpleRecord(int intField, String stringField, List<SimpleClass> listField, Map<String, Integer> mapField) {
	public interface Writer {
		@JsonOutput
		void write(SimpleRecord object, JsonGenerator generator) throws IOException;
		@JsonOutput
		void write(SimpleRecord object, JsonWriter generator) throws IOException;

		@JsonOutput
		JsonNode write(SimpleRecord object);
	}
}