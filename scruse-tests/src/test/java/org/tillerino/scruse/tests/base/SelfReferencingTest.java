package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.SelfReferencingRecord;

import java.io.IOException;

class SelfReferencingTest {
    SelfReferencingSerde serde = new SelfReferencingSerdeImpl();

    @Test
    void selfReferencingSerialization() throws IOException {
        for (SelfReferencingRecord instance : SelfReferencingRecord.INSTANCES) {
            OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind(instance, serde::serialize);
        }
    }

    @Test
    void selfReferencingDeserialization() throws IOException {
        for (String json : SelfReferencingRecord.JSON) {
            SelfReferencingRecord r = InputUtils.assertThatJacksonJsonParserIsEqualToDatabind(json, serde::deserialize, new TypeReference<>() {
            });
        }
    }
}
