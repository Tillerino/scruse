package org.tillerino.scruse.processor.util;

import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonConfig.DelegateeMode;
import org.tillerino.scruse.annotations.JsonConfig.ImplementationMode;
import org.tillerino.scruse.annotations.JsonConfig.MergeMode;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.AnnotationProcessorUtils.GetAnnotationValues;
import org.tillerino.scruse.processor.ScruseBlueprint;

import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record Config(
		List<ScruseBlueprint> uses,
		List<Config> configs,
		DelegateeMode delegateTo,
		ImplementationMode implement,
		MergeMode merge
) {
	public static Config defaultConfig(Element element, AnnotationProcessorUtils utils) {
		List<ScruseBlueprint> uses = new ArrayList<>();
		List<Config> configs = new ArrayList<>();
		DelegateeMode delegateTo = DelegateeMode.MERGE;
		ImplementationMode implement = ImplementationMode.MERGE;
		MergeMode merge = MergeMode.LIKE_ENCLOSING;
		List<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> list = element.getAnnotationMirrors().stream()
			.filter(annotation -> annotation.getAnnotationType().toString().equals(JsonConfig.class.getName()))
			.flatMap(annotation -> annotation.getElementValues().entrySet().stream())
			.toList();
		for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : list) {
			AnnotationValue value = entry.getValue();
			switch (entry.getKey().getSimpleName().toString()) {
				case "uses" -> value.accept(new GetAnnotationValues<Void, Void>() {
					@Override
					public Void visitType(TypeMirror t, Void o) {
						uses.add(utils.blueprint(utils.elements.getTypeElement(t.toString())));
						return null;
					}
				}, null);
				case "configs" -> value.accept(new GetAnnotationValues<Void, Void>() {
					@Override
					public Void visitType(TypeMirror t, Void o) {
						configs.add(utils.blueprint(utils.elements.getTypeElement(t.toString())).config);
						return null;
					}
				}, null);
				case "delegateTo" -> delegateTo = DelegateeMode.valueOf(value.getValue().toString());
				case "implement" -> implement = ImplementationMode.valueOf(value.getValue().toString());
				case "merge" -> merge = MergeMode.valueOf(value.getValue().toString());
			}
		}
		return new Config(Collections.unmodifiableList(uses), Collections.unmodifiableList(configs),
			delegateTo, implement, merge);
	}

	public Config merge(@Nullable Config enclosing) {
		// TODO. This is not well thought out, but I want to work on other things at the moment.
		// With some use cases, everything will probably become clearer.
		MergeMode merge = effectiveMergeMode(enclosing);
		if (merge == MergeMode.DO_NOT_MERGE) {
			return this;
		}
		DelegateeMode mergedDelegateTo = DelegateeMode.MERGE;
		ImplementationMode mergedImplement = ImplementationMode.MERGE;
		List<ScruseBlueprint> mergedUses = new ArrayList<>();

		if (merge == MergeMode.ENCLOSING_THEN_CONFIGURATION) {
			List<Config> configsToMerge = new ArrayList<>();
			if (enclosing != null) {
				configsToMerge.add(enclosing);
			}
			configsToMerge.addAll(configs());
			for (Config config : configsToMerge) {
				mergedUses.addAll(config.uses());
				if (config.delegateTo != DelegateeMode.NONE) {
					mergedDelegateTo = config.delegateTo;
				}
				if (config.implement != ImplementationMode.NONE) {
					mergedImplement = config.implement;
				}
			}
		}
		for (ScruseBlueprint use : this.uses()) {
			mergedUses.addAll(use.config.uses());
			mergedUses.add(use);
		}
		if (this.delegateTo != DelegateeMode.MERGE) {
			mergedDelegateTo = this.delegateTo;
		}
		if (this.implement != ImplementationMode.MERGE) {
			mergedImplement = this.implement;
		}
		return new Config(Collections.unmodifiableList(mergedUses), List.of(), mergedDelegateTo, mergedImplement, merge);
	}

	private MergeMode effectiveMergeMode(Config enclosing) {
		MergeMode merge = this.merge;
		if (merge == MergeMode.LIKE_ENCLOSING && enclosing != null) {
			merge = enclosing.merge();
		}
		if (merge == MergeMode.LIKE_ENCLOSING) {
			merge = MergeMode.ENCLOSING_THEN_CONFIGURATION;
		}
		return merge;
	}

	public interface HasConfig {
		Config config();
	}
}
