package org.tillerino.scruse.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.function.FailableBiConsumer;
import org.apache.commons.lang3.function.FailableConsumer;
import org.tillerino.scruse.api.SerializationContext;

import java.io.IOException;
import java.io.StringWriter;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class OutputUtils {

	public static String withGsonJsonWriter(FailableConsumer<JsonWriter, IOException> output) throws IOException {
		StringWriter out = new StringWriter();
		JsonWriter generator = new JsonWriter(out);
		output.accept(generator);
		generator.flush();
		return out.toString();
	}

	public static <T> String assertIsEqualToDatabind(T obj, FailableBiConsumer<T, JsonWriter, IOException> output) throws IOException {
		String databind = new ObjectMapper().writeValueAsString(obj);
		String ours = serialize(obj, output);
		assertThatJson(ours).isEqualTo(databind);
		return ours;
	}

	public static <T, U> String assertIsEqualToDatabind2(T obj, U obj2, FailableTriConsumer<T, JsonWriter, U, IOException> output) throws IOException {
		return assertIsEqualToDatabind(obj, (o, writer) -> output.accept(o, writer, obj2));
	}

	public static <T> String serialize(T obj, FailableBiConsumer<T, JsonWriter, IOException> output) throws IOException {
		return withGsonJsonWriter(generator -> output.accept(obj, generator));
	}

	public static <T, U> String serialize2(T obj, U obj2, FailableTriConsumer<T, JsonWriter, U, IOException> output) throws IOException {
		return withGsonJsonWriter(generator -> output.accept(obj, generator, obj2));
	}

	public static <T> String assertIsEqualToDatabind(T obj, FailableTriConsumer<T, JsonWriter, SerializationContext, IOException> output) throws IOException {
		String databind = new ObjectMapper().writeValueAsString(obj);
		String ours = withGsonJsonWriter(generator -> output.accept(obj, generator, new SerializationContext()));
		assertThatJson(ours).isEqualTo(databind);
		return ours;
	}

	public interface FailableTriConsumer<T, U, V, E extends Throwable> {
		void accept(T t, U u, V v) throws E;
	}
}