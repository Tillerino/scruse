package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.TestSettings;
import org.tillerino.scruse.tests.model.PrimitiveArrayFieldsRecord;
import org.tillerino.scruse.tests.model.ReferenceArrayFieldsRecord;

class ArrayFieldsRecordTest extends ReferenceTest {
    ArrayFieldsRecordSerde serde = new ArrayFieldsRecordSerdeImpl();

    @Test
    void roundtripPrimitive() throws Exception {
        for (PrimitiveArrayFieldsRecord record : PrimitiveArrayFieldsRecord.instances(TestSettings.SETTINGS)) {
            outputUtils.roundTripRecursive(
                    record,
                    serde::writePrimitive,
                    serde::readPrimitive,
                    new TypeReference<PrimitiveArrayFieldsRecord>() {});
        }
    }

    @Test
    void roundtripReference() throws Exception {
        for (ReferenceArrayFieldsRecord record : ReferenceArrayFieldsRecord.instances(TestSettings.SETTINGS)) {
            outputUtils.roundTripRecursive(
                    record,
                    serde::writeReference,
                    serde::readReference,
                    new TypeReference<ReferenceArrayFieldsRecord>() {});
        }
    }
}
