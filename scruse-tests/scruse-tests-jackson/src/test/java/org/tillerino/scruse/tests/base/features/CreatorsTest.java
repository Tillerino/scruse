package org.tillerino.scruse.tests.base.features;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.SerdeUtil;
import org.tillerino.scruse.tests.model.features.CreatorsModel;
import org.tillerino.scruse.tests.model.features.CreatorsModel.JsonCreatorConstructorFactoryMultiplePropertiesClass;
import org.tillerino.scruse.tests.model.features.CreatorsModel.JsonCreatorMethodFactoryMultiplePropertiesRecord;

public class CreatorsTest extends ReferenceTest {
    CreatorsSerde serde = SerdeUtil.impl(CreatorsSerde.class);

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorConstructorFactory() throws Exception {
        CreatorsModel.JsonCreatorConstructorFactoryClass<Integer> o = inputUtils.assertIsEqualToDatabind(
                "{ \"notprop\": 1 }", serde::readConstructorFactory, new TypeReference<>() {});
        assertThat(o.getProp()).isEqualTo(1);
    }

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorRecordFactory() throws Exception {
        CreatorsModel.JsonCreatorMethodFactoryRecord<Integer> o = inputUtils.assertIsEqualToDatabind(
                "{ \"notprop\": 1 }", serde::readMethodFactory, new TypeReference<>() {});
        assertThat(o.prop()).isEqualTo(1);
    }

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorConstructorFactoryMultipleProperties() throws Exception {
        JsonCreatorConstructorFactoryMultiplePropertiesClass<Integer> o =
                inputUtils.deserialize2("1", Thread.currentThread(), serde::readConstructorFactoryMultipleProperties);
        assertThat(o.getProp()).isEqualTo(1);
        assertThat(o.getT()).isSameAs(Thread.currentThread());
    }

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorRecordFactoryMultipleProperties() throws Exception {
        JsonCreatorMethodFactoryMultiplePropertiesRecord<Integer> o =
                inputUtils.deserialize2("1", Thread.currentThread(), serde::readMethodFactoryMultipleProperties);
        assertThat(o.prop()).isEqualTo(1);
        assertThat(o.t()).isSameAs(Thread.currentThread());
    }

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorConstructorCreator() throws Exception {
        CreatorsModel.JsonCreatorConstructorCreatorClass<Integer> o = inputUtils.assertIsEqualToDatabind(
                "{ \"notprop\": 1, \"nots\": \"x\" }", serde::readConstructorCreator, new TypeReference<>() {});
        assertThat(o.getProp()).isEqualTo(1);
        assertThat(o.getS()).isEqualTo("x");
    }

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorMethodCreator() throws Exception {
        CreatorsModel.JsonCreatorMethodCreatorRecord<Integer> o = inputUtils.assertIsEqualToDatabind(
                "{ \"notprop\": 1, \"nots\": \"x\" }", serde::readMethodCreator, new TypeReference<>() {});
        assertThat(o.prop()).isEqualTo(1);
        assertThat(o.s()).isEqualTo("x");
    }

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorMethodCreatorSinglePropertyRecord() throws Exception {
        CreatorsModel.JsonCreatorMethodCreatorSinglePropertyRecord<Integer> o = inputUtils.assertIsEqualToDatabind(
                "{ \"notprop\": 1 }", serde::readMethodCreatorSingleProperty, new TypeReference<>() {});
        assertThat(o.prop()).containsExactly(1);
    }

    /** FEATURE-JSON */
    @Test
    public void jsonCreatorConstructorCreatorSinglePropertyRecord() throws Exception {
        CreatorsModel.JsonCreatorConstructorCreatorSinglePropertyClass<Integer> o = inputUtils.assertIsEqualToDatabind(
                "{ \"notprop\": 1 }", serde::readConstructorCreatorSingleProperty, new TypeReference<>() {});
        assertThat(o.getProp()).containsExactly(1);
    }
}
