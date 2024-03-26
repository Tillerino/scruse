package org.tillerino.scruse.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface JsonConfig {
	/**
	 * Uses mappers from these classes (recursively).
	 * These are merged in order, i.e. later elements take when resolving conflicts.
	 */
	Class<?>[] uses() default { };

	/**
	 * Uses mappers and configuration from these classes.
	 * These are merged in order, i.e. later elements take when resolving conflicts.
	 */
	Class<?>[] config() default { };

	/**
	 * Determines if the annotated element should be implemented.
	 *
	 * <p>
	 * By default, all methods are implemented.
	 *
	 * @return see {@link ImplementationMode}
	 */
	ImplementationMode implement() default ImplementationMode.MERGE;

	/**
	 * Determines if the annotated element can be called from other serializers.
	 *
	 * <p>
	 * By default, serializer implementations will always call suitable methods on other serializers that are
	 * reachable through the {@link #uses()}.
	 *
	 * @return see {@link DelegateeMode}
	 */
	DelegateeMode delegateTo() default DelegateeMode.MERGE;

	/**
	 * Determines how this annotation is treated in the presence of other {@link JsonConfig} annotations
	 * which apply to the same scope.
	 *
	 * <p>
	 * By default, the annotations are merged. For example, the {@link #uses()} arrays are merged.
	 * Conflicts are always solved by choosing the value from the annotation which is closer to the annotated element.
	 *
	 * @return false to ignore other {@link JsonConfig} annotations which would apply to the annotated element.
	 */
	MergeMode merge() default MergeMode.LIKE_ENCLOSING;

	enum DelegateeMode {
		/**
		 * The annotated element can be called from other serializers.
		 */
		DELEGATE_TO,
		/**
		 * The annotated element cannot be called from other serializers.
		 */
		DO_NOT_DELEGATE_TO,
		/**
		 * The annotated element inherits the setting.
		 * If there is nothing to inherit, this is equivalent to {@link #DELEGATE_TO}.
		 */
		MERGE,
		/**
		 * During merging, this value is not taken into account.
		 */
		NONE,
		;

		public boolean canBeDelegatedTo() {
			return this != DO_NOT_DELEGATE_TO;
		}
	}

	enum ImplementationMode {
		DO_IMPLEMENT,
		DO_NOT_IMPLEMENT,
		MERGE,
		NONE,
		;

		public boolean shouldImplement() {
			return this != DO_NOT_IMPLEMENT;
		}
	}

	enum MergeMode {
		/**
		 * Starts with the configuration of the enclosing element.
		 * Then merges this with the configuration from any {@link JsonConfig#config()} elements in their referenced order.
		 * Finally merges this with the configuration from the annotated element.
		 */
		ENCLOSING_THEN_CONFIGURATION,
		/**
		 * Only uses the configuration from the annotated element.
		 */
		DO_NOT_MERGE,
		/**
		 * The annotated element inherits this setting from the enclosing element.
		 * If there is nothing to inherit, this is equivalent to {@link #ENCLOSING_THEN_CONFIGURATION}.
		 */
		LIKE_ENCLOSING,
		;
	}
}
