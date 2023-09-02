package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.stream.JsonWriter;
import org.tillerino.scruse.annotations.JsonOutput;

import java.io.IOException;

interface ScalarsWriter {
	@JsonOutput
	void writeBoolean(boolean b, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoolean(boolean b, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoolean(boolean b);

	@JsonOutput
	void writeByte(byte b, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeByte(byte b, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeByte(byte b);

	@JsonOutput
	void writeShort(short s, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeShort(short s, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeShort(short s);

	@JsonOutput
	void writeInt(int i, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeInt(int i, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeInt(int i);

	@JsonOutput
	void writeLong(long l, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeLong(long l, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeLong(long l);

	@JsonOutput
	void writeCharacter(char c, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeCharacter(char c, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeCharacter(char c);

	@JsonOutput
	void writeFloat(float f, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeFloat(float f, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeFloat(float f);

	@JsonOutput
	void writeDouble(double d, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeDouble(double d, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeDouble(double d);

	@JsonOutput
	void writeBoxedBoolean(Boolean b, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedBoolean(Boolean b, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedBoolean(Boolean b);

	@JsonOutput
	void writeBoxedByte(Byte b, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedByte(Byte b, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedByte(Byte b);

	@JsonOutput
	void writeBoxedShort(Short s, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedShort(Short s, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedShort(Short s);

	@JsonOutput
	void writeBoxedInt(Integer i, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedInt(Integer i, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedInt(Integer i);

	@JsonOutput
	void writeBoxedLong(Long l, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedLong(Long l, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedLong(Long l);

	@JsonOutput
	void writeBoxedCharacter(Character c, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedCharacter(Character c, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedCharacter(Character c);

	@JsonOutput
	void writeBoxedFloat(Float f, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedFloat(Float f, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedFloat(Float f);

	@JsonOutput
	void writeBoxedDouble(Double d, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeBoxedDouble(Double d, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeBoxedDouble(Double d);

	@JsonOutput
	void writeString(String s, JsonGenerator generator) throws IOException;

	@JsonOutput
	void writeString(String s, JsonWriter generator) throws IOException;

	@JsonOutput
	JsonNode writeString(String s);
}
