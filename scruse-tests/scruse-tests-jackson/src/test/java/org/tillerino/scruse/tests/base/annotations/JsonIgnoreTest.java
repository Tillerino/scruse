package org.tillerino.scruse.tests.base.annotations;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.model.annotations.*;

class JsonIgnoreTest extends ReferenceTest {
    JsonIgnoreSerde serde = new JsonIgnoreSerdeImpl();

    @Test
    void fieldWithGetter() throws Exception {
        outputUtils.assertIsEqualToDatabind(new JsonIgnoreOnFieldWithGetter("x"), serde::writeFieldWithGetter);
    }

    @Test
    void fieldAndGetter() throws Exception {
        outputUtils.assertIsEqualToDatabind(new JsonIgnoreOnFieldAndGetter("x"), serde::writeFieldAndGetter);
    }

    @Test
    void fieldWithoutGetter() throws Exception {
        outputUtils.assertIsEqualToDatabind(new JsonIgnoreOnFieldWithoutGetter("x"), serde::writeFieldWithoutGetter);
    }

    @Test
    void getter() throws Exception {
        outputUtils.assertIsEqualToDatabind(new JsonIgnoreOnGetter("x"), serde::writeGetter);
    }

    @Test
    void recordComponent() throws Exception {
        outputUtils.assertIsEqualToDatabind(new JsonIgnoreOnRecordComponent("x"), serde::writeRecordComponent);
    }
}
