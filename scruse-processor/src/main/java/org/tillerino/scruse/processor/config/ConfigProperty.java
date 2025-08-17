package org.tillerino.scruse.processor.config;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.lang.model.element.Element;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.processor.AnnotationProcessorUtils;
import org.tillerino.scruse.processor.ScruseBlueprint;
import org.tillerino.scruse.processor.util.Annotations.AnnotationMirrorWrapper;
import org.tillerino.scruse.processor.util.Annotations.AnnotationValueWrapper;

public final class ConfigProperty<T> {
    private static final AtomicInteger counter = new AtomicInteger();

    public static ConfigProperty<JsonConfig.UnknownPropertiesMode> UNKNOWN_PROPERTIES = createConfigProperty(
            List.of(LocationKind.BLUEPRINT, LocationKind.PROTOTYPE, LocationKind.CREATOR, LocationKind.DTO),
            List.of(
                    new ConfigPropertyRetriever<>(
                            "com.fasterxml.jackson.annotation.JsonIgnoreProperties",
                            (wrapper, utils) -> wrapper.method("ignoreUnknown", true)
                                    .map(AnnotationValueWrapper::asBoolean)
                                    .map(i -> i
                                            ? JsonConfig.UnknownPropertiesMode.IGNORE
                                            : JsonConfig.UnknownPropertiesMode.THROW)),
                    ConfigPropertyRetriever.jsonConfigPropertyRetriever(
                            "unknownProperties", JsonConfig.UnknownPropertiesMode.class)),
            JsonConfig.UnknownPropertiesMode.DEFAULT,
            MergeFunction.notDefault(JsonConfig.UnknownPropertiesMode.DEFAULT));

    public static ConfigProperty<Set<ScruseBlueprint>> USES = createConfigProperty(
            List.of(LocationKind.values()),
            List.of(new ConfigPropertyRetriever<>(
                    "org.tillerino.scruse.annotations.JsonConfig", (wrapper, utils) -> wrapper.method("uses", true)
                            .map(AnnotationValueWrapper::asArray)
                            .map(classNames -> classNames.stream()
                                    .map(className -> utils.blueprint(utils.elements.getTypeElement(
                                            className.asTypeMirror().toString())))
                                    .flatMap(ScruseBlueprint::includeUses)
                                    .collect(toUnmodifiableSet())))),
            Set.of(),
            MergeFunction.mergeSets());

    public static ConfigProperty<JsonConfig.DelegateeMode> DELEGATEE = createConfigProperty(
            List.of(LocationKind.BLUEPRINT, LocationKind.PROTOTYPE),
            List.of(ConfigPropertyRetriever.jsonConfigPropertyRetriever("delegateTo", JsonConfig.DelegateeMode.class)),
            JsonConfig.DelegateeMode.DEFAULT,
            MergeFunction.notDefault(JsonConfig.DelegateeMode.DEFAULT));

    public static ConfigProperty<JsonConfig.ImplementationMode> IMPLEMENT = createConfigProperty(
            List.of(LocationKind.BLUEPRINT, LocationKind.PROTOTYPE),
            List.of(ConfigPropertyRetriever.jsonConfigPropertyRetriever(
                    "implement", JsonConfig.ImplementationMode.class)),
            JsonConfig.ImplementationMode.DEFAULT,
            MergeFunction.notDefault(JsonConfig.ImplementationMode.DEFAULT));

    public static ConfigProperty<Boolean> IGNORE_PROPERTY = createConfigProperty(
            List.of(LocationKind.PROPERTY),
            List.of(new ConfigPropertyRetriever<>(
                    "com.fasterxml.jackson.annotation.JsonIgnore",
                    (ann, utils) -> ann.method("value", true).map(AnnotationValueWrapper::asBoolean))),
            false,
            MergeFunction.notDefault(false));

    public static ConfigProperty<Set<String>> IGNORED_PROPERTIES = createConfigProperty(
            List.of(LocationKind.BLUEPRINT, LocationKind.PROTOTYPE, LocationKind.CREATOR, LocationKind.DTO),
            List.of(new ConfigPropertyRetriever<>(
                    "com.fasterxml.jackson.annotation.JsonIgnoreProperties",
                    (wrapper, utils) -> wrapper.method("value", true)
                            .map(AnnotationValueWrapper::asArray)
                            .map(arr -> arr.stream()
                                    .map(AnnotationValueWrapper::asString)
                                    .collect(toUnmodifiableSet())))),
            Set.of(),
            MergeFunction.mergeSets());

    final int index = counter.incrementAndGet();

    public final List<LocationKind> locationKind;
    public final List<ConfigPropertyRetriever<T>> retrievers;
    public final T defaultValue;
    public final MergeFunction<T> merger;

    public ConfigProperty(
            List<LocationKind> locationKind,
            List<ConfigPropertyRetriever<T>> retrievers,
            T defaultValue,
            MergeFunction<T> merger) {
        this.locationKind = locationKind;
        this.retrievers = retrievers;
        this.defaultValue = defaultValue;
        this.merger = merger;
    }

    public static <T> ConfigProperty<T> createConfigProperty(
            List<LocationKind> locationKind,
            List<ConfigPropertyRetriever<T>> retriever,
            T defaultValue,
            MergeFunction<T> merger) {
        return new ConfigProperty<>(locationKind, retriever, defaultValue, merger);
    }

    Optional<InstantiatedProperty<T>> instantiate(
            Element element, LocationKind elementType, AnnotationProcessorUtils utils) {
        if (!this.locationKind.contains(elementType)) {
            return Optional.empty();
        }
        return retrievers.stream()
                .flatMap(retriever -> utils
                        .annotations
                        .findAnnotation(element, retriever.annotationClass)
                        .flatMap(annotation -> retriever.valueRetriever.retrieve(annotation, utils))
                        .map(value -> new InstantiatedProperty<>(
                                ConfigProperty.this, elementType, value, retriever.annotationClass + " on " + element))
                        .stream())
                .reduce(merger::merge);
    }

    private static <T> Collector<T, ?, Set<T>> toUnmodifiableSet() {
        // this set preserves order
        return Collectors.collectingAndThen(Collectors.toCollection(LinkedHashSet::new), Collections::unmodifiableSet);
    }

    public interface MergeFunction<T> {
        InstantiatedProperty<T> merge(InstantiatedProperty<T> strong, InstantiatedProperty<T> weak);

        static <T> MergeFunction<T> notDefault(T defaultValue) {
            return (strong, weak) -> {
                if (strong.property.equals(defaultValue)) {
                    return weak;
                }
                return strong;
            };
        }

        static <T> MergeFunction<Set<T>> mergeSets() {
            return (strong, weak) -> {
                if (weak.value.isEmpty()) {
                    return strong;
                }
                if (strong.value.isEmpty()) {
                    return weak;
                }
                LinkedHashSet<T> merged = new LinkedHashSet<>();
                merged.addAll(strong.value);
                merged.addAll(weak.value);
                return new InstantiatedProperty<>(
                        strong.property,
                        strong.locationKind,
                        Collections.unmodifiableSet(merged),
                        "Merged " + strong.sourceLocation + " and " + weak.sourceLocation);
            };
        }
    }

    public enum LocationKind {
        // these are sorted from strongest to weakest
        PROPERTY,
        CREATOR,
        DTO,
        PROTOTYPE,
        BLUEPRINT,
    }

    public record ConfigPropertyRetriever<T>(
            String annotationClass, ConfigPropertyRetrieverFunction<T> valueRetriever) {

        private static <T extends Enum<T>> ConfigPropertyRetriever<T> jsonConfigPropertyRetriever(
                String method, Class<T> enumClass) {
            return new ConfigPropertyRetriever<>(
                    "org.tillerino.scruse.annotations.JsonConfig", (wrapper, utils) -> wrapper.method(method, false)
                            .map(annotationValueWrapper -> annotationValueWrapper.asEnum(enumClass)));
        }

        public interface ConfigPropertyRetrieverFunction<T> {
            Optional<T> retrieve(AnnotationMirrorWrapper annotation, AnnotationProcessorUtils utils);
        }
    }

    public record InstantiatedProperty<T>(
            ConfigProperty<T> property, LocationKind locationKind, T value, String sourceLocation) {}
}
