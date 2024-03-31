package org.tillerino.scruse.tests.base.generics;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.GenericRecord;

@JsonConfig(
        uses = {
            StringSerde.class,
            GenericRecordSerde.class,
        })
public interface StringRecordSerde {
    @JsonInput
    GenericRecord<String> readStringRecord(JsonParser parser) throws IOException;

    @JsonOutput
    void writeStringRecord(GenericRecord<String> obj, JsonGenerator gen) throws IOException;
}
