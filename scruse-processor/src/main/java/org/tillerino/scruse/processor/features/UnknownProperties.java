package org.tillerino.scruse.processor.features;

import java.util.List;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.processor.config.AnyConfig;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.util.Annotations;

public class UnknownProperties {
    public static ConfigProperty<JsonConfig.UnknownPropertiesMode> UNKNOWN_PROPERTIES =
            ConfigProperty.createConfigProperty(
                    List.of(
                            ConfigProperty.LocationKind.BLUEPRINT,
                            ConfigProperty.LocationKind.PROTOTYPE,
                            ConfigProperty.LocationKind.CREATOR,
                            ConfigProperty.LocationKind.DTO),
                    List.of(
                            new ConfigProperty.ConfigPropertyRetriever<>(
                                    "com.fasterxml.jackson.annotation.JsonIgnoreProperties",
                                    (wrapper, utils) -> wrapper.method("ignoreUnknown", true)
                                            .map(Annotations.AnnotationValueWrapper::asBoolean)
                                            .map(i -> i
                                                    ? JsonConfig.UnknownPropertiesMode.IGNORE
                                                    : JsonConfig.UnknownPropertiesMode.THROW)),
                            ConfigProperty.ConfigPropertyRetriever.jsonConfigPropertyRetriever(
                                    "unknownProperties", JsonConfig.UnknownPropertiesMode.class)),
                    JsonConfig.UnknownPropertiesMode.DEFAULT,
                    ConfigProperty.MergeFunction.notDefault(JsonConfig.UnknownPropertiesMode.DEFAULT),
                    null);

    public static boolean shouldThrow(AnyConfig config) {
        return config.resolveProperty(UNKNOWN_PROPERTIES).value() != JsonConfig.UnknownPropertiesMode.IGNORE;
    }
}
