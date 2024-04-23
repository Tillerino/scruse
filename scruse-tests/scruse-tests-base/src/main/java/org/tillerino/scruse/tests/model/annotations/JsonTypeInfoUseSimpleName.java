package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
@JsonSubTypes({
    // SIMPLE_NAME actually prefers the specified name in @Type over the simple name
    @Type(value = JsonTypeInfoUseSimpleName.RecordOne.class, name = "1"),
    @Type(value = JsonTypeInfoUseSimpleName.RecordTwo.class)
})
public interface JsonTypeInfoUseSimpleName {
    record RecordOne(String s) implements JsonTypeInfoUseSimpleName {}

    record RecordTwo(int i) implements JsonTypeInfoUseSimpleName {}
}
