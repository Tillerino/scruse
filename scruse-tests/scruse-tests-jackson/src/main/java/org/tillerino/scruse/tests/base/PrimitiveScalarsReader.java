package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;

import java.io.IOException;

/**
 * We use this interface as a delegate, so we mark methods with an X
 * to make sure they don't collide with other libraries' methods.
 */
public interface PrimitiveScalarsReader {
	@JsonInput
	boolean readPrimitiveBooleanX(JsonParser parser) throws IOException;
	@JsonInput
	byte readPrimitiveByteX(JsonParser parser) throws IOException;
	@JsonInput
	short readPrimitiveShortX(JsonParser parser) throws IOException;
	@JsonInput
	int readPrimitiveIntX(JsonParser parser) throws IOException;
	@JsonInput
	long readPrimitiveLongX(JsonParser parser) throws IOException;
	@JsonInput
	char readPrimitiveCharX(JsonParser parser) throws IOException;
	@JsonInput
	float readPrimitiveFloatX(JsonParser parser) throws IOException;
	@JsonInput
	double readPrimitiveDoubleX(JsonParser parser) throws IOException;
}
