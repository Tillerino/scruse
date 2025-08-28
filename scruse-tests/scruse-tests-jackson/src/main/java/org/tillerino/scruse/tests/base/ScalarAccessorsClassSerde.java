package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.ScalarAccessorsClass;

public interface ScalarAccessorsClassSerde {
    @JsonOutput
    void write(ScalarAccessorsClass record, JsonGenerator generator) throws Exception;

    @JsonInput
    ScalarAccessorsClass read(JsonParser parser) throws Exception;
}
