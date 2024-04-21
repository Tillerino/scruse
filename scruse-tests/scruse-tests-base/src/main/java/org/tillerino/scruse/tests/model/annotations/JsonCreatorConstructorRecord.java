package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Map;

public record JsonCreatorConstructorRecord<T>(T prop) {

    @JsonCreator
    public static <T> JsonCreatorConstructorRecord<T> of(Map<String, T> props) {
        return new JsonCreatorConstructorRecord<T>(props.get("notprop"));
    }
}
