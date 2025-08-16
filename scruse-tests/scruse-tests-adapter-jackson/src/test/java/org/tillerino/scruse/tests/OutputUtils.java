package org.tillerino.scruse.tests;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.function.FailableBiConsumer;
import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableFunction;
import org.tillerino.scruse.adapters.JacksonJsonGeneratorAdapter;
import org.tillerino.scruse.adapters.JacksonJsonParserAdapter;
import org.tillerino.scruse.api.SerializationContext;

public class OutputUtils {
    public static String withJsonGenerator(FailableConsumer<JacksonJsonGeneratorAdapter, Exception> output)
            throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = new JsonFactory().createGenerator(out, JsonEncoding.UTF8);
        output.accept(new JacksonJsonGeneratorAdapter(generator));
        generator.flush();
        return out.toString(StandardCharsets.UTF_8);
    }

    public static <T> String serialize(T obj, FailableBiConsumer<T, JacksonJsonGeneratorAdapter, Exception> output)
            throws Exception {
        return withJsonGenerator(generator -> output.accept(obj, generator));
    }

    public static <T, U> String serialize2(
            T obj, U obj2, FailableTriConsumer<T, JacksonJsonGeneratorAdapter, U, Exception> output) throws Exception {
        return withJsonGenerator(generator -> output.accept(obj, generator, obj2));
    }

    public static <T> String assertIsEqualToDatabind(
            T obj, FailableBiConsumer<T, JacksonJsonGeneratorAdapter, Exception> output) throws Exception {
        String databind = InputUtils.objectMapper.writeValueAsString(obj);
        String ours = serialize(obj, output);
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> String assertIsEqualToDatabind(
            T obj, FailableTriConsumer<T, JacksonJsonGeneratorAdapter, SerializationContext, Exception> output)
            throws Exception {
        return assertIsEqualToDatabind2(
                obj, new SerializationContext(), (obj2, generator, context) -> output.accept(obj, generator, context));
    }

    public static <T, U> String assertIsEqualToDatabind2(
            T obj, U obj2, FailableTriConsumer<T, JacksonJsonGeneratorAdapter, U, Exception> output) throws Exception {
        String databind = InputUtils.objectMapper.writeValueAsString(obj);
        String ours = serialize2(obj, obj2, output);
        assertThatJson(ours).isEqualTo(databind);
        return ours;
    }

    public static <T> T roundTrip(
            T obj,
            FailableBiConsumer<T, JacksonJsonGeneratorAdapter, Exception> output,
            FailableFunction<JacksonJsonParserAdapter, T, Exception> input,
            TypeReference<T> typeRef)
            throws Exception {
        String json = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabind(json, input, typeRef);
    }

    public static <T, U> T roundTrip2(
            T obj,
            U obj2,
            FailableTriConsumer<T, JacksonJsonGeneratorAdapter, U, Exception> output,
            FailableBiFunction<JacksonJsonParserAdapter, U, T, Exception> input,
            TypeReference<T> typeRef)
            throws Exception {
        String json = assertIsEqualToDatabind2(obj, obj2, output);
        return InputUtils.assertIsEqualToDatabind2(json, obj2, input, typeRef);
    }

    public static <T> T roundTripRecursive(
            T obj,
            FailableBiConsumer<T, JacksonJsonGeneratorAdapter, Exception> output,
            FailableFunction<JacksonJsonParserAdapter, T, Exception> input,
            TypeReference<T> typeRef)
            throws Exception {
        String json = assertIsEqualToDatabind(obj, output);
        return InputUtils.assertIsEqualToDatabindComparingRecursively(json, input, typeRef);
    }

    public interface FailableTriConsumer<T, U, V, E extends Throwable> {
        void accept(T t, U u, V v) throws E;
    }
}
