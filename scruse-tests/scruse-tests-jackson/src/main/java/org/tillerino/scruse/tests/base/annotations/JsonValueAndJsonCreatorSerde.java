package org.tillerino.scruse.tests.base.annotations;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.annotations.JsonCreatorConstructorClass;
import org.tillerino.scruse.tests.model.annotations.JsonCreatorConstructorRecord;
import org.tillerino.scruse.tests.model.annotations.JsonValueRecord;

public interface JsonValueAndJsonCreatorSerde {
    @JsonOutput
    void write(JsonValueRecord<Integer> obj, JsonGenerator generator) throws Exception;

    @JsonInput
    JsonCreatorConstructorClass<Integer> readConstructor(JsonParser parser) throws Exception;

    @JsonInput
    JsonCreatorConstructorRecord<Integer> readRecord(JsonParser parser) throws Exception;
}
