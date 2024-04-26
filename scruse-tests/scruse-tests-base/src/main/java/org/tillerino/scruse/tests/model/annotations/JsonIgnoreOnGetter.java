package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JsonIgnoreOnGetter {
    private String s;

    @JsonIgnore
    public String getS() {
        return s;
    }

    public JsonIgnoreOnGetter(String s) {
        this.s = s;
    }
}
