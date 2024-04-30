package org.tillerino.scruse.tests.base.delegate;

import static org.tillerino.scruse.tests.CodeAssertions.assertThatCode;

import org.junit.jupiter.api.Test;

class PrimitiveArrayFieldsRecordTest {

    @Test
    void delegationWorks() throws Exception {
        assertThatCode(PrimitiveArrayFieldsRecordSerdeImpl.class)
                .method("writePrimitiveArrayFieldsRecord")
                .calls("writeBooleanArrayX")
                .calls("writeByteArrayX")
                .calls("writeCharArrayX")
                .calls("writeShortArrayX")
                .calls("writeIntArrayX")
                .calls("writeLongArrayX")
                .calls("writeFloatArrayX")
                .calls("writeDoubleArrayX");
    }
}
