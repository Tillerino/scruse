package org.tillerino.scruse.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableFunction;
import org.tillerino.scruse.api.SerializationContext;

import java.io.IOException;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

public class OutputUtils {

	public static <T> String assertIsEqualToDatabind(T obj, FailableFunction<T, JsonNode, IOException> output) throws IOException {
		String databind = new ObjectMapper().writeValueAsString(obj);
		String ours = output.apply(obj).toString();
		assertThatJson(ours).isEqualTo(databind);
		return ours;
	}

	public static <T> String assertIsEqualToDatabind(T obj, FailableBiFunction<T, SerializationContext, JsonNode, IOException> output) throws IOException {
		String databind = new ObjectMapper().writeValueAsString(obj);
		String ours = output.apply(obj, new SerializationContext()).toString();
		assertThatJson(ours).isEqualTo(databind);
		return ours;
	}

}
