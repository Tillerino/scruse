package org.tillerino.scruse.tests;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;

import java.io.IOException;

public interface ScalarsReader {
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
	@JsonInput
	Boolean readBoxedBoolean(JsonParser parser) throws IOException;
	@JsonInput
	Byte readBoxedByte(JsonParser parser) throws IOException;
	@JsonInput
	Short readBoxedShort(JsonParser parser) throws IOException;
	@JsonInput
	Integer readBoxedInt(JsonParser parser) throws IOException;
	@JsonInput
	Long readBoxedLong(JsonParser parser) throws IOException;
	@JsonInput
	Character readBoxedCharacter(JsonParser parser) throws IOException;
	@JsonInput
	Float readBoxedFloat(JsonParser parser) throws IOException;
	@JsonInput
	Double readBoxedDouble(JsonParser parser) throws IOException;
	@JsonInput
	String readString(JsonParser parser) throws IOException;
}
