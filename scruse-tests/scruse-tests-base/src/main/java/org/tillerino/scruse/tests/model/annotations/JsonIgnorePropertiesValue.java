package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties({"ignoredValue", "ignoredArray", "ignoredObject"})
public record JsonIgnorePropertiesValue(
        String name,
        int value,
        String ignoredValue,
        // this is renamed, so it will not actually be ignored
        @JsonProperty("renamed") String ignoredArray) {}
