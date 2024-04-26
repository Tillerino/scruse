package org.tillerino.scruse.tests.base.annotations;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.model.annotations.*;

class JsonIgnoreTest {
    JsonIgnoreSerde serde = new JsonIgnoreSerdeImpl();

    @Test
    void fieldWithGetter() throws Exception {
        OutputUtils.assertIsEqualToDatabind(new JsonIgnoreOnFieldWithGetter("x"), serde::writeFieldWithGetter);
    }

    @Test
    void fieldAndGetter() throws Exception {
        OutputUtils.assertIsEqualToDatabind(new JsonIgnoreOnFieldAndGetter("x"), serde::writeFieldAndGetter);
    }

    @Test
    void fieldWithoutGetter() throws Exception {
        OutputUtils.assertIsEqualToDatabind(new JsonIgnoreOnFieldWithoutGetter("x"), serde::writeFieldWithoutGetter);
    }

    @Test
    void getter() throws Exception {
        OutputUtils.assertIsEqualToDatabind(new JsonIgnoreOnGetter("x"), serde::writeGetter);
    }

    @Test
    void recordComponent() throws Exception {
        OutputUtils.assertIsEqualToDatabind(new JsonIgnoreOnRecordComponent("x"), serde::writeRecordComponent);
    }
}
