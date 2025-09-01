package org.tillerino.scruse.processor.util;

import java.util.stream.Collectors;
import javax.lang.model.type.*;
import javax.lang.model.util.AbstractTypeVisitor14;

public class ShortName extends AbstractTypeVisitor14<String, Object> {
    public static final ShortName INSTANCE = new ShortName();

    public static String of(TypeMirror t) {
        return t.accept(INSTANCE, null);
    }

    @Override
    public String visitIntersection(IntersectionType t, Object o) {
        return t.getBounds().stream().map(b -> b.accept(this, null)).collect(Collectors.joining(" & "));
    }

    @Override
    public String visitPrimitive(PrimitiveType t, Object o) {
        return t.toString();
    }

    @Override
    public String visitNull(NullType t, Object o) {
        return "null";
    }

    @Override
    public String visitArray(ArrayType t, Object o) {
        return t.getComponentType().accept(this, null) + "[]";
    }

    @Override
    public String visitDeclared(DeclaredType t, Object o) {
        String name = t.asElement().getSimpleName().toString();
        if (!t.getTypeArguments().isEmpty()) {
            name += t.getTypeArguments().stream()
                    .map(t2 -> t2.accept(this, null))
                    .collect(Collectors.joining(", ", "<", ">"));
        }
        return name;
    }

    @Override
    public String visitError(ErrorType t, Object o) {
        return "error";
    }

    @Override
    public String visitTypeVariable(TypeVariable t, Object o) {
        return t.toString();
    }

    @Override
    public String visitWildcard(WildcardType t, Object o) {
        if (t.getExtendsBound() != null) {
            return "? extends " + t.getExtendsBound().accept(this, null);
        }
        if (t.getSuperBound() != null) {
            return "? super " + t.getSuperBound().accept(this, null);
        }
        return "?";
    }

    @Override
    public String visitExecutable(ExecutableType t, Object o) {
        return "executable";
    }

    @Override
    public String visitNoType(NoType t, Object o) {
        return "no type";
    }

    @Override
    public String visitUnion(UnionType t, Object o) {
        return t.getAlternatives().stream().map(b -> b.accept(this, null)).collect(Collectors.joining(" | "));
    }
}
