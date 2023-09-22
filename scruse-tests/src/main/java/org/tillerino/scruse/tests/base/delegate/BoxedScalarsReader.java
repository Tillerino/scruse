package org.tillerino.scruse.tests.base.delegate;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.tests.base.PrimitiveScalarsReader;

import java.io.IOException;

/**
 * We use this interface as a delegate, so we mark methods with an X
 * to make sure they don't collide with other libraries' methods.
 */
@JsonConfig(uses = PrimitiveScalarsReader.class)
public interface BoxedScalarsReader {
	@JsonInput
	Boolean readBoxedBooleanX(JsonParser parser) throws IOException;
	@JsonInput
	Byte readBoxedByteX(JsonParser parser) throws IOException;
	@JsonInput
	Short readBoxedShortX(JsonParser parser) throws IOException;
	@JsonInput
	Integer readBoxedIntX(JsonParser parser) throws IOException;
	@JsonInput
	Long readBoxedLongX(JsonParser parser) throws IOException;
	@JsonInput
	Character readBoxedCharX(JsonParser parser) throws IOException;
	@JsonInput
	Float readBoxedFloatX(JsonParser parser) throws IOException;
	@JsonInput
	Double readBoxedDoubleX(JsonParser parser) throws IOException;
	@JsonInput
	String readStringX(JsonParser parser) throws IOException;
}
