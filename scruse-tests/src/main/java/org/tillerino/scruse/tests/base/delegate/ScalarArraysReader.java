package org.tillerino.scruse.tests.base.delegate;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonConfig;
import org.tillerino.scruse.annotations.JsonInput;

import java.io.IOException;

@JsonConfig(uses = BoxedScalarsReader.class)
interface ScalarArraysReader {
	@JsonInput
	boolean[] readBooleanArray(JsonParser parser) throws IOException;

	@JsonInput
	byte[] readByteArray(JsonParser parser) throws IOException;

	@JsonInput
	char[] readCharArray(JsonParser parser) throws IOException;

	@JsonInput
	short[] readShortArray(JsonParser parser) throws IOException;

	@JsonInput
	int[] readIntArray(JsonParser parser) throws IOException;

	@JsonInput
	long[] readLongArray(JsonParser parser) throws IOException;

	@JsonInput
	float[] readFloatArray(JsonParser parser) throws IOException;

	@JsonInput
	double[] readDoubleArray(JsonParser parser) throws IOException;

	@JsonInput
	Boolean[] readBoxedBooleanArray(JsonParser parser) throws IOException;

	@JsonInput
	Byte[] readBoxedByteArray(JsonParser parser) throws IOException;

	@JsonInput
	Short[] readBoxedShortArray(JsonParser parser) throws IOException;

	@JsonInput
	Integer[] readBoxedIntArray(JsonParser parser) throws IOException;

	@JsonInput
	Long[] readBoxedLongArray(JsonParser parser) throws IOException;

	@JsonInput
	Float[] readBoxedFloatArray(JsonParser parser) throws IOException;

	@JsonInput
	Double[] readBoxedDoubleArray(JsonParser parser) throws IOException;

	@JsonInput
	String[] readStringArray(JsonParser parser) throws IOException;
}
