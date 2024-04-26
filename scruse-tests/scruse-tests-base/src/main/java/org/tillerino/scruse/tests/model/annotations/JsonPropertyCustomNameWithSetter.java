package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class JsonPropertyCustomNameWithSetter {
    private @JsonProperty("notS") String s;

    public JsonPropertyCustomNameWithSetter(String s) {
        this.s = s;
    }

    public JsonPropertyCustomNameWithSetter() {}

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
