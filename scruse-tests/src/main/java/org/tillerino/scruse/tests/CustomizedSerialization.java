package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;
import java.time.OffsetDateTime;

interface CustomizedSerialization {
    @JsonOutput
    void writeMyObj(MyObj o, JsonGenerator generator) throws IOException;

    @JsonOutput
    default void writeOffsetDate(OffsetDateTime timestamp, JsonGenerator generator) throws IOException {
        generator.writeString(timestamp.toString());
    }

    record MyObj(OffsetDateTime t) {

    }
}
