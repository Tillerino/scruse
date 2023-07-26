package org.tillerino.scruse.processor;

import com.squareup.javapoet.MethodSpec;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.*;

public record CompositeTypeUtils(AnnotationProcessorUtils utils) {
	List<CompositeTypeComponent> listComponents(TypeElement element) {
		List<CompositeTypeComponent> elements = new ArrayList<>();
		if (element.getKind() == ElementKind.RECORD) {
			for (RecordComponentElement recordComponent : element.getRecordComponents()) {
				elements.add(new CompositeTypeComponent.RecordComponent(recordComponent));
			}
		} else if (element.getKind() == ElementKind.CLASS) {
			Map<String, CompositeTypeComponent> byName = new LinkedHashMap<>();
			for (ExecutableElement method : ElementFilter.methodsIn(element.getEnclosedElements())) {
				List<? extends AnnotationMirror> annotationMirrors = method.getAnnotationMirrors();
				Set<Modifier> modifiers = method.getModifiers();
				if (modifiers.contains(Modifier.STATIC) || !modifiers.contains(Modifier.PUBLIC) || utils.isJsonIgnore(annotationMirrors)
						|| !method.getParameters().isEmpty() || method.getReturnType().getKind() == TypeKind.VOID) {
					continue;
				}
				String name = method.getSimpleName().toString();
				if (name.startsWith("get") && name.length() > 3 && Character.isUpperCase(name.charAt(3))) {
					String propertyName = StringUtils.uncapitalize(name.substring(3));
					byName.put(propertyName, new CompositeTypeComponent.Getter(method, propertyName));
				}
				if (name.startsWith("is") && name.length() > 2 && Character.isUpperCase(name.charAt(2))
					&& (method.getReturnType().getKind() == TypeKind.BOOLEAN || utils.types.isSameType(method.getReturnType(), utils.commonTypes.boxedBoolean))) {
					String propertyName = StringUtils.uncapitalize(name.substring(2));
					byName.put(propertyName, new CompositeTypeComponent.Getter(method, propertyName));
				}
			}
			elements.addAll(byName.values());
		} else {
			throw new IllegalArgumentException("Unsupported element kind: " + element.getKind());
		}
		return elements;
	}

	sealed interface CompositeTypeComponent {
		String propertyName();

		TypeMirror typeMirror();

		void getToVariable(String compositeVariableName, String targetVariableName, MethodSpec.Builder builder);

		record RecordComponent(RecordComponentElement element) implements CompositeTypeComponent {
			@Override
			public String propertyName() {
				return element.getSimpleName().toString();
			}

			@Override
			public TypeMirror typeMirror() {
				return element.asType();
			}

			@Override
			public void getToVariable(String compositeVariableName, String targetVariableName, MethodSpec.Builder builder) {
				builder.addStatement("$T $L = $L.$L()", typeMirror(), targetVariableName, compositeVariableName, propertyName());
			}
		}

		record Getter(ExecutableElement element, String propertyName) implements CompositeTypeComponent {
			@Override
			public String propertyName() {
				return propertyName;
			}

			@Override
			public TypeMirror typeMirror() {
				return element.getReturnType();
			}

			@Override
			public void getToVariable(String compositeVariableName, String targetVariableName, MethodSpec.Builder builder) {
				String typeName = typeMirror().toString();

				if (typeName.equals("boolean") || typeName.equals("java.lang.Boolean")) {
					builder.addStatement("$T $L = $L.$L()", typeMirror(), targetVariableName, compositeVariableName, element.getSimpleName().toString());
				} else {
					builder.addStatement("$T $L = $L.$L()", typeMirror(), targetVariableName, compositeVariableName, element.getSimpleName().toString());
				}
			}
		}
	}
}
