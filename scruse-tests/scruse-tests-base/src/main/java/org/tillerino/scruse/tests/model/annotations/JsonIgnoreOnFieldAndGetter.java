package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** Field annotation takes precedence. */
public class JsonIgnoreOnFieldAndGetter {
    @JsonIgnore
    private String s;

    public JsonIgnoreOnFieldAndGetter(String s) {
        this.s = s;
    }

    @JsonIgnore(false)
    public String getS() {
        return s;
    }
}
