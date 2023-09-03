package org.tillerino.scruse.processor;

import org.apache.commons.lang3.NotImplementedException;
import org.mapstruct.ap.internal.model.common.Type;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.Objects;

public record ScruseMethod(ScruseBlueprint blueprint, String name, ExecutableElement methodElement, InputOutput type) {
	enum InputOutput {
		INPUT, OUTPUT
	}

	/**
	 * Checks if reads/writes the given type and matches the signature of a reference method.
	 */
	public boolean matches(Types types, ScruseMethod referenceSignature, Type targetType) {
		if(type != referenceSignature.type) {
			return false;
		}

		List<? extends VariableElement> parameters = methodElement.getParameters();
		return switch (type) {
			case INPUT -> types.isSameType(methodElement.getReturnType(), targetType.getTypeMirror())
				&& parametersMatchStartingAt(types, parameters, referenceSignature.methodElement.getParameters(), 0);
			case OUTPUT -> types.isSameType(methodElement.getReturnType(), referenceSignature.methodElement.getReturnType())
				&& types.isSameType(methodElement.getParameters().get(0).asType(), targetType.getTypeMirror())
				&& parametersMatchStartingAt(types, parameters, referenceSignature.methodElement.getParameters(), 1);
		};
	}

	private static boolean parametersMatchStartingAt(Types types, List<? extends VariableElement> ps1, List<? extends VariableElement> ps2, int firstIndex) {
		if (ps1.size() != ps2.size() || firstIndex > ps1.size()) {
			return false;
		}
		for (int i = firstIndex; i < ps1.size(); i++) {
			if (!types.isSameType(ps1.get(i).asType(), ps2.get(i).asType())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return methodElement.toString();
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
