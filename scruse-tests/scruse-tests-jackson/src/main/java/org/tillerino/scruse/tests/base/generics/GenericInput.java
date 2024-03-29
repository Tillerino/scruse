package org.tillerino.scruse.tests.base.generics;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;

import java.io.IOException;

// use 'V' instead of 'T' to make sure that we actually instantiate the type
@JsonConfig(implement = JsonConfig.ImplementationMode.DO_NOT_IMPLEMENT)
public interface GenericInput<V> {
	@JsonInput
	V readOnGenericInterface(JsonParser parser) throws IOException;
}
