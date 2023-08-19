package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.stream.JsonWriter;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;

public interface ScalarArraysWriter {
	@JsonOutput
	void writeBooleanArray(boolean[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBooleanArray(boolean[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBooleanArray(boolean[] input);

	@JsonOutput
	void writeByteArray(byte[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeByteArray(byte[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeByteArray(byte[] input);

	@JsonOutput
	void writeShortArray(short[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeShortArray(short[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeShortArray(short[] input);

	@JsonOutput
	void writeIntArray(int[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeIntArray(int[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeIntArray(int[] input);

	@JsonOutput
	void writeLongArray(long[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeLongArray(long[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeLongArray(long[] input);

	@JsonOutput
	void writeCharArray(char[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeCharArray(char[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeCharArray(char[] input);

	@JsonOutput
	void writeFloatArray(float[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeFloatArray(float[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeFloatArray(float[] input);

	@JsonOutput
	void writeDoubleArray(double[] input, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeDoubleArray(double[] input, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeDoubleArray(double[] input);

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
