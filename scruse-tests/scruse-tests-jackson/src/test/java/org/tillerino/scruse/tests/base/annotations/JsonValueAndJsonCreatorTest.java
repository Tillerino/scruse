package org.tillerino.scruse.tests.base.annotations;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.SerdeUtil;
import org.tillerino.scruse.tests.TestSettings;
import org.tillerino.scruse.tests.model.annotations.*;

class JsonValueAndJsonCreatorTest extends ReferenceTest {
    JsonValueAndJsonCreatorSerde serde = SerdeUtil.impl(JsonValueAndJsonCreatorSerde.class);

    @Test
    public void jsonValueOutput() throws Exception {
        for (Integer boxedInt : TestSettings.SETTINGS.javaData().BOXED_INTS) {
            JsonValueRecord<Integer> obj = new JsonValueRecord<>(boxedInt);
            outputUtils.assertIsEqualToDatabind(obj, serde::write);
        }
    }
}
