package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.annotation.Nullable;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public abstract class AbstractCodeGeneratorStack<SELF extends AbstractCodeGeneratorStack<SELF>> {
	protected final AnnotationProcessorUtils utils;
	protected final Type type;
	protected final Key key;
	protected final CodeBlock.Builder code;
	protected final Mode mode;
	protected final SELF parent;

	protected AbstractCodeGeneratorStack(AnnotationProcessorUtils utils, Type type, Key key, CodeBlock.Builder code, Mode mode, SELF parent) {
		this.utils = utils;
		this.type = type;
		this.key = key;
		this.code = code;
		this.mode = mode;
		this.parent = parent;
	}

	enum Mode {
		ROOT,
		IN_OBJECT,
		IN_ARRAY
	}

	String varName() {
		if (mode == Mode.ROOT) {
			// in this case, the name is determined by the method parameter
			return key.propertyName();
		}
		return key.propertyName() + "$" + stackDepth();
	}

	int stackDepth() {
		return parent != null ? 1 + parent.stackDepth() : 1;
	}

	SELF nestIntoObject(TypeMirror type, String propertyName) {
		return nest(type, new Key(propertyName, propertyName + "$" + (stackDepth() + 1), "$S", propertyName), Mode.IN_OBJECT);
	}

	SELF nestIntoArray(TypeMirror type, String elemName) {
		return nest(type, new Key("element", elemName + "$" + (stackDepth() + 1), null, null), Mode.IN_ARRAY);
	}

	SELF nestIntoMap(TypeMirror type, String keyVariable, String valueVariable) {
		return nest(type, new Key("value", valueVariable, "$L", keyVariable), Mode.IN_OBJECT);
	}

	protected abstract SELF nest(TypeMirror type, Key nestedKey, Mode mode);

	protected StringBuilder stack() {
		if (parent != null) {
			if (key.propertyName() == null) {
				return parent.stack();
			}
			return parent.stack().append(" -> ").append(key.propertyName());
		}
		if (key.propertyName() == null) {
			return new StringBuilder();
		}
		return new StringBuilder(key.propertyName());
	}

	enum StringKind {
		STRING,
		CHAR_ARRAY
	}

	enum BinaryKind {
		BYTE_ARRAY
	}

	/**
	 * If {@link Mode#IN_OBJECT}, describes the key of the current value.
	 *
	 * @param propertyName For error message traces. Either the property name or can be something like "element", or "value".
	 *                     This is also used as prefix for the variable name.
	 * @param varName      The variable name to use for the current value.
	 * @param keyDollar    $S or $L: how to retrieve the key value.
	 * @param keyValue     What to use put in keyDollar.
	 */
	protected record Key(@Nullable String propertyName, String varName, String keyDollar, String keyValue) {
		static Key root(VariableElement variable) {
			return new Key(variable.getSimpleName().toString(), variable.getSimpleName().toString(), null, null);
		}
	}
}

