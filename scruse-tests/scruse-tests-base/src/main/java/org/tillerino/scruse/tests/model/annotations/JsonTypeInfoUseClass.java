package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes({
    @Type(value = JsonTypeInfoUseClass.RecordOne.class, name = "1"),
    @Type(value = JsonTypeInfoUseClass.RecordTwo.class, name = "2")
})
public interface JsonTypeInfoUseClass {
    record RecordOne(String s) implements JsonTypeInfoUseClass {}

    record RecordTwo(int i) implements JsonTypeInfoUseClass {}
}
