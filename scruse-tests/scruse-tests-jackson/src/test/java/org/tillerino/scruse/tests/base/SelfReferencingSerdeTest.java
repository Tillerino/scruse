package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.SerdeUtil;
import org.tillerino.scruse.tests.model.SelfReferencingRecord;

class SelfReferencingSerdeTest extends ReferenceTest {
    SelfReferencingSerde serde = SerdeUtil.impl(SelfReferencingSerde.class);

    @Test
    void selfReferencingSerialization() throws Exception {
        for (SelfReferencingRecord instance : SelfReferencingRecord.INSTANCES) {
            outputUtils.roundTrip(instance, serde::serialize, serde::deserialize, new TypeReference<>() {});
        }
    }
}
