package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.SelfReferencingRecord;

interface SelfReferencingSerde {
    @JsonOutput
    void serialize(SelfReferencingRecord record, JsonGenerator output) throws Exception;

    @JsonInput
    SelfReferencingRecord deserialize(JsonParser input) throws Exception;
}
