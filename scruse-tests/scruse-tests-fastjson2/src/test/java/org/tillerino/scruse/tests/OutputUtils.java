package org.tillerino.scruse.tests;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.function.FailableBiConsumer;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.tillerino.scruse.api.DeserializationContext;
import org.tillerino.scruse.api.SerializationContext;

public class OutputUtils {

    public static String withWriter(FailableConsumer<JSONWriter, IOException> output) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JSONWriter writer = JSONWriter.of();
        output.accept(writer);
        writer.flushTo(out);
        return out.toString(StandardCharsets.UTF_8);
    }

    public static <T> String assertIsEqualToDatabind(T obj, FailableBiConsumer<T, JSONWriter, IOException> output)
            throws IOException {
        String databind = new ObjectMapper().writeValueAsString(obj);
        String ours = serialize(obj, output);
        System.out.println(ours);
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T, U> String assertIsEqualToDatabind2(
            T obj, U obj2, FailableTriConsumer<T, JSONWriter, U, IOException> output) throws IOException {
        return assertIsEqualToDatabind(obj, (o, writer) -> output.accept(o, writer, obj2));
    }

    public static <T> String serialize(T obj, FailableBiConsumer<T, JSONWriter, IOException> output)
            throws IOException {
        return withWriter(generator -> output.accept(obj, generator));
    }

    public static <T, U> String serialize2(T obj, U obj2, FailableTriConsumer<T, JSONWriter, U, IOException> output)
            throws IOException {
        return withWriter(generator -> output.accept(obj, generator, obj2));
    }

    public static <T> String assertIsEqualToDatabind(
            T obj, FailableTriConsumer<T, JSONWriter, SerializationContext, IOException> output) throws IOException {
        String databind = new ObjectMapper().writeValueAsString(obj);
        String ours = withWriter(generator -> output.accept(obj, generator, new SerializationContext()));
        System.out.println(ours);
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T roundTrip(
            T obj,
            FailableBiConsumer<T, JSONWriter, IOException> output,
            FailableFunction<JSONReader, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        String json = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabind(json, input, typeRef);
    }

    public static <T, U> T roundTrip2(
            T obj,
            U obj2,
            FailableTriConsumer<T, JSONWriter, U, IOException> output,
            FailableBiFunction<JSONReader, U, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        String json = assertIsEqualToDatabind2(obj, obj2, output);
        return InputUtils.assertIsEqualToDatabind2(json, obj2, input, typeRef);
    }

    public static <T> T roundTripContext(
            T obj,
            FailableTriConsumer<T, JSONWriter, SerializationContext, IOException> output,
            FailableBiFunction<JSONReader, DeserializationContext, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        String json = assertIsEqualToDatabind2(obj, new SerializationContext(), output);
        return InputUtils.assertIsEqualToDatabind2(json, new DeserializationContext(), input, typeRef);
    }

    public static <T> T roundTripRecursive(
            T obj,
            FailableBiConsumer<T, JSONWriter, IOException> output,
            FailableFunction<JSONReader, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        String json = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabindComparingRecursively(json, input, typeRef);
    }

    public interface FailableTriConsumer<T, U, V, E extends Throwable> {
        void accept(T t, U u, V v) throws E;
    }
}
