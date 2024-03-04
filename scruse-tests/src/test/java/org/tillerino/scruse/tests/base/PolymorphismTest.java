package org.tillerino.scruse.tests.base;

import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.OutputUtils;

import java.io.IOException;

class PolymorphismTest {
	PolymorphismSerde serde = new PolymorphismSerdeImpl();

	PolymorphismSerde.WithDelegate withDelegate = new PolymorphismSerde$WithDelegateImpl();

	@Test
	void sealedInterfaceDefaults() throws IOException {
		OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind(new PolymorphismSerde.RecordOne("abc"), serde::writePolymorphism);
		OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind(new PolymorphismSerde.RecordTwo(123), serde::writePolymorphism);
	}

	@Test
	void sealedInterfaceWithDelegatorDefaults() throws IOException {
		OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind(new PolymorphismSerde.RecordOne("abc"), withDelegate::writePolymorphism);
		OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind(new PolymorphismSerde.RecordTwo(123), withDelegate::writePolymorphism);
	}
}
