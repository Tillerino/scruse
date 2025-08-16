package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;

class PolymorphismTest extends ReferenceTest {
    PolymorphismSerde serde = new PolymorphismSerdeImpl();

    PolymorphismSerde.WithDelegate withDelegate = new PolymorphismSerde$WithDelegateImpl();

    @Test
    void sealedInterfaceDefaultsOutput() throws Exception {
        outputUtils.assertIsEqualToDatabind(new PolymorphismSerde.RecordOne("abc"), serde::writePolymorphism);
        outputUtils.assertIsEqualToDatabind(new PolymorphismSerde.RecordTwo(123), serde::writePolymorphism);
    }

    @Test
    void sealedInterfaceWithDelegatorDefaultsOutput() throws Exception {
        outputUtils.assertIsEqualToDatabind(new PolymorphismSerde.RecordOne("abc"), withDelegate::writePolymorphism);
        outputUtils.assertIsEqualToDatabind(new PolymorphismSerde.RecordTwo(123), withDelegate::writePolymorphism);
    }

    @Test
    void sealedInterfaceDefaultsInput() throws Exception {
        inputUtils.assertIsEqualToDatabind(
                "{\"@c\": \".PolymorphismSerde$RecordOne\", \"s\":\"abc\"}",
                serde::readPolymorphism,
                new TypeReference<>() {});
        inputUtils.assertIsEqualToDatabind(
                "{\"@c\": \".PolymorphismSerde$RecordTwo\", \"i\": 123}",
                serde::readPolymorphism,
                new TypeReference<>() {});
    }

    @Test
    void sealedInterfaceWithDelefatorDefaultsInput() throws Exception {
        inputUtils.assertIsEqualToDatabind(
                "{\"@c\": \".PolymorphismSerde$RecordOne\", \"s\":\"abc\"}",
                withDelegate::readPolymorphism,
                new TypeReference<>() {});
        inputUtils.assertIsEqualToDatabind(
                "{\"@c\": \".PolymorphismSerde$RecordTwo\", \"i\": 123}",
                withDelegate::readPolymorphism,
                new TypeReference<>() {});
    }
}
