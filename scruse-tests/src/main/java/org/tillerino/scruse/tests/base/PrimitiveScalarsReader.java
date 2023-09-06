package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;

import java.io.IOException;

public interface PrimitiveScalarsReader {
	@JsonInput
	boolean readBoolean(JsonParser parser) throws IOException;
	@JsonInput
	byte readByte(JsonParser parser) throws IOException;
	@JsonInput
	short readShort(JsonParser parser) throws IOException;
	@JsonInput
	int readInt(JsonParser parser) throws IOException;
	@JsonInput
	long readLong(JsonParser parser) throws IOException;
	@JsonInput
	char readCharacter(JsonParser parser) throws IOException;
	@JsonInput
	float readFloat(JsonParser parser) throws IOException;
	@JsonInput
	double readDouble(JsonParser parser) throws IOException;
}
