package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;

interface PolymorphismSerde {
	@JsonOutput
	void writePolymorphism(SealedInterface sealedInterface, JsonGenerator generator) throws IOException;

	@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
	sealed interface SealedInterface {
	}

	record RecordOne(String s) implements SealedInterface {
	}

	record RecordTwo(int i) implements SealedInterface {
	}
}
