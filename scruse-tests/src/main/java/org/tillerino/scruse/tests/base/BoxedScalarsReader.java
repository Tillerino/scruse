package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;

import java.io.IOException;

/**
 * This interface must be separated from {@link PrimitiveScalarsReader} because otherwise the boxed writers
 * delegate to the non-boxed ones.
 */
public interface BoxedScalarsReader {
	@JsonInput
	Boolean readBoxedBoolean(JsonParser parser) throws IOException;
	@JsonInput
	Byte readBoxedByte(JsonParser parser) throws IOException;
	@JsonInput
	Short readBoxedShort(JsonParser parser) throws IOException;
	@JsonInput
	Integer readBoxedInt(JsonParser parser) throws IOException;
	@JsonInput
	Long readBoxedLong(JsonParser parser) throws IOException;
	@JsonInput
	Character readBoxedChar(JsonParser parser) throws IOException;
	@JsonInput
	Float readBoxedFloat(JsonParser parser) throws IOException;
	@JsonInput
	Double readBoxedDouble(JsonParser parser) throws IOException;
	@JsonInput
	String readString(JsonParser parser) throws IOException;
}
