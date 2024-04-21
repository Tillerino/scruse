package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public class JsonCreatorConstructorCreatorClass<T> {
    private final T prop;
    private final String s;

    @JsonCreator
    public JsonCreatorConstructorCreatorClass(T notprop, String nots) {
        this.prop = notprop;
        this.s = nots;
    }
}
