package org.tillerino.scruse.tests.base.delegate;

import com.fasterxml.jackson.core.JsonGenerator;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;

@JsonConfig(uses = BoxedScalarsWriter.class)
interface ScalarArraysWriter {
	@JsonOutput
	void writeBooleanArray(boolean[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeByteArray(byte[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeShortArray(short[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeIntArray(int[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeLongArray(long[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeCharArray(char[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeFloatArray(float[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeDoubleArray(double[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedBooleanArray(Boolean[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedByteArray(Byte[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedShortArray(Short[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedIntArray(Integer[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedLongArray(Long[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedCharArray(Character[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedFloatArray(Float[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedDoubleArray(Double[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeStringArray(String[] input, JsonGenerator generator) throws IOException;
}
