package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.SelfReferencingRecord;

class SelfReferencingSerdeTest {
    SelfReferencingSerde serde = new SelfReferencingSerdeImpl();

    @Test
    void selfReferencingSerialization() throws IOException {
        for (SelfReferencingRecord instance : SelfReferencingRecord.INSTANCES) {
            OutputUtils.roundTrip(instance, serde::serialize, serde::deserialize, new TypeReference<>() {});
        }
    }
}
