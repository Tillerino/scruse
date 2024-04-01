package org.tillerino.scruse.tests;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.apache.commons.lang3.function.FailableBiConsumer;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.tillerino.scruse.api.SerializationContext;

public class OutputUtils {

    public static String withGsonJsonWriter(FailableConsumer<JsonWriter, IOException> output) throws IOException {
        StringWriter out = new StringWriter();
        JsonWriter generator = new JsonWriter(out);
        output.accept(generator);
        generator.flush();
        return out.toString();
    }

    public static <T> String assertIsEqualToDatabind(T obj, FailableBiConsumer<T, JsonWriter, IOException> output)
            throws IOException {
        String databind = new ObjectMapper().writeValueAsString(obj);
        String ours = serialize(obj, output);
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T, U> String assertIsEqualToDatabind2(
            T obj, U obj2, FailableTriConsumer<T, JsonWriter, U, IOException> output) throws IOException {
        return assertIsEqualToDatabind(obj, (o, writer) -> output.accept(o, writer, obj2));
    }

    public static <T> String serialize(T obj, FailableBiConsumer<T, JsonWriter, IOException> output)
            throws IOException {
        return withGsonJsonWriter(generator -> output.accept(obj, generator));
    }

    public static <T, U> String serialize2(T obj, U obj2, FailableTriConsumer<T, JsonWriter, U, IOException> output)
            throws IOException {
        return withGsonJsonWriter(generator -> output.accept(obj, generator, obj2));
    }

    public static <T> String assertIsEqualToDatabind(
            T obj, FailableTriConsumer<T, JsonWriter, SerializationContext, IOException> output) throws IOException {
        String databind = new ObjectMapper().writeValueAsString(obj);
        String ours = withGsonJsonWriter(generator -> output.accept(obj, generator, new SerializationContext()));
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T roundTrip(
            T obj,
            FailableBiConsumer<T, JsonWriter, IOException> output,
            FailableFunction<JsonReader, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        String json = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabind(json, input, typeRef);
    }

    public static <T, U> T roundTrip2(
            T obj,
            U obj2,
            FailableTriConsumer<T, JsonWriter, U, IOException> output,
            FailableBiFunction<JsonReader, U, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        String json = assertIsEqualToDatabind2(obj, obj2, output);
        return InputUtils.assertIsEqualToDatabind2(json, obj2, input, typeRef);
    }

    public static <T> T roundTripRecursive(
            T obj,
            FailableBiConsumer<T, JsonWriter, IOException> output,
            FailableFunction<JsonReader, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        String json = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabindComparingRecursively(json, input, typeRef);
    }

    public interface FailableTriConsumer<T, U, V, E extends Throwable> {
        void accept(T t, U u, V v) throws E;
    }
}
