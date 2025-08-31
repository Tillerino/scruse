package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.CodeBlock.Builder;
import jakarta.annotation.Nullable;
import java.util.*;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.ScrusePrototype;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.features.Polymorphism;

public abstract class AbstractCodeGeneratorStack<SELF extends AbstractCodeGeneratorStack<SELF>> {
    protected final AnnotationProcessorUtils utils;
    protected final GeneratedClass generatedClass;
    protected final ScrusePrototype prototype;
    protected final CodeBlock.Builder code;

    @Nullable
    protected final SELF parent;

    protected final Type type;
    protected final boolean stackRelevantType;

    @Nullable
    protected final Property property;

    protected final boolean canBePolyChild;

    protected final AnyConfig config;

    protected final Stack<Set<String>> variables;

    protected AbstractCodeGeneratorStack(
            AnnotationProcessorUtils utils,
            GeneratedClass generatedClass,
            ScrusePrototype prototype,
            Builder code,
            SELF parent,
            Type type,
            boolean stackRelevantType,
            @Nullable Property property,
            AnyConfig config) {
        this.prototype = prototype;
        this.utils = utils;
        this.type = type;
        this.code = code;
        this.parent = parent;
        this.generatedClass = Objects.requireNonNull(generatedClass);
        this.stackRelevantType = stackRelevantType;
        this.property = property;
        if (stackRelevantType && type.getTypeElement() != null) {
            config = AnyConfig.create(type.getTypeElement(), ConfigProperty.LocationKind.DTO, utils)
                    .merge(config);
        }
        this.config = config;
        this.canBePolyChild = prototype.contextParameter().isPresent()
                && stackDepth() == 1
                && Polymorphism.isSomeChild(type.getTypeMirror(), utils.types);
        if (parent != null) {
            variables = parent.variables;
        } else {
            variables = new Stack<>();
            variables.push(new LinkedHashSet<>());
        }
    }

    protected void detectSelfReferencingType() {
        if (stackRelevantType && parent != null && parent.stackContainsType(type)) {
            throw new ContextedRuntimeException(
                            "Self-referencing type detected. Define a separate method for this type.")
                    .addContextValue("type", type);
        }
    }

    boolean stackContainsType(Type type) {
        if ((stackRelevantType || parent == null) && this.type.equals(type)) {
            return true;
        }
        if (parent != null) {
            return parent.stackContainsType(type);
        }
        return false;
    }

    int stackDepth() {
        return parent != null ? 1 + parent.stackDepth() : 1;
    }

    protected String propertyName() {
        return property != null ? property.serializedName : parent != null ? parent.propertyName() : "root";
    }

    protected AbstractCodeGeneratorStack<SELF> addStatement(Snippet s) {
        code.addStatement(s.format(), s.args());
        return this;
    }

    protected AbstractCodeGeneratorStack<SELF> addStatement(String format, Object... args) {
        return addStatement(Snippet.of(format, args));
    }

    protected AbstractCodeGeneratorStack<SELF> beginControlFlow(Snippet s) {
        code.beginControlFlow(s.format(), s.args());
        variables.push(new LinkedHashSet<>(variables.peek()));
        return this;
    }

    protected AbstractCodeGeneratorStack<SELF> beginControlFlow(String controlFlow, Object... args) {
        return beginControlFlow(Snippet.of(controlFlow, args));
    }

    protected AbstractCodeGeneratorStack<SELF> nextControlFlow(Snippet s) {
        variables.pop();
        assert !variables.isEmpty();
        code.nextControlFlow(s.format(), s.args());
        variables.push(new LinkedHashSet<>(variables.peek()));
        return this;
    }

    protected AbstractCodeGeneratorStack<SELF> nextControlFlow(String controlFlow, Object... args) {
        return nextControlFlow(Snippet.of(controlFlow, args));
    }

    protected AbstractCodeGeneratorStack<SELF> endControlFlow() {
        variables.pop();
        assert !variables.isEmpty();
        code.endControlFlow();
        return this;
    }

    protected static Object[] flatten(Object... all) {
        List<Object> aggregator = new ArrayList<>();
        Snippet.collectInto(all, aggregator);
        return aggregator.toArray();
    }

    protected ScopedVar createVariable(String name) {
        if (variables.peek().add(name)) {
            return new ScopedVar(name);
        }
        int suf = 2;
        while (!variables.peek().add(name + suf)) {
            suf++;
        }
        return new ScopedVar(name + suf);
    }

    protected enum StringKind {
        STRING,
        CHAR_ARRAY
    }

    enum BinaryKind {
        BYTE_ARRAY
    }

    protected record Property(String canonicalName, String serializedName) {
        static Property ITEM = new Property("item", "item");
        static Property VALUE = new Property("value", "value");
        static Property DISCRIMINATOR = new Property("discriminator", "discriminator");
        static Property INSTANCE = new Property("instance", "instance");
    }

    protected record ScopedVar(String name) implements Snippet {
        @Override
        public String format() {
            return "$L";
        }

        @Override
        public Object[] args() {
            return new Object[] {name()};
        }
    }
}
