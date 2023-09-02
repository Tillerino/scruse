package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;

import javax.annotation.Nullable;
import javax.lang.model.type.TypeMirror;

public abstract class AbstractCodeGeneratorStack<SELF extends AbstractCodeGeneratorStack<SELF>> {
	protected final AnnotationProcessorUtils utils;
	protected final Type type;
	protected final CodeBlock.Builder code;
	protected final SELF parent;
	@Nullable
	protected final String property;

	protected AbstractCodeGeneratorStack(AnnotationProcessorUtils utils, Type type, CodeBlock.Builder code, SELF parent, @Nullable String property) {
		this.utils = utils;
		this.type = type;
		this.code = code;
		this.parent = parent;
		this.property = property;
	}

	int stackDepth() {
		return parent != null ? 1 + parent.stackDepth() : 1;
	}

	protected StringBuilder stack() {
		if (parent != null) {
			if (property == null) {
				return parent.stack();
			}
			return parent.stack().append(" -> ").append(property);
		}
		if (property == null) {
			return new StringBuilder();
		}
		return new StringBuilder(property);
	}

	protected String propertyName() {
		return property != null ? property : parent != null ? parent.propertyName() : "root";
	}

	enum StringKind {
		STRING,
		CHAR_ARRAY
	}

	enum BinaryKind {
		BYTE_ARRAY
	}
}

