package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.TestSettings;
import org.tillerino.scruse.tests.model.PrimitiveArrayFieldsRecord;
import org.tillerino.scruse.tests.model.ReferenceArrayFieldsRecord;

class ArrayFieldsRecordTest {
    ArrayFieldsRecordSerde serde = new ArrayFieldsRecordSerdeImpl();

    @Test
    void roundtripPrimitive() throws IOException {
        for (PrimitiveArrayFieldsRecord record : PrimitiveArrayFieldsRecord.instances(TestSettings.SETTINGS)) {
            OutputUtils.roundTripRecursive(
                    record,
                    serde::writePrimitive,
                    serde::readPrimitive,
                    new TypeReference<PrimitiveArrayFieldsRecord>() {});
        }
    }

    @Test
    void roundtripReference() throws IOException {
        for (ReferenceArrayFieldsRecord record : ReferenceArrayFieldsRecord.instances(TestSettings.SETTINGS)) {
            OutputUtils.roundTripRecursive(
                    record,
                    serde::writeReference,
                    serde::readReference,
                    new TypeReference<ReferenceArrayFieldsRecord>() {});
        }
    }
}
