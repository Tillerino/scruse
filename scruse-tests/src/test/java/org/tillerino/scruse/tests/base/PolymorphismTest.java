package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.OutputUtils;

import java.io.IOException;

class PolymorphismTest {
	PolymorphismSerde serde = new PolymorphismSerdeImpl();
	@Test
	void sealedInterfaceDefaults() throws IOException {
		OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind(new PolymorphismSerde.RecordOne("abc"), serde::writePolymorphism);
		OutputUtils.assertThatJacksonJsonGeneratorIsEqualToDatabind(new PolymorphismSerde.RecordTwo(123), serde::writePolymorphism);
	}
}
