package org.tillerino.scruse.tests;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableFunction;
import org.tillerino.scruse.api.DeserializationContext;
import org.tillerino.scruse.api.SerializationContext;

public class OutputUtils {

    public static <T> String assertIsEqualToDatabind(T obj, FailableFunction<T, JsonNode, IOException> output)
            throws IOException {
        String databind = new ObjectMapper().writeValueAsString(obj);
        String ours = serialize(obj, output);
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T, U> String assertIsEqualToDatabind2(
            T obj, U obj2, FailableBiFunction<T, U, JsonNode, IOException> output) throws IOException {
        return assertIsEqualToDatabind(obj, o -> output.apply(o, obj2));
    }

    public static <T> String serialize(T obj, FailableFunction<T, JsonNode, IOException> output) throws IOException {
        return output.apply(obj).toString();
    }

    public static <T, U> String serialize2(T obj, U obj2, FailableBiFunction<T, U, JsonNode, IOException> output)
            throws IOException {
        return output.apply(obj, obj2).toString();
    }

    public static <T> String assertIsEqualToDatabind(
            T obj, FailableBiFunction<T, SerializationContext, JsonNode, IOException> output) throws IOException {
        String databind = new ObjectMapper().writeValueAsString(obj);
        String ours = output.apply(obj, new SerializationContext()).toString();
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T roundTrip(
            T obj,
            FailableFunction<T, JsonNode, IOException> output,
            FailableFunction<JsonNode, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        String json = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabind(json, input, typeRef);
    }

    public static <T, U> T roundTrip2(
            T obj,
            U obj2,
            FailableBiFunction<T, U, JsonNode, IOException> output,
            FailableBiFunction<JsonNode, U, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        String json = assertIsEqualToDatabind2(obj, obj2, output);
        return InputUtils.assertIsEqualToDatabind2(json, obj2, input, typeRef);
    }

    public static <T> T roundTripContext(
            T obj,
            FailableBiFunction<T, SerializationContext, JsonNode, IOException> output,
            FailableBiFunction<JsonNode, DeserializationContext, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        String json = assertIsEqualToDatabind2(obj, new SerializationContext(), output);
        return InputUtils.assertIsEqualToDatabind2(json, new DeserializationContext(), input, typeRef);
    }

    public static <T> T roundTripRecursive(
            T obj,
            FailableFunction<T, JsonNode, IOException> output,
            FailableFunction<JsonNode, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        String json = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabindComparingRecursively(json, input, typeRef);
    }
}
