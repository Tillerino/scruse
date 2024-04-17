package org.tillerino.scruse.tests;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableFunction;
import org.tillerino.scruse.api.SerializationContext;

public class OutputUtils {

    public static <T> String assertIsEqualToDatabind(T obj, FailableFunction<T, JsonNode, Exception> output)
            throws Exception {
        String databind = new ObjectMapper().writeValueAsString(obj);
        String ours = serialize(obj, output);
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T, U> String assertIsEqualToDatabind2(
            T obj, U obj2, FailableBiFunction<T, U, JsonNode, Exception> output) throws Exception {
        return assertIsEqualToDatabind(obj, o -> output.apply(o, obj2));
    }

    public static <T> String serialize(T obj, FailableFunction<T, JsonNode, Exception> output) throws Exception {
        return output.apply(obj).toString();
    }

    public static <T, U> String serialize2(T obj, U obj2, FailableBiFunction<T, U, JsonNode, Exception> output)
            throws Exception {
        return output.apply(obj, obj2).toString();
    }

    public static <T> String assertIsEqualToDatabind(
            T obj, FailableBiFunction<T, SerializationContext, JsonNode, Exception> output) throws Exception {
        String databind = new ObjectMapper().writeValueAsString(obj);
        String ours = output.apply(obj, new SerializationContext()).toString();
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T roundTrip(
            T obj,
            FailableFunction<T, JsonNode, Exception> output,
            FailableFunction<JsonNode, T, Exception> input,
            TypeReference<T> typeRef)
            throws Exception {
        String json = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabind(json, input, typeRef);
    }

    public static <T, U> T roundTrip2(
            T obj,
            U obj2,
            FailableBiFunction<T, U, JsonNode, Exception> output,
            FailableBiFunction<JsonNode, U, T, Exception> input,
            TypeReference<T> typeRef)
            throws Exception {
        String json = assertIsEqualToDatabind2(obj, obj2, output);
        return InputUtils.assertIsEqualToDatabind2(json, obj2, input, typeRef);
    }

    public static <T> T roundTripRecursive(
            T obj,
            FailableFunction<T, JsonNode, Exception> output,
            FailableFunction<JsonNode, T, Exception> input,
            TypeReference<T> typeRef)
            throws Exception {
        String json = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabindComparingRecursively(json, input, typeRef);
    }
}
