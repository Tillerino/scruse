package org.tillerino.scruse.tests;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.json.stream.JsonGenerator;
import org.apache.commons.lang3.function.FailableBiConsumer;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableFunction;
import org.tillerino.scruse.api.SerializationContext;
import org.tillerino.scruse.helpers.JakartaJsonParserHelper.JsonParserWrapper;

public class OutputUtils {

    public static <T> String assertIsEqualToDatabind(T obj, FailableBiConsumer<T, JsonGenerator, Exception> output)
            throws Exception {
        String databind = InputUtils.objectMapper.writeValueAsString(obj);
        String ours = serialize(obj, output);
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T, U> String assertIsEqualToDatabind2(
            T obj, U obj2, FailableTriConsumer<T, JsonGenerator, U, Exception> output) throws Exception {
        return assertIsEqualToDatabind(obj, (o, writer) -> output.accept(o, writer, obj2));
    }

    public static <T> String serialize(T obj, FailableBiConsumer<T, JsonGenerator, Exception> output) throws Exception {
        return ToShadeUtils.withGsonJsonWriter(generator -> output.accept(obj, generator));
    }

    public static <T, U> String serialize2(T obj, U obj2, FailableTriConsumer<T, JsonGenerator, U, Exception> output)
            throws Exception {
        return ToShadeUtils.withGsonJsonWriter(generator -> output.accept(obj, generator, obj2));
    }

    public static <T> String assertIsEqualToDatabind(
            T obj, FailableTriConsumer<T, JsonGenerator, SerializationContext, Exception> output) throws Exception {
        String databind = InputUtils.objectMapper.writeValueAsString(obj);
        String ours =
                ToShadeUtils.withGsonJsonWriter(generator -> output.accept(obj, generator, new SerializationContext()));
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T roundTrip(
            T obj,
            FailableBiConsumer<T, JsonGenerator, Exception> output,
            FailableFunction<JsonParserWrapper, T, Exception> input,
            TypeReference<T> typeRef)
            throws Exception {
        String json = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabind(json, input, typeRef);
    }

    public static <T, U> T roundTrip2(
            T obj,
            U obj2,
            FailableTriConsumer<T, JsonGenerator, U, Exception> output,
            FailableBiFunction<JsonParserWrapper, U, T, Exception> input,
            TypeReference<T> typeRef)
            throws Exception {
        String json = assertIsEqualToDatabind2(obj, obj2, output);
        return InputUtils.assertIsEqualToDatabind2(json, obj2, input, typeRef);
    }

    public static <T> T roundTripRecursive(
            T obj,
            FailableBiConsumer<T, JsonGenerator, Exception> output,
            FailableFunction<JsonParserWrapper, T, Exception> input,
            TypeReference<T> typeRef)
            throws Exception {
        String json = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabindComparingRecursively(json, input, typeRef);
    }

    public interface FailableTriConsumer<T, U, V, E extends Throwable> {
        void accept(T t, U u, V v) throws E;
    }
}
