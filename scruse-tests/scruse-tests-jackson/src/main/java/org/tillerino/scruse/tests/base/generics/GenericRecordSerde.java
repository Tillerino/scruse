package org.tillerino.scruse.tests.base.generics;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.model.GenericRecord;

public interface GenericRecordSerde {
    @JsonInput
    <T> GenericRecord<T> readGenericRecord(JsonParser parser, GenericInput<T> fieldSerde) throws IOException;

    @JsonOutput
    <T> void writeGenericRecord(GenericRecord<T> obj, JsonGenerator gen, GenericOutput<T> fieldSerde)
            throws IOException;
}
