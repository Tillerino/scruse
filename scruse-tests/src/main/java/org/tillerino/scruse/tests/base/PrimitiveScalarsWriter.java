package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;

public interface PrimitiveScalarsWriter {
	@JsonOutput
	void writeBoolean(boolean b, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeByte(byte b, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeShort(short s, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeInt(int i, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeLong(long l, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeCharacter(char c, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeFloat(float f, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeDouble(double d, JsonGenerator generator) throws IOException;
}
