package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.ScalarFieldsClass;

public interface ScalarFieldsClassSerde {
    @JsonOutput
    void write(ScalarFieldsClass record, JsonGenerator generator) throws Exception;

    @JsonInput
    ScalarFieldsClass read(JsonParser parser) throws Exception;
}
