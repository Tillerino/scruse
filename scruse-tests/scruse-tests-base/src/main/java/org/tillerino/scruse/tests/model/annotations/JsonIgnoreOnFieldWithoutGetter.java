package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JsonIgnoreOnFieldWithoutGetter {
    @JsonIgnore
    public String s;

    public JsonIgnoreOnFieldWithoutGetter(String s) {
        this.s = s;
    }
}
