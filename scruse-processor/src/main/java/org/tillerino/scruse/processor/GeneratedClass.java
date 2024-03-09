package org.tillerino.scruse.processor;

import com.squareup.javapoet.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName.TopLevelClassName;

import java.util.*;
import java.util.function.Consumer;

/**
 * Keeps track of the delegate readers/writers that are collected while processing a blueprint.
 */
public class GeneratedClass {
	Map<String, Field> variables = new LinkedHashMap<>();
	public final TypeSpec.Builder typeBuilder;
	public final List<Consumer<JavaFile.Builder>> fileBuilderMods = new ArrayList<>();

	public GeneratedClass(TypeSpec.Builder typeBuilder) {
		this.typeBuilder = typeBuilder;
	}


	/**
	 * Returns the variable name for the given blueprint. If it does not exist yet, it is created.
	 *
	 * @param caller the blueprint which is currently being processed
	 * @param callee the blueprint which is being called from caller
	 * @return the field name
	 */
	public String getOrCreateField(ScruseBlueprint caller, ScruseBlueprint callee) {
		if (caller == callee) {
			return "this";
		}
		return variables.computeIfAbsent(callee.className().importName(),
			__ -> new Field(StringUtils.uncapitalize(callee.className().className()) + "$" + variables.size() + "$delegate", callee))
			.variable();
	}

	public void buildFields(TypeSpec.Builder classBuilder) {
		for (Field value : variables.values()) {
			TopLevelClassName impl = value.blueprint().className().impl();
			FieldSpec.Builder field = FieldSpec.builder(TypeName.get(value.blueprint().typeElement().asType()), value.variable())
				.initializer("new $T()", ClassName.get(impl.packageName(), impl.className()));
			classBuilder.addField(field.build());
		}

	}

	record Field(String variable, ScruseBlueprint blueprint) {

	}
}
