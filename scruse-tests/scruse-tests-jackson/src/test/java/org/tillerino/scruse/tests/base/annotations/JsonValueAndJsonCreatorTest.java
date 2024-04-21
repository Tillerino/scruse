package org.tillerino.scruse.tests.base.annotations;

import com.fasterxml.jackson.core.type.TypeReference;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.OutputUtils;
import org.tillerino.scruse.tests.TestSettings;
import org.tillerino.scruse.tests.model.annotations.JsonCreatorConstructorClass;
import org.tillerino.scruse.tests.model.annotations.JsonValueRecord;

class JsonValueAndJsonCreatorTest {
    JsonValueAndJsonCreatorSerde serde = new JsonValueAndJsonCreatorSerdeImpl();

    @Test
    public void jsonValueOutput() throws Exception {
        for (Integer boxedInt : TestSettings.SETTINGS.javaData().BOXED_INTS) {
            JsonValueRecord<Integer> obj = new JsonValueRecord<>(boxedInt);
            OutputUtils.assertIsEqualToDatabind(obj, serde::write);
        }
    }

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorConstructor() throws Exception {
        JsonCreatorConstructorClass<Integer> o = InputUtils.assertIsEqualToDatabind(
                "{ \"notprop\": 1 }", serde::readConstructor, new TypeReference<>() {});
        Assertions.assertThat(o.getProp()).isEqualTo(1);
    }
}
