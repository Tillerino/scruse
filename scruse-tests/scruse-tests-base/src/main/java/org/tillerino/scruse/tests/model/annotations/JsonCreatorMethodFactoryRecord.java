package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Map;

public record JsonCreatorMethodFactoryRecord<T>(T prop) {

    @JsonCreator
    public static <U> JsonCreatorMethodFactoryRecord<U> of(Map<String, U> props) {
        return new JsonCreatorMethodFactoryRecord<>(props.get("notprop"));
    }
}
