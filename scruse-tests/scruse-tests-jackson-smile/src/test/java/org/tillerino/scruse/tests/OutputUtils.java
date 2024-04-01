package org.tillerino.scruse.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.smile.databind.SmileMapper;
import java.io.IOException;
import org.apache.commons.lang3.function.FailableBiConsumer;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableFunction;
import org.tillerino.scruse.api.DeserializationContext;
import org.tillerino.scruse.api.SerializationContext;

public class OutputUtils {

    public static <T> byte[] serialize(T obj, FailableBiConsumer<T, JsonGenerator, IOException> output)
            throws IOException {
        return ToShadeUtils.withJsonGenerator(generator -> output.accept(obj, generator));
    }

    public static <T, U> byte[] serialize2(T obj, U obj2, FailableTriConsumer<T, JsonGenerator, U, IOException> output)
            throws IOException {
        return ToShadeUtils.withJsonGenerator(generator -> output.accept(obj, generator, obj2));
    }

    public static <T> byte[] assertIsEqualToDatabind(T obj, FailableBiConsumer<T, JsonGenerator, IOException> output)
            throws IOException {
        byte[] databind = new SmileMapper().writeValueAsBytes(obj);
        byte[] ours = serialize(obj, output);
        assertThat(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> byte[] assertIsEqualToDatabind(
            T obj, FailableTriConsumer<T, JsonGenerator, SerializationContext, IOException> output) throws IOException {
        return assertIsEqualToDatabind2(
                obj, new SerializationContext(), (obj2, generator, context) -> output.accept(obj, generator, context));
    }

    public static <T, U> byte[] assertIsEqualToDatabind2(
            T obj, U obj2, FailableTriConsumer<T, JsonGenerator, U, IOException> output) throws IOException {
        byte[] databind = new SmileMapper().writeValueAsBytes(obj);
        byte[] ours = serialize2(obj, obj2, output);
        assertThat(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T roundTrip(
            T obj,
            FailableBiConsumer<T, JsonGenerator, IOException> output,
            FailableFunction<JsonParser, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        byte[] smile = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabind(smile, input, typeRef);
    }

    public static <T, U> T roundTrip2(
            T obj,
            U obj2,
            FailableTriConsumer<T, JsonGenerator, U, IOException> output,
            FailableBiFunction<JsonParser, U, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        byte[] smile = assertIsEqualToDatabind2(obj, obj2, output);
        return InputUtils.assertIsEqualToDatabind2(smile, obj2, input, typeRef);
    }

    public static <T> T roundTripContext(
            T obj,
            FailableTriConsumer<T, JsonGenerator, SerializationContext, IOException> output,
            FailableBiFunction<JsonParser, DeserializationContext, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        byte[] smile = assertIsEqualToDatabind2(obj, new SerializationContext(), output);
        return InputUtils.assertIsEqualToDatabind2(smile, new DeserializationContext(), input, typeRef);
    }

    public static <T> T roundTripRecursive(
            T obj,
            FailableBiConsumer<T, JsonGenerator, IOException> output,
            FailableFunction<JsonParser, T, IOException> input,
            TypeReference<T> typeRef)
            throws IOException {
        byte[] smile = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabindComparingRecursively(smile, input, typeRef);
    }

    public interface FailableTriConsumer<T, U, V, E extends Throwable> {
        void accept(T t, U u, V v) throws E;
    }
}
