package org.tillerino.scruse.tests.base.annotations;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.annotations.*;

public interface JsonValueAndJsonCreatorSerde {
    @JsonOutput
    void write(JsonValueRecord<Integer> obj, JsonGenerator generator) throws Exception;

    @JsonInput
    JsonCreatorConstructorFactoryClass<Integer> readConstructorFactory(JsonParser parser) throws Exception;

    @JsonInput
    JsonCreatorMethodFactoryRecord<Integer> readMethodFactory(JsonParser parser) throws Exception;

    @JsonInput
    JsonCreatorConstructorCreatorClass<Integer> readConstructorCreator(JsonParser parser) throws Exception;

    @JsonInput
    JsonCreatorMethodCreatorRecord<Integer> readMethodCreator(JsonParser parser) throws Exception;
}
