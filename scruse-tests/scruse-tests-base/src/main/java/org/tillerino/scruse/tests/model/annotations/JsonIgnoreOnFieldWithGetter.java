package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JsonIgnoreOnFieldWithGetter {
    @JsonIgnore
    private String s;

    public JsonIgnoreOnFieldWithGetter(String s) {
        this.s = s;
    }

    public String getS() {
        return s;
    }
}
