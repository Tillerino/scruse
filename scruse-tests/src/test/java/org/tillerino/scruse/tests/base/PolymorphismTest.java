package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.InputUtils;
import org.tillerino.scruse.tests.OutputUtils;

import java.io.IOException;

class PolymorphismTest {
	PolymorphismSerde serde = new PolymorphismSerdeImpl();

	PolymorphismSerde.WithDelegate withDelegate = new PolymorphismSerde$WithDelegateImpl();

	@Test
	void sealedInterfaceDefaultsOutput() throws IOException {
		OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind(new PolymorphismSerde.RecordOne("abc"), serde::writePolymorphism);
		OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind(new PolymorphismSerde.RecordTwo(123), serde::writePolymorphism);
	}

	@Test
	void sealedInterfaceWithDelegatorDefaultsOutput() throws IOException {
		OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind(new PolymorphismSerde.RecordOne("abc"), withDelegate::writePolymorphism);
		OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind(new PolymorphismSerde.RecordTwo(123), withDelegate::writePolymorphism);
	}

	@Test
	void sealedInterfaceDefaultsInput() throws IOException {
		InputUtils.assertThatJacksonJsonParserIsEqualToDatabind("{\"@c\": \".PolymorphismSerde$RecordOne\", \"s\":\"abc\"}", serde::readPolymorphism, new TypeReference<>() {});
		InputUtils.assertThatJacksonJsonParserIsEqualToDatabind("{\"@c\": \".PolymorphismSerde$RecordTwo\", \"i\": 123}", serde::readPolymorphism, new TypeReference<>() {});
	}
}
