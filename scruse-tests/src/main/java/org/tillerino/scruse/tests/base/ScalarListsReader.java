package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;

import java.io.IOException;
import java.util.List;

interface ScalarListsReader {
	@JsonInput
	List<Boolean> readBooleanList(JsonParser parser) throws IOException;

	@JsonInput
	List<Byte> readByteList(JsonParser parser) throws IOException;

	@JsonInput
	List<Character> readCharacterList(JsonParser parser) throws IOException;

	@JsonInput
	List<Short> readShortList(JsonParser parser) throws IOException;

	@JsonInput
	List<Integer> readIntegerList(JsonParser parser) throws IOException;

	@JsonInput
	List<Long> readLongList(JsonParser parser) throws IOException;

	@JsonInput
	List<Float> readFloatList(JsonParser parser) throws IOException;

	@JsonInput
	List<Double> readDoubleList(JsonParser parser) throws IOException;

	@JsonInput
	List<String> readStringList(JsonParser parser) throws IOException;
}
