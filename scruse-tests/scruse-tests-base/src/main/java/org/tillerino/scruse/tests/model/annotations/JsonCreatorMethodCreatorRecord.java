package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonCreator;

public record JsonCreatorMethodCreatorRecord<T>(T prop, String s) {

    @JsonCreator
    public static <U> JsonCreatorMethodCreatorRecord<U> of(U notprop, String nots) {
        return new JsonCreatorMethodCreatorRecord<>(notprop, nots);
    }
}
