package org.tillerino.scruse.tests.base.generics;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;
import org.tillerino.scruse.tests.base.delegate.BoxedScalarsWriter;
import org.tillerino.scruse.tests.model.GenericRecord;

@JsonConfig(
        uses = {
            BoxedScalarsWriter.class,
            GenericRecordSerde.class,
        })
public interface IntegerRecordSerde {
    @JsonInput
    GenericRecord<Integer> readIntegerRecord(JsonParser parser) throws IOException;

    @JsonOutput
    void writeIntegerRecord(GenericRecord<Integer> obj, JsonGenerator gen) throws IOException;
}
