package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.SelfReferencingRecord;

interface SelfReferencingSerde {
    @JsonOutput
    void serialize(SelfReferencingRecord record, JsonGenerator output) throws IOException;

    @JsonInput
    SelfReferencingRecord deserialize(JsonParser input) throws IOException;
}
