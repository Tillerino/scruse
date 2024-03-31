package org.tillerino.scruse.processor.util;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.*;
import javax.lang.model.util.AbstractTypeVisitor8;
import javax.lang.model.util.Types;

public class RebuildingTypeVisitor extends AbstractTypeVisitor8<TypeMirror, Types> {

    @Override
    public TypeMirror visitIntersection(IntersectionType t, Types types) {
        return t;
    }

    @Override
    public TypeMirror visitPrimitive(PrimitiveType t, Types types) {
        return t;
    }

    @Override
    public TypeMirror visitNull(NullType t, Types types) {
        return t;
    }

    @Override
    public TypeMirror visitArray(ArrayType t, Types types) {
        return types.getArrayType(t.getComponentType().accept(this, types));
    }

    @Override
    public TypeMirror visitDeclared(DeclaredType t, Types types) {
        return types.getDeclaredType(
                (TypeElement) t.asElement(),
                t.getTypeArguments().stream()
                        .map(arg -> arg.accept(this, types))
                        .toArray(TypeMirror[]::new));
    }

    @Override
    public TypeMirror visitError(ErrorType t, Types types) {
        return t;
    }

    @Override
    public TypeMirror visitTypeVariable(TypeVariable t, Types types) {
        return t;
    }

    @Override
    public TypeMirror visitWildcard(WildcardType t, Types types) {
        return t;
    }

    @Override
    public TypeMirror visitExecutable(ExecutableType t, Types types) {
        return null;
    }

    @Override
    public TypeMirror visitNoType(NoType t, Types types) {
        return t;
    }

    @Override
    public TypeMirror visitUnion(UnionType t, Types types) {
        return t;
    }
}
