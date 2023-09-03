package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseMethod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCodeGeneratorStack<SELF extends AbstractCodeGeneratorStack<SELF>> {
	protected final ScruseMethod prototype;
	protected final AnnotationProcessorUtils utils;
	protected final SELF parent;
	protected final CodeBlock.Builder code;
	protected final Type type;
	@Nullable
	protected final String property;

	protected AbstractCodeGeneratorStack(ScruseMethod prototype, AnnotationProcessorUtils utils, Type type, CodeBlock.Builder code, SELF parent, @Nullable String property) {
		this.prototype = prototype;
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

	protected static Object[] flatten(Object... all) {
		List<Object> aggregator = new ArrayList<>();
		collectInto(all, aggregator);
		return aggregator.toArray();
	}

	private static void collectInto(Object o, List<Object> aggregator) {
		if (o instanceof Object[]) {
			for (Object o2 : (Object[]) o) {
				collectInto(o2, aggregator);
			}
		} else {
			aggregator.add(o);
		}
	}

	enum StringKind {
		STRING,
		CHAR_ARRAY
	}

	enum BinaryKind {
		BYTE_ARRAY
	}
}

