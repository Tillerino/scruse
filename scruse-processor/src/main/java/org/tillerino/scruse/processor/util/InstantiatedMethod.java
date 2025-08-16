package org.tillerino.scruse.processor.util;

import java.util.List;
import java.util.stream.Collectors;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.Snippet;

/** Need this to instantiate generics. */
public record InstantiatedMethod(
        String name, TypeMirror returnType, List<InstantiatedVariable> parameters, ExecutableElement element)
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

    public boolean sameTypes(InstantiatedMethod other, AnnotationProcessorUtils utils) {
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

    @Override
    public String toString() {
        return String.format(
                "%s %s.%s(%s)",
                returnType,
                element.getEnclosingElement().getSimpleName(),
                name,
                parameters.stream().map(InstantiatedVariable::toString).collect(Collectors.joining(", ")));
    }
}
