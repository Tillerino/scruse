package org.tillerino.scruse.processor;

import com.squareup.javapoet.*;
import org.apache.commons.lang3.StringUtils;
import org.tillerino.scruse.helpers.EnumHelper;
import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName.TopLevelClassName;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.*;
import java.util.function.Consumer;

/**
 * Keeps track of the delegate readers/writers that are collected while processing a blueprint.
 */
public class GeneratedClass {
	Map<String, DelegateeField> delegateeFields = new LinkedHashMap<>();
	Map<String, EnumValuesField> enumFields = new LinkedHashMap<>();
	public final TypeSpec.Builder typeBuilder;
	public final List<Consumer<JavaFile.Builder>> fileBuilderMods = new ArrayList<>();

	public GeneratedClass(TypeSpec.Builder typeBuilder) {
		this.typeBuilder = typeBuilder;
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
		return delegateeFields.computeIfAbsent(callee.className().importName(),
			__ -> new DelegateeField(StringUtils.uncapitalize(callee.className().className()) + "$" + delegateeFields.size() + "$delegate", callee))
			.name();
	}

	public String getOrCreateEnumField(TypeMirror enumType) {
		return enumFields.computeIfAbsent(enumType.toString(),
			__ -> new EnumValuesField(StringUtils.uncapitalize(((DeclaredType) enumType).asElement().getSimpleName().toString()) + "$" + enumFields.size() + "$values", enumType, "name"))
			.name();
	}

	public void buildFields(TypeSpec.Builder classBuilder) {
        delegateeFields.values().forEach(value -> value.writeField(classBuilder));
		enumFields.values().forEach(value -> value.writeField(classBuilder));
	}

	record DelegateeField(String name, ScruseBlueprint blueprint) {
		private void writeField(TypeSpec.Builder classBuilder) {
			TopLevelClassName impl = this.blueprint().className().impl();
			FieldSpec.Builder field = FieldSpec.builder(TypeName.get(this.blueprint().typeElement().asType()), this.name())
				.initializer("new $T()", ClassName.get(impl.packageName(), impl.className()));
			classBuilder.addField(field.build());
		}
	}

	record EnumValuesField(String name, TypeMirror type, String valueFunction) {
		private void writeField(TypeSpec.Builder classBuilder) {
			FieldSpec.Builder field = FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Map.class), TypeName.get(String.class), TypeName.get(type)), name)
					.initializer("$T.buildValuesMap($T.class, $T::$L)", TypeName.get(EnumHelper.class), type, type, valueFunction);
			classBuilder.addField(field.build());
		}
	}
}
