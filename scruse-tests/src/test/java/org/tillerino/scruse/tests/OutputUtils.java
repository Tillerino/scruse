package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.function.FailableBiConsumer;
import org.apache.commons.lang3.function.FailableConsumer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class OutputUtils {
	static String withJacksonJsonGenerator(FailableConsumer<JsonGenerator, IOException> output) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JsonGenerator generator = new JsonFactory().createGenerator(out, JsonEncoding.UTF8);
		output.accept(generator);
		generator.flush();
		return out.toString(StandardCharsets.UTF_8);
	}

	static String withGsonJsonWriter(FailableConsumer<JsonWriter, IOException> output) throws IOException {
		StringWriter out = new StringWriter();
		JsonWriter generator = new JsonWriter(out);
		output.accept(generator);
		generator.flush();
		return out.toString();
	}

	public static <T> void assertThatJacksonJsonGeneratorIsEqualToDatabind(T obj, FailableBiConsumer<T, JsonGenerator, IOException> output) throws IOException {
		String databind = new ObjectMapper().writeValueAsString(obj);
		String ours = withJacksonJsonGenerator(generator -> output.accept(obj, generator));
		System.out.println(ours);
		assertThatJson(ours).isEqualTo(databind);
	}

	public static <T> void assertThatJacksonJsonNodeIsEqualToDatabind(T obj, Function<T, JsonNode> output) throws IOException {
		String databind = new ObjectMapper().writeValueAsString(obj);
		String ours = output.apply(obj).toString();
		assertThatJson(ours).isEqualTo(databind);
	}

	public static <T> void assertThatGsonJsonWriterIsEqualToDatabind(T obj, FailableBiConsumer<T, JsonWriter, IOException> output) throws IOException {
		String databind = new ObjectMapper().writeValueAsString(obj);
		String ours = withGsonJsonWriter(generator -> output.accept(obj, generator));
		assertThatJson(ours).isEqualTo(databind);
	}
}
