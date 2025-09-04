package org.tillerino.scruse.processor.util;

import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.*;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.config.AnyConfig;

/** Need this to instantiate generics. */
public record InstantiatedMethod(
        String name,
        TypeMirror returnType,
        List<InstantiatedVariable> parameters,
        ExecutableElement element,
        AnyConfig config)
        implements Named {
    public Snippet callSymbol(AnnotationProcessorUtils utils) {
        TypeMirror tm = element.getEnclosingElement().asType();
        TypeMirror raw = utils.types.erasure(tm);
        String diamond =
                (tm instanceof DeclaredType dt) && !dt.getTypeArguments().isEmpty() ? "<>" : "";
        return element.getKind() == ElementKind.CONSTRUCTOR
                ? Snippet.of("new $T$L", raw, diamond)
                : Snippet.of("$T.$L", raw, name);
    }

    public boolean hasSameSignature(InstantiatedMethod other, AnnotationProcessorUtils utils) {
        if (!utils.types.isSameType(returnType, other.returnType)) {
            return false;
        }
        if (parameters.size() != other.parameters.size()) {
            return false;
        }
        for (int i = 0; i < parameters.size(); i++) {
            if (!utils.types.isSameType(
                    parameters.get(i).type(), other.parameters.get(i).type())) {
                return false;
            }
        }
        return true;
    }

    public boolean hasParameterAssignableFrom(TypeMirror t, AnnotationProcessorUtils utils) {
        return parameters.stream().anyMatch(p -> utils.types.isAssignable(t, p.type));
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s.%s(%s)",
                ShortName.of(returnType),
                element.getEnclosingElement().getSimpleName(),
                name,
                parameters.stream().map(InstantiatedVariable::toString).collect(Collectors.joining(", ")));
    }

    public InstantiatedMethod withName(String name) {
        return new InstantiatedMethod(name, returnType, parameters, element, config);
    }

    public record InstantiatedVariable(TypeMirror type, String name, AnyConfig config) implements Snippet {
        @Override
        public String toString() {
            return ShortName.of(type) + " " + name();
        }

        @Override
        public String format() {
            return "$L";
        }

        @Override
        public Object[] args() {
            return new Object[] {name};
        }
    }
}
