package org.tillerino.scruse.processor.features;

import java.util.List;
import java.util.Set;
import org.tillerino.scruse.processor.Snippet;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.config.ConfigProperty.ConfigPropertyRetriever;
import org.tillerino.scruse.processor.config.ConfigProperty.LocationKind;
import org.tillerino.scruse.processor.config.ConfigProperty.MergeFunction;
import org.tillerino.scruse.processor.util.Annotations.AnnotationValueWrapper;

public class IgnoreProperties {
    public static ConfigProperty<Set<String>> IGNORED_PROPERTIES = ConfigProperty.createConfigProperty(
            List.of(LocationKind.BLUEPRINT, LocationKind.PROTOTYPE, LocationKind.CREATOR, LocationKind.DTO),
            List.of(new ConfigPropertyRetriever<>(
                    "com.fasterxml.jackson.annotation.JsonIgnoreProperties",
                    (wrapper, utils) -> wrapper.method("value", true)
                            .map(AnnotationValueWrapper::asArray)
                            .map(arr -> arr.stream()
                                    .map(AnnotationValueWrapper::asString)
                                    .collect(ConfigProperty.toUnmodifiableSet())))),
            Set.of(),
            MergeFunction.mergeSets(),
            LocationKind.DTO);

    public static Snippet toSnippet(Set<String> ignoredProperties) {
        return Snippet.join(
                ignoredProperties.stream().map(prop -> Snippet.of("$S", prop)).toList(), ", ");
    }
}
