package org.tillerino.scruse.processor;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.util.Config;
import org.tillerino.scruse.processor.util.Generics.TypeVar;
import org.tillerino.scruse.processor.util.InstantiatedMethod;
import org.tillerino.scruse.processor.util.InstantiatedVariable;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Accessor object for a method which is annotated with {@link org.tillerino.scruse.annotations.JsonInput} or {@link org.tillerino.scruse.annotations.JsonOutput}.
 * @param jsonType the reader or writer type from the underlying json parser/formatter library
 * @param javaType the java type that is converted to/from json
 */
public record ScruseMethod(ScruseBlueprint blueprint, String name, ExecutableElement methodElement, InputOutput direction,
		AnnotationProcessorUtils utils, TypeMirror instantiatedReturnType, List<InstantiatedVariable> instantiatedParameters,
		TypeMirror jsonType, TypeMirror javaType, Config config) {

	enum InputOutput {
		INPUT, OUTPUT;
	}

	static ScruseMethod of(ScruseBlueprint blueprint, ExecutableElement methodElement, InputOutput type, AnnotationProcessorUtils utils) {
		TypeMirror instantiatedReturnType = utils.generics.applyTypeBindings(methodElement.getReturnType(), blueprint.typeBindings);
		List<InstantiatedVariable> instantiatedParameters = methodElement.getParameters().stream()
				.map(p -> new InstantiatedVariable(utils.generics.applyTypeBindings(p.asType(), blueprint.typeBindings), p.getSimpleName().toString()))
				.toList();

		TypeMirror javaType = type == InputOutput.INPUT ? instantiatedReturnType : instantiatedParameters.get(0).type();
		TypeMirror jsonType = type == InputOutput.INPUT ? instantiatedParameters.get(0).type()
				: methodElement.getReturnType().getKind() != TypeKind.VOID ? instantiatedReturnType : instantiatedParameters.get(1).type();

		Config config = Config.defaultConfig(methodElement, utils).merge(blueprint.config);

		return new ScruseMethod(blueprint, methodElement.getSimpleName().toString(), methodElement, type, utils,
			instantiatedReturnType, instantiatedParameters,
			jsonType, javaType,
			config);
	}

	/**
	 * Checks if reads/writes the given type and matches the signature of a reference method.
	 */
	public InstantiatedMethod matches(ScruseMethod referenceSignature, Type targetType, boolean allowExact) {
		if(direction != referenceSignature.direction || !utils.types.isSameType(jsonType, referenceSignature.jsonType)) {
			return null;
		}

		LinkedHashSet<TypeVar> localTypeVars = methodElement.getTypeParameters().stream()
			.map(TypeParameterElement::asType)
			.map(TypeVariable.class::cast)
			.map(TypeVar::of).collect(Collectors.toCollection(LinkedHashSet::new));
		LinkedHashMap<TypeVar, TypeMirror> typeBindings = new LinkedHashMap<>();

		if (isSameTypeWithBindings(javaType, targetType.getTypeMirror(), localTypeVars, typeBindings)) {
			if (!allowExact && typeBindings.isEmpty()) {
				return null;
			}
			return utils.generics.applyTypeBindings(typeBindings, this.asInstantiatedMethod());
		}
		return null;
	}

	private static boolean isSameTypeWithBindings(TypeMirror javaType, TypeMirror targetType,
			 LinkedHashSet<TypeVar> localTypeVars, LinkedHashMap<TypeVar, TypeMirror> typeBindings) {
		if (javaType instanceof TypeVariable t) {
			TypeVar typeVar = TypeVar.of(t);
			if (typeBindings.containsKey(typeVar)) {
				return typeBindings.get(typeVar).equals(targetType);
			}
			if (localTypeVars.contains(typeVar)) {
				typeBindings.put(typeVar, targetType);
				return true;
			}
			return false;
		}
		if (javaType instanceof DeclaredType t) {
			if (!(targetType instanceof DeclaredType tt) || !t.asElement().equals(tt.asElement())) {
				return false;
			}
			for (int i = 0; i < t.getTypeArguments().size(); i++) {
				TypeMirror type = t.getTypeArguments().get(i);
				TypeMirror targetTypeArg = tt.getTypeArguments().get(i);
				if (!isSameTypeWithBindings(type, targetTypeArg, localTypeVars, typeBindings)) {
					return false;
				}
			}
			return true;
		}
		if (javaType instanceof PrimitiveType p && targetType instanceof PrimitiveType pt) {
			return p.getKind().equals(pt.getKind());
		}
		return false;
	}

	public Optional<VariableElement> contextParameter() {
		for (VariableElement parameter : methodElement.getParameters()) {
			if (utils.types.isAssignable(parameter.asType(), switch (direction) {
				case INPUT -> utils.commonTypes.deserializationContext;
				case OUTPUT -> utils.commonTypes.serializationContext;
			})) {
				return Optional.of(parameter);
			}
		}
		return Optional.empty();
	}

	public List<Snippet> findArguments(InstantiatedMethod callee, int firstArgument, GeneratedClass generatedClass) {
		List<Snippet> arguments = new ArrayList<>();
		calleeParameter: for (int i = firstArgument; i < callee.parameters().size(); i++) {
			TypeMirror calleeParameterType = callee.parameters().get(i).type();
			// search in caller's own parameters
			for (int j = 0; j < methodElement.getParameters().size(); j++) {
				if (utils.types.isAssignable(methodElement.getParameters().get(j).asType(), calleeParameterType)) {
					arguments.add(Snippet.of("$L", methodElement.getParameters().get(j)));
					continue calleeParameter;
				}
			}
			// see if we can instantiate an instance from our list of used blueprints
			String delegateeInField = generatedClass.getOrCreateUsedBlueprintWithTypeField(calleeParameterType);
			if (delegateeInField != null) {
				arguments.add(Snippet.of("$L", delegateeInField));
				continue calleeParameter;
			}
			throw new ContextedRuntimeException("Could not find value to pass to method argument")
					.addContextValue("caller", this.methodElement)
					.addContextValue("callee", callee)
					.addContextValue("argument", callee.parameters().get(i));
		}
		return arguments;
	}

	@Override
	public String toString() {
		return blueprint + "." + name + instantiatedParameters().stream()
			.map(InstantiatedVariable::toString)
			.collect(Collectors.joining(", ", "(", ")"));
	}

	public InstantiatedMethod asInstantiatedMethod() {
		return new InstantiatedMethod(name, instantiatedReturnType, instantiatedParameters, methodElement);
	}

	@Override
	public int hashCode() {
		throw new NotImplementedException("hashCode");
	}

	@Override
	public boolean equals(Object obj) {
		throw new NotImplementedException("equals");
	}

}
