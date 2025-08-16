package org.tillerino.scruse.tests.base.annotations;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.TestSettings;
import org.tillerino.scruse.tests.model.annotations.*;

class JsonValueAndJsonCreatorTest extends ReferenceTest {
    JsonValueAndJsonCreatorSerde serde = new JsonValueAndJsonCreatorSerdeImpl();

    @Test
    public void jsonValueOutput() throws Exception {
        for (Integer boxedInt : TestSettings.SETTINGS.javaData().BOXED_INTS) {
            JsonValueRecord<Integer> obj = new JsonValueRecord<>(boxedInt);
            outputUtils.assertIsEqualToDatabind(obj, serde::write);
        }
    }

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorConstructorFactory() throws Exception {
        JsonCreatorConstructorFactoryClass<Integer> o = inputUtils.assertIsEqualToDatabind(
                "{ \"notprop\": 1 }", serde::readConstructorFactory, new TypeReference<>() {});
        assertThat(o.getProp()).isEqualTo(1);
    }

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorRecordFactory() throws Exception {
        JsonCreatorMethodFactoryRecord<Integer> o = inputUtils.assertIsEqualToDatabind(
                "{ \"notprop\": 1 }", serde::readMethodFactory, new TypeReference<>() {});
        assertThat(o.prop()).isEqualTo(1);
    }

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorConstructorCreator() throws Exception {
        JsonCreatorConstructorCreatorClass<Integer> o = inputUtils.assertIsEqualToDatabind(
                "{ \"notprop\": 1, \"nots\": \"x\" }", serde::readConstructorCreator, new TypeReference<>() {});
        assertThat(o.getProp()).isEqualTo(1);
        assertThat(o.getS()).isEqualTo("x");
    }

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorMethodCreator() throws Exception {
        JsonCreatorMethodCreatorRecord<Integer> o = inputUtils.assertIsEqualToDatabind(
                "{ \"notprop\": 1, \"nots\": \"x\" }", serde::readMethodCreator, new TypeReference<>() {});
        assertThat(o.prop()).isEqualTo(1);
        assertThat(o.s()).isEqualTo("x");
    }
}
