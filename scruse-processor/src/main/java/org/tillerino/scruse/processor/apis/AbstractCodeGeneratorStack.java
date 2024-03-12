package org.tillerino.scruse.processor.apis;

import com.squareup.javapoet.CodeBlock;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.mapstruct.ap.internal.model.common.Type;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.GeneratedClass;
import org.tillerino.scruse.processor.Polymorphism;
import org.tillerino.scruse.processor.ScruseMethod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCodeGeneratorStack<SELF extends AbstractCodeGeneratorStack<SELF>> {
	protected final AnnotationProcessorUtils utils;
	protected final GeneratedClass generatedClass;
	protected final ScruseMethod prototype;
	protected final CodeBlock.Builder code;
	@Nullable
	protected final SELF parent;
	protected final Type type;
	protected final boolean stackRelevantType;
	@Nullable
	protected final String property;
    protected final boolean canBePolyChild;

    protected AbstractCodeGeneratorStack(AnnotationProcessorUtils utils, GeneratedClass generatedClass, ScruseMethod prototype, CodeBlock.Builder code, SELF parent, Type type, boolean stackRelevantType, @Nullable String property) {
		this.prototype = prototype;
		this.utils = utils;
		this.type = type;
		this.code = code;
		this.parent = parent;
		this.generatedClass = Validate.notNull(generatedClass);
		this.stackRelevantType = stackRelevantType;
		this.property = property;
        this.canBePolyChild = prototype.contextParameter().isPresent() && stackDepth() == 1 && Polymorphism.isSomeChild(type.getTypeMirror(), utils.types);
	}

	protected void detectSelfReferencingType() {
		if (stackRelevantType && parent != null && parent.stackContainsType(type)) {
			throw new ContextedRuntimeException("Self-referencing type detected. Define a separate method for this type.")
				.addContextValue("type", type)
				.addContextValue("stack", stack());
		}
	}

	boolean stackContainsType(Type type) {
		if ((stackRelevantType || parent == null) && this.type.equals(type)) {
			return true;
		}
		if (parent != null) {
			return parent.stackContainsType(type);
		}
		return false;
	}

	int stackDepth() {
		return parent != null ? 1 + parent.stackDepth() : 1;
	}

	protected StringBuilder stack() {
		if (parent != null) {
			if (property == null) {
				return parent.stack();
			}
			return parent.stack().append(" -> ").append(property + ": " + type.getName());
		}
		if (property == null) {
			return new StringBuilder(type.getName());
		}
		return new StringBuilder(property + ": " + type.getName());
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

