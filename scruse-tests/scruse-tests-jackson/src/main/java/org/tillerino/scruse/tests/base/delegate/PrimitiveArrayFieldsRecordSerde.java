package org.tillerino.scruse.tests.base.delegate;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.PrimitiveArrayFieldsRecord;

@JsonConfig(uses = ScalarArraysWriter.class)
public interface PrimitiveArrayFieldsRecordSerde {
    @JsonOutput
    void writePrimitiveArrayFieldsRecord(PrimitiveArrayFieldsRecord input, JsonGenerator generator) throws Exception;
}
