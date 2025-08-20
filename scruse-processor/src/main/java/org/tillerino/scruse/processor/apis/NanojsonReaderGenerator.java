package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import java.io.IOException;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

public class NanojsonReaderGenerator extends AbstractReaderGenerator<NanojsonReaderGenerator> {
    private final VariableElement parserVariable;

    public NanojsonReaderGenerator(
            AnnotationProcessorUtils utils, ScrusePrototype prototype, GeneratedClass generatedClass) {
        super(utils, prototype, generatedClass);
        parserVariable = prototype.methodElement().getParameters().get(0);
    }

    public NanojsonReaderGenerator(
            ScrusePrototype prototype,
            AnnotationProcessorUtils utils,
            Type type,
            String propertyName,
            CodeBlock.Builder code,
            VariableElement parserVariable,
            LHS lhs,
            NanojsonReaderGenerator parent,
            boolean stackRelevantType,
            AnyConfig config) {
        super(
                utils,
                parent.generatedClass,
                prototype,
                code,
                parent,
                type,
                stackRelevantType,
                propertyName,
                lhs,
                config);
        this.parserVariable = parserVariable;
    }

    @Override
    protected void startStringCase(Branch branch) {
        branch.controlFlow(code, "$L.current() == $L", parserVariable.getSimpleName(), token("TOKEN_STRING"));
    }

    @Override
    protected void startNumberCase(Branch branch) {
        branch.controlFlow(code, "$L.current() == $L", parserVariable.getSimpleName(), token("TOKEN_NUMBER"));
    }

    @Override
    protected Snippet objectCaseCondition() {
        //        importHelper();
        return Snippet.of("$L.nextIfCurrentTokenIs($L)", parserVariable.getSimpleName(), token("TOKEN_OBJECT_START"));
    }

    @Override
    protected void startArrayCase(Branch branch) {
        branch.controlFlow(code, "$L.current() == $L", parserVariable.getSimpleName(), token("TOKEN_ARRAY_START"));
        advance();
    }

    @Override
    protected void startBooleanCase(Branch branch) {
        branch.controlFlow(
                code,
                "$L.current() == $L || $L.current() == $L",
                parserVariable.getSimpleName(),
                token("TOKEN_TRUE"),
                parserVariable.getSimpleName(),
                token("TOKEN_FALSE"));
    }

    @Override
    protected void startFieldCase(Branch branch) {
        branch.controlFlow(code, "$L.current() == $L", parserVariable.getSimpleName(), token("TOKEN_STRING"));
    }

    @Override
    protected void initializeParser() {
        // nothing to do, reader always starts with a token, yay!
    }

    @Override
    protected Snippet nullCaseCondition() {
        //        importHelper();
        return Snippet.of("$L.nextIfCurrentTokenIs($L)", parserVariable.getSimpleName(), token("TOKEN_NULL"));
    }

    @Override
    protected void readPrimitive(TypeMirror type) {
        Pair<String, String> readMethod =
                switch (type.getKind()) {
                    case BOOLEAN -> Pair.of(null, "bool");
                    case BYTE -> Pair.of("byte", "intVal");
                    case SHORT -> Pair.of("short", "intVal");
                    case INT -> Pair.of(null, "intVal");
                    case LONG -> Pair.of("long", "longVal");
                    case FLOAT -> Pair.of(null, "floatVal");
                    case DOUBLE -> Pair.of(null, "doubleVal");
                    default -> throw new ContextedRuntimeException(
                            type.getKind().toString());
                };
        String cast = readMethod.getLeft() == null ? "" : "(" + readMethod.getLeft() + ") ";
        String method = readMethod.getRight();
        if (lhs instanceof LHS.Return) {
            String tmp = "tmp$" + stackDepth();
            code.addStatement("$T $L = $L$L.$L()", type, tmp, cast, parserVariable.getSimpleName(), method);
            advance();
            code.addStatement("return $L", tmp);
        } else {
            lhs.assign(code, "$L$L.$L()", cast, parserVariable.getSimpleName(), method);
            advance();
        }
    }

    @Override
    protected void readString(StringKind stringKind) {
        String conversion =
                switch (stringKind) {
                    case STRING -> "";
                    case CHAR_ARRAY -> ".toCharArray()";
                };
        if (lhs instanceof LHS.Return) {
            String tmp = "tmp$" + stackDepth();
            code.addStatement(
                    "$T $L = $L.string()$L",
                    stringKind == StringKind.STRING ? String.class : char[].class,
                    tmp,
                    parserVariable.getSimpleName(),
                    conversion);
            advance();
            code.addStatement("return $L", tmp);
        } else {
            lhs.assign(code, "$L.string()$L", parserVariable.getSimpleName(), conversion);
            advance();
        }
    }

    @Override
    protected void iterateOverFields() {
        code.beginControlFlow("while ($L.objectLoop())", parserVariable.getSimpleName());
    }

    @Override
    protected void skipValue() {
        code.addStatement("$L.skipChildren()", parserVariable.getSimpleName());
        code.addStatement("$L.next()", parserVariable.getSimpleName());
    }

    @Override
    protected void afterObject() {}

    @Override
    protected void readFieldNameInIteration(String propertyName) {
        code.addStatement("String $L = $L.fieldNameAndSkipColon()", propertyName, parserVariable.getSimpleName());
    }

    @Override
    protected void readDiscriminator(String propertyName) {
        //        importHelper();
        lhs.assign(code, "$L.readDiscriminator($S)", parserVariable.getSimpleName(), propertyName);
    }

    @Override
    protected void iterateOverElements() {
        code.beginControlFlow("while ($L.arrayLoop())", parserVariable.getSimpleName());
    }

    @Override
    protected void afterArray() {
        // we skipped the END_ARRAY token in the head of the loop
    }

    @Override
    protected void throwUnexpected(String expected) {
        code.addStatement(
                "throw new $T($S + $L.current())",
                IOException.class,
                "Expected " + expected + ", got ",
                parserVariable.getSimpleName());
    }

    @Override
    protected void invokeDelegate(String instance, InstantiatedMethod callee) {
        lhs.assign(
                code,
                Snippet.of(
                        "$L.$L($C)",
                        instance,
                        callee,
                        Snippet.join(prototype.findArguments(callee, 0, generatedClass), ", ")));
    }

    @Override
    protected NanojsonReaderGenerator nest(
            TypeMirror type, String propertyName, LHS lhs, boolean stackRelevantType, AnyConfig config) {
        return new NanojsonReaderGenerator(
                prototype,
                utils,
                utils.tf.getType(type),
                propertyName,
                code,
                parserVariable,
                lhs,
                this,
                stackRelevantType,
                config);
    }

    private String token(String t) {
        generatedClass.fileBuilderMods.add(
                builder -> builder.addStaticImport(ClassName.get("com.grack.nanojson", "TokenerWrapper"), "*"));
        return t;
    }

    private void advance() {
        code.addStatement("$L.next()", parserVariable.getSimpleName());
    }
}
