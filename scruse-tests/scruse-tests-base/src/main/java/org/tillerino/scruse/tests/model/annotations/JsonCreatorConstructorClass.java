package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class JsonCreatorConstructorClass<T> {
    private final T prop;

    @JsonCreator
    public JsonCreatorConstructorClass(Map<String, T> props) {
        this.prop = props.get("notprop");
    }
}
