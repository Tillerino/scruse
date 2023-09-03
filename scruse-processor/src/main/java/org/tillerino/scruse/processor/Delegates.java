package org.tillerino.scruse.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.StringUtils;
import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName.TopLevelClassName;

import java.util.LinkedHashMap;
import java.util.Map;

public class Delegates {
	Map<String, Field> variables = new LinkedHashMap<>();

	public String getOrCreateField(ScruseBlueprint b) {
		return variables.computeIfAbsent(b.className().importName(),
			__ -> new Field(StringUtils.uncapitalize(b.className().className()) + "$" + variables.size() + "$delegate", b))
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
