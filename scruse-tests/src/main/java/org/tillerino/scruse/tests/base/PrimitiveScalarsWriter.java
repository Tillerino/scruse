package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;

public interface PrimitiveScalarsWriter {
	@JsonOutput
	void writePrimitiveBooleanX(boolean b, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveByteX(byte b, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveShortX(short s, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveIntX(int i, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveLongX(long l, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveCharX(char c, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveFloatX(float f, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveDoubleX(double d, JsonGenerator generator) throws IOException;
}
