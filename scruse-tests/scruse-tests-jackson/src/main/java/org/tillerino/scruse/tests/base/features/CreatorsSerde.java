package org.tillerino.scruse.tests.base.features;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.tests.model.features.CreatorsModel;

public interface CreatorsSerde {

    @JsonInput
    CreatorsModel.JsonCreatorConstructorFactoryClass<Integer> readConstructorFactory(JsonParser parser)
            throws Exception;

    @JsonInput
    CreatorsModel.JsonCreatorConstructorFactoryMultiplePropertiesClass<Integer>
            readConstructorFactoryMultipleProperties(JsonParser parser, Thread t) throws Exception;

    @JsonInput
    CreatorsModel.JsonCreatorMethodFactoryRecord<Integer> readMethodFactory(JsonParser parser) throws Exception;

    @JsonInput
    CreatorsModel.JsonCreatorMethodFactoryMultiplePropertiesRecord<Integer> readMethodFactoryMultipleProperties(
            JsonParser parser, Thread t) throws Exception;

    @JsonInput
    CreatorsModel.JsonCreatorConstructorCreatorClass<Integer> readConstructorCreator(JsonParser parser)
            throws Exception;

    @JsonInput
    CreatorsModel.JsonCreatorMethodCreatorRecord<Integer> readMethodCreator(JsonParser parser) throws Exception;

    @JsonInput
    CreatorsModel.JsonCreatorMethodCreatorSinglePropertyRecord<Integer> readMethodCreatorSingleProperty(
            JsonParser parser) throws Exception;

    @JsonInput
    CreatorsModel.JsonCreatorConstructorCreatorSinglePropertyClass<Integer> readConstructorCreatorSingleProperty(
            JsonParser parser) throws Exception;
}
