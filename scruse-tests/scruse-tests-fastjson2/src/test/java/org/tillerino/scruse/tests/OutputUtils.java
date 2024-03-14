package org.tillerino.scruse.tests;

import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.function.FailableBiConsumer;
import org.apache.commons.lang3.function.FailableConsumer;
import org.tillerino.scruse.api.SerializationContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class OutputUtils {

	public static String withWriter(FailableConsumer<JSONWriter, IOException> output) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		JSONWriter writer = JSONWriter.of();
		output.accept(writer);
		writer.flushTo(out);
		return out.toString(StandardCharsets.UTF_8);
	}

	public static <T> String assertIsEqualToDatabind(T obj, FailableBiConsumer<T, JSONWriter, IOException> output) throws IOException {
		String databind = new ObjectMapper().writeValueAsString(obj);
		String ours = withWriter(generator -> output.accept(obj, generator));
		System.out.println(ours);
		assertThatJson(ours).isEqualTo(databind);
		return ours;
	}

	public static <T> String assertIsEqualToDatabind(T obj, FailableTriConsumer<T, JSONWriter, SerializationContext, IOException> output) throws IOException {
		String databind = new ObjectMapper().writeValueAsString(obj);
		String ours = withWriter(generator -> output.accept(obj, generator, new SerializationContext()));
		System.out.println(ours);
		assertThatJson(ours).isEqualTo(databind);
		return ours;
	}

	public interface FailableTriConsumer<T, U, V, E extends Throwable> {
		void accept(T t, U u, V v) throws E;
	}
}
