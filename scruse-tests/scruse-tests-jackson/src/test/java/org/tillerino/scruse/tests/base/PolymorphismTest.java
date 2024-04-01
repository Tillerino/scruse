package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.OutputUtils;

class PolymorphismTest {
    PolymorphismSerde serde = new PolymorphismSerdeImpl();

    PolymorphismSerde.WithDelegate withDelegate = new PolymorphismSerde$WithDelegateImpl();

    @Test
    void sealedInterfaceDefaults() throws IOException {
        OutputUtils.roundTrip(
                new PolymorphismSerde.RecordOne("abc"),
                serde::writePolymorphism,
                serde::readPolymorphism,
                new TypeReference<>() {});
        OutputUtils.roundTrip(
                new PolymorphismSerde.RecordTwo(123),
                serde::writePolymorphism,
                serde::readPolymorphism,
                new TypeReference<>() {});
    }

    @Test
    void sealedInterfaceWithDelegatorDefaultsOutput() throws IOException {
        OutputUtils.roundTripContext(
                new PolymorphismSerde.RecordOne("abc"),
                withDelegate::writePolymorphism,
                withDelegate::readPolymorphism,
                new TypeReference<>() {});
        OutputUtils.roundTripContext(
                new PolymorphismSerde.RecordTwo(123),
                withDelegate::writePolymorphism,
                withDelegate::readPolymorphism,
                new TypeReference<>() {});
    }
}
