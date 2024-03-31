package org.tillerino.scruse.processor;

import com.squareup.javapoet.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.tillerino.scruse.helpers.EnumHelper;
import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName.TopLevelClassName;
import org.tillerino.scruse.processor.util.InstantiatedMethod;

/** Keeps track of the delegate readers/writers that are collected while processing a blueprint. */
public class GeneratedClass {
    Map<String, DelegateeField> delegateeFields = new LinkedHashMap<>();
    Map<String, EnumValuesField> enumFields = new LinkedHashMap<>();
    public final TypeSpec.Builder typeBuilder;
    public final List<Consumer<JavaFile.Builder>> fileBuilderMods = new ArrayList<>();
    private final AnnotationProcessorUtils utils;
    private final ScruseBlueprint blueprint;

    public GeneratedClass(TypeSpec.Builder typeBuilder, AnnotationProcessorUtils utils, ScruseBlueprint blueprint) {
        this.typeBuilder = typeBuilder;
        this.utils = utils;
        this.blueprint = blueprint;
    }

    /**
     * Returns the field name for the given blueprint. If it does not exist yet, it is created.
     *
     * @param caller the blueprint which is currently being processed
     * @param callee the blueprint which is being called from caller
     * @return the field name
     */
    public String getOrCreateDelegateeField(ScruseBlueprint caller, ScruseBlueprint callee) {
        if (caller == callee) {
            return "this";
        }
        return delegateeFields
                .computeIfAbsent(
                        callee.className.importName(),
                        __ -> new DelegateeField(
                                StringUtils.uncapitalize(callee.className.className()) + "$" + delegateeFields.size()
                                        + "$delegate",
                                callee))
                .name();
    }

    public String getOrCreateUsedBlueprintWithTypeField(TypeMirror targetType) {
        return getOrCreateUsedBlueprintWithTypeField(targetType, blueprint);
    }

    private String getOrCreateUsedBlueprintWithTypeField(TypeMirror targetType, ScruseBlueprint calleeBlueprint) {
        if (utils.types.isAssignable(calleeBlueprint.typeElement.asType(), targetType)) {
            return getOrCreateDelegateeField(this.blueprint, calleeBlueprint);
        }
        for (ScruseBlueprint use : calleeBlueprint.config.uses()) {
            String found = getOrCreateUsedBlueprintWithTypeField(targetType, use);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public Pair<String, String> getOrCreateLambda(TypeMirror targetType) {
        if (!(targetType instanceof DeclaredType d)) {
            return null;
        }
        TypeElement typeElement = (TypeElement) d.asElement();
        if (!typeElement.getKind().isInterface()) {
            return null;
        }
        Stream<ExecutableElement> methods = ElementFilter.methodsIn(utils.elements.getAllMembers(typeElement)).stream()
                .filter(method -> !method.getEnclosingElement().toString().equals("java.lang.Object"));
        if (methods.count() != 1) {
            return null;
        }
        InstantiatedMethod targetMethod = utils.generics.instantiateMethods(d).get(0);
        for (ScruseMethod method : blueprint.methods) {
            if (method.asInstantiatedMethod().sameTypes(targetMethod, utils)) {
                return Pair.of(getOrCreateDelegateeField(blueprint, blueprint), method.name());
            }
        }
        for (ScruseBlueprint use : blueprint.config.uses()) {
            for (ScruseMethod method : use.methods) {
                if (method.asInstantiatedMethod().sameTypes(targetMethod, utils)) {
                    return Pair.of(getOrCreateDelegateeField(blueprint, use), method.name());
                }
            }
        }
        return null;
    }

    public String getOrCreateEnumField(TypeMirror enumType) {
        return enumFields
                .computeIfAbsent(
                        enumType.toString(),
                        __ -> new EnumValuesField(
                                StringUtils.uncapitalize(((DeclaredType) enumType)
                                                .asElement()
                                                .getSimpleName()
                                                .toString())
                                        + "$" + enumFields.size() + "$values",
                                enumType,
                                "name"))
                .name();
    }

    public void buildFields(TypeSpec.Builder classBuilder) {
        delegateeFields.values().forEach(value -> value.writeField(classBuilder));
        enumFields.values().forEach(value -> value.writeField(classBuilder));
    }

    record DelegateeField(String name, ScruseBlueprint blueprint) {
        private void writeField(TypeSpec.Builder classBuilder) {
            TopLevelClassName impl = this.blueprint().className.impl();
            FieldSpec.Builder field = FieldSpec.builder(
                            TypeName.get(this.blueprint().typeElement.asType()), this.name())
                    .initializer("new $T()", ClassName.get(impl.packageName(), impl.className()));
            classBuilder.addField(field.build());
        }
    }

    record EnumValuesField(String name, TypeMirror type, String valueFunction) {
        private void writeField(TypeSpec.Builder classBuilder) {
            FieldSpec.Builder field = FieldSpec.builder(
                            ParameterizedTypeName.get(
                                    ClassName.get(Map.class), TypeName.get(String.class), TypeName.get(type)),
                            name)
                    .initializer(
                            "$T.buildValuesMap($T.class, $T::$L)",
                            TypeName.get(EnumHelper.class),
                            type,
                            type,
                            valueFunction);
            classBuilder.addField(field.build());
        }
    }
}
