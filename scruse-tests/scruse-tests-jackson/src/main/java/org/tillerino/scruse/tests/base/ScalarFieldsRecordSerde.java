package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.ScalarFieldsRecord;

public interface ScalarFieldsRecordSerde {
    @JsonOutput
    void write(ScalarFieldsRecord record, JsonGenerator generator) throws Exception;

    @JsonInput
    ScalarFieldsRecord read(JsonParser parser) throws Exception;
}
