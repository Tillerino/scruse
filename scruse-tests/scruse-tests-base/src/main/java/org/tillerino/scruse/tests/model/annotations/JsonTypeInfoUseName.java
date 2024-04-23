package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @Type(value = JsonTypeInfoUseName.RecordOne.class, name = "1"),
    @Type(value = JsonTypeInfoUseName.RecordTwo.class, name = "2")
})
public interface JsonTypeInfoUseName {
    record RecordOne(String s) implements JsonTypeInfoUseName {}

    record RecordTwo(int i) implements JsonTypeInfoUseName {}
}
