package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.LinkedHashMap;
import java.util.Map;

public record JsonValueRecord<T>(T prop) {

    @JsonValue
    public Map<String, T> toMap() {
        LinkedHashMap<String, T> map = new LinkedHashMap<>();
        map.put("notprop", prop);
        return map;
    }
}
