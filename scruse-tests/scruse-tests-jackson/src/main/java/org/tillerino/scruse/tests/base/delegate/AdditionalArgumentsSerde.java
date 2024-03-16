package org.tillerino.scruse.tests.base.delegate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public interface AdditionalArgumentsSerde {
	@JsonOutput
	void writeStringIntMap(Map<String, Integer> obj, JsonGenerator out, List<Integer> collector) throws IOException;

	@JsonOutput
	default void writeInt(Integer obj, JsonGenerator out, List<Integer> collector) throws IOException {
		collector.add(obj);
		// WriterMode.RETURN: return writeString("");
		writeString("", out);
	}

	@JsonOutput
	void writeString(String obj, JsonGenerator gen) throws IOException;

	@JsonInput
	Map<String, Integer> readStringIntMap(JsonParser in, Queue<Integer> collector) throws IOException;

	@JsonInput
	default Integer readInt(JsonParser in, Queue<Integer> collector) throws IOException {
		readString(in);
		return collector.remove();
	}

	@JsonInput
	String readString(JsonParser in) throws IOException;
}
