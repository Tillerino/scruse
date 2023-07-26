package org.tillerino.scruse.apis;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class JsonGeneratorApi {

	// PRIMITIVES
	public static void writePrimitive(boolean value, JsonGenerator generator) throws IOException {
		generator.writeBoolean(value);
	}
	public static void writePrimitive(short value, JsonGenerator generator) throws IOException {
		generator.writeNumber(value);
	}
	public static void writePrimitive(int value, JsonGenerator generator) throws IOException {
		generator.writeNumber(value);
	}
	public static void writePrimitive(long value, JsonGenerator generator) throws IOException {
		generator.writeNumber(value);
	}
	public static void writePrimitive(float value, JsonGenerator generator) throws IOException {
		generator.writeNumber(value);
	}
	public static void writePrimitive(double value, JsonGenerator generator) throws IOException {
		generator.writeNumber(value);
	}

	// PRIMITIVE FIELDS
	public static void writePrimitiveField(String fieldName, boolean value, JsonGenerator generator) throws IOException {
		generator.writeBooleanField(fieldName, value);
	}
	public static void writePrimitiveField(String fieldName, short value, JsonGenerator generator) throws IOException {
		generator.writeNumberField(fieldName, value);
	}
	public static void writePrimitiveField(String fieldName, int value, JsonGenerator generator) throws IOException {
		generator.writeNumberField(fieldName, value);
	}
	public static void writePrimitiveField(String fieldName, long value, JsonGenerator generator) throws IOException {
		generator.writeNumberField(fieldName, value);
	}
	public static void writePrimitiveField(String fieldName, float value, JsonGenerator generator) throws IOException {
		generator.writeNumberField(fieldName, value);
	}
	public static void writePrimitiveField(String fieldName, double value, JsonGenerator generator) throws IOException {
		generator.writeNumberField(fieldName, value);
	}

	// NON-PRIMITIVE SCALARS
	public static void writeNonNull(String value, JsonGenerator generator) throws IOException {
		generator.writeString(value);
	}
	public static void writeNonNull(Boolean value, JsonGenerator generator) throws IOException {
		generator.writeBoolean(value);
	}
	public static void writeNonNull(Short value, JsonGenerator generator) throws IOException {
		generator.writeNumber(value);
	}
	public static void writeNonNull(Integer value, JsonGenerator generator) throws IOException {
		generator.writeNumber(value);
	}
	public static void writeNonNull(Long value, JsonGenerator generator) throws IOException {
		generator.writeNumber(value);
	}
	public static void writeNonNull(Float value, JsonGenerator generator) throws IOException {
		generator.writeNumber(value);
	}
	public static void writeNonNull(Double value, JsonGenerator generator) throws IOException {
		generator.writeNumber(value);
	}
	public static void writeNonNull(BigInteger value, JsonGenerator generator) throws IOException {
		generator.writeNumber(value);
	}
	public static void writeNonNull(BigDecimal value, JsonGenerator generator) throws IOException {
		generator.writeNumber(value);
	}
	public static void writeNonNull(byte[] value, JsonGenerator generator) throws IOException {
		generator.writeBinary(value);
	}

	public static void writeNull(JsonGenerator generator) throws IOException {
		generator.writeNull();
	}
}
