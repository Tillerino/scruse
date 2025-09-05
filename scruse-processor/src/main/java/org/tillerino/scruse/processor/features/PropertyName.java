package org.tillerino.scruse.processor.features;

import java.util.List;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.config.ConfigProperty.PropagationKind;
import org.tillerino.scruse.processor.util.Annotations;

public class PropertyName {
    public static ConfigProperty<String> PROPERTY_NAME = ConfigProperty.createConfigProperty(
            List.of(ConfigProperty.LocationKind.PROPERTY),
            List.of(new ConfigProperty.ConfigPropertyRetriever<>(
                    "com.fasterxml.jackson.annotation.JsonProperty",
                    (ann, utils) -> ann.method("value", true).map(Annotations.AnnotationValueWrapper::asString))),
            "",
            ConfigProperty.MergeFunction.notDefault(""),
            PropagationKind.none());

    public static String resolvePropertyName(AnyConfig config, String canonicalPropertyName) {
        String customPropertyName = config.resolveProperty(PROPERTY_NAME).value();
        if (!customPropertyName.isEmpty()) {
            return customPropertyName;
        }
        return canonicalPropertyName;
    }
}
