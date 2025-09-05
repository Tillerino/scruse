package org.tillerino.scruse.processor.features;

import java.util.List;
import org.tillerino.scruse.processor.config.ConfigProperty;
import org.tillerino.scruse.processor.config.ConfigProperty.ConfigPropertyRetriever;
import org.tillerino.scruse.processor.config.ConfigProperty.LocationKind;
import org.tillerino.scruse.processor.config.ConfigProperty.MergeFunction;
import org.tillerino.scruse.processor.config.ConfigProperty.PropagationKind;
import org.tillerino.scruse.processor.util.Annotations.AnnotationValueWrapper;

public class IgnoreProperty {
    public static ConfigProperty<Boolean> IGNORE_PROPERTY = ConfigProperty.createConfigProperty(
            List.of(LocationKind.PROPERTY),
            List.of(new ConfigPropertyRetriever<>(
                    "com.fasterxml.jackson.annotation.JsonIgnore",
                    (ann, utils) -> ann.method("value", true).map(AnnotationValueWrapper::asBoolean))),
            false,
            MergeFunction.notDefault(false),
            PropagationKind.none());
}
