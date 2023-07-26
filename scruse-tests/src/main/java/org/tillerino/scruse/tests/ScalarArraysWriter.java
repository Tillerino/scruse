package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.stream.JsonWriter;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;

public interface ScalarArraysWriter {
	@JsonOutput
	void writePrimitiveBooleanArray(boolean[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveBooleanArray(boolean[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writePrimitiveBooleanArray(boolean[] input);

	@JsonOutput
	void writePrimitiveByteArray(byte[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveByteArray(byte[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writePrimitiveByteArray(byte[] input);

	@JsonOutput
	void writePrimitiveShortArray(short[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveShortArray(short[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writePrimitiveShortArray(short[] input);

	@JsonOutput
	void writePrimitiveIntArray(int[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveIntArray(int[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writePrimitiveIntArray(int[] input);

	@JsonOutput
	void writePrimitiveLongArray(long[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveLongArray(long[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writePrimitiveLongArray(long[] input);

	@JsonOutput
	void writePrimitiveCharArray(char[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveCharArray(char[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writePrimitiveCharArray(char[] input);

	@JsonOutput
	void writePrimitiveFloatArray(float[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveFloatArray(float[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writePrimitiveFloatArray(float[] input);

	@JsonOutput
	void writePrimitiveDoubleArray(double[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writePrimitiveDoubleArray(double[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writePrimitiveDoubleArray(double[] input);

	@JsonOutput
	void writeBoxedBooleanArray(Boolean[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedBooleanArray(Boolean[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedBooleanArray(Boolean[] input);

	@JsonOutput
	void writeBoxedByteArray(Byte[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedByteArray(Byte[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedByteArray(Byte[] input);

	@JsonOutput
	void writeBoxedShortArray(Short[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedShortArray(Short[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedShortArray(Short[] input);

	@JsonOutput
	void writeBoxedIntArray(Integer[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedIntArray(Integer[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedIntArray(Integer[] input);

	@JsonOutput
	void writeBoxedLongArray(Long[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedLongArray(Long[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedLongArray(Long[] input);

	@JsonOutput
	void writeBoxedCharArray(Character[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedCharArray(Character[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedCharArray(Character[] input);

	@JsonOutput
	void writeBoxedFloatArray(Float[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedFloatArray(Float[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedFloatArray(Float[] input);

	@JsonOutput
	void writeBoxedDoubleArray(Double[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedDoubleArray(Double[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedDoubleArray(Double[] input);

	@JsonOutput
	void writeStringArray(String[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeStringArray(String[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeStringArray(String[] input);
}
