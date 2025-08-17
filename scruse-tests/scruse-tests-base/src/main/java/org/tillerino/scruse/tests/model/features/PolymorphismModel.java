package org.tillerino.scruse.tests.model.features;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public interface PolymorphismModel {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({
        @Type(value = JsonTypeInfoUseName.RecordOne.class, name = "1"),
        @Type(value = JsonTypeInfoUseName.RecordTwo.class, name = "2")
    })
    interface JsonTypeInfoUseName {
        record RecordOne(String s) implements JsonTypeInfoUseName {}

        record RecordTwo(int i) implements JsonTypeInfoUseName {}
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonSubTypes({
        @Type(value = JsonTypeInfoUseClass.RecordOne.class, name = "1"),
        @Type(value = JsonTypeInfoUseClass.RecordTwo.class, name = "2")
    })
    interface JsonTypeInfoUseClass {
        record RecordOne(String s) implements JsonTypeInfoUseClass {}

        record RecordTwo(int i) implements JsonTypeInfoUseClass {}
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.SIMPLE_NAME)
    @JsonSubTypes({
        // SIMPLE_NAME actually prefers the specified name in @Type over the simple name
        @Type(value = JsonTypeInfoUseSimpleName.RecordOne.class, name = "1"),
        @Type(value = JsonTypeInfoUseSimpleName.RecordTwo.class)
    })
    interface JsonTypeInfoUseSimpleName {
        record RecordOne(String s) implements JsonTypeInfoUseSimpleName {}

        record RecordTwo(int i) implements JsonTypeInfoUseSimpleName {}
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    sealed interface SealedInterface {}

    record RecordOne(String s) implements SealedInterface {}

    record RecordTwo(int i) implements SealedInterface {}
}
