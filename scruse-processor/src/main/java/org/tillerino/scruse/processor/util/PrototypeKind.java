package org.tillerino.scruse.processor.util;

import java.util.List;
import java.util.Optional;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

public sealed interface PrototypeKind {
    String JACKSON_JSON_GENERATOR = "com.fasterxml.jackson.core.JsonGenerator";
    String JACKSON_JSON_PARSER = "com.fasterxml.jackson.core.JsonParser";

    String JACKSON_JSON_NODE = "com.fasterxml.jackson.databind.JsonNode";

    String GSON_JSON_READER = "com.google.gson.stream.JsonReader";
    String GSON_JSON_WRITER = "com.google.gson.stream.JsonWriter";

    String FASTJSON_2_JSONREADER = "com.alibaba.fastjson2.JSONReader";
    String FASTJSON_2_JSONWRITER = "com.alibaba.fastjson2.JSONWriter";

    String MOSHI_JSON_READER = "com.squareup.moshi.JsonReader";
    String MOSHI_JSON_WRITER = "com.squareup.moshi.JsonWriter";

    record Input(TypeMirror jsonType, TypeMirror javaType, List<InstantiatedVariable> otherParameters)
            implements PrototypeKind {}

    record Output(TypeMirror jsonType, TypeMirror javaType, List<InstantiatedVariable> otherParameters)
            implements PrototypeKind {}

    record ReturningOutput(TypeMirror jsonType, TypeMirror javaType, List<InstantiatedVariable> otherParameters)
            implements PrototypeKind {}

    static Optional<PrototypeKind> of(InstantiatedMethod m) {
        if (m.element().getAnnotation(JsonInput.class) != null
                && m.returnType().getKind() != TypeKind.VOID
                && !m.parameters().isEmpty()
                && List.of(
                                JACKSON_JSON_PARSER,
                                JACKSON_JSON_NODE,
                                GSON_JSON_READER,
                                FASTJSON_2_JSONREADER,
                                MOSHI_JSON_READER)
                        .contains(m.parameters().get(0).type().toString())) {
            return Optional.of(new Input(
                    m.parameters().get(0).type(),
                    m.returnType(),
                    m.parameters().subList(1, m.parameters().size())));
        }
        if (m.element().getAnnotation(JsonOutput.class) != null
                && m.returnType().getKind() == TypeKind.VOID
                && m.parameters().size() >= 2
                && List.of(JACKSON_JSON_GENERATOR, GSON_JSON_WRITER, FASTJSON_2_JSONWRITER, MOSHI_JSON_WRITER)
                        .contains(m.parameters().get(1).type().toString())) {
            return Optional.of(new Output(
                    m.parameters().get(1).type(),
                    m.parameters().get(0).type(),
                    m.parameters().subList(2, m.parameters().size())));
        }
        if (m.element().getAnnotation(JsonOutput.class) != null
                && m.returnType().getKind() != TypeKind.VOID
                && !m.parameters().isEmpty()
                && List.of(JACKSON_JSON_NODE).contains(m.returnType().toString())) {
            return Optional.of(new ReturningOutput(
                    m.returnType(),
                    m.parameters().get(0).type(),
                    m.parameters().subList(1, m.parameters().size())));
        }
        return Optional.empty();
    }

    default Direction direction() {
        return this instanceof Input ? Direction.INPUT : Direction.OUTPUT;
    }

    TypeMirror jsonType();

    TypeMirror javaType();

    List<InstantiatedVariable> otherParameters();

    default boolean matchesWithJavaType(PrototypeKind other, TypeMirror javaType, AnnotationProcessorUtils utils) {
        return direction() == other.direction()
                && utils.types.isSameType(jsonType(), other.jsonType())
                && utils.types.isSameType(javaType(), javaType);
    }

    enum Direction {
        INPUT,
        OUTPUT;
    }
}
