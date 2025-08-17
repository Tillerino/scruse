package org.tillerino.scruse.tests.model.features;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface IgnorePropertiesModel {
    @JsonIgnoreProperties({"ignoredValue", "ignoredArray", "ignoredObject"})
    record JsonIgnorePropertiesValue(
            String name,
            int value,
            String ignoredValue,
            // this is renamed, so it will not actually be ignored
            @JsonProperty("renamed") String ignoredArray) {}
}
