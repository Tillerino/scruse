package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    protected final String property;

    protected final boolean canBePolyChild;

    protected final AnyConfig config;

    protected AbstractCodeGeneratorStack(
            AnnotationProcessorUtils utils,
            GeneratedClass generatedClass,
            ScrusePrototype prototype,
            CodeBlock.Builder code,
            SELF parent,
            Type type,
            boolean stackRelevantType,
            @Nullable String property,
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

    protected StringBuilder stack() {
        if (parent != null) {
            if (property == null) {
                return parent.stack();
            }
            return parent.stack().append(" -> ").append(property).append(": ").append(type.getName());
        }
        if (property == null) {
            return new StringBuilder(type.getName());
        }
        return new StringBuilder(property + ": " + type.getName());
    }

    protected String propertyName() {
        return property != null ? property : parent != null ? parent.propertyName() : "root";
    }

    protected static Object[] flatten(Object... all) {
        List<Object> aggregator = new ArrayList<>();
        Snippet.collectInto(all, aggregator);
        return aggregator.toArray();
    }

    protected enum StringKind {
        STRING,
        CHAR_ARRAY
    }

    enum BinaryKind {
        BYTE_ARRAY
    }
}
