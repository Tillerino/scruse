package org.tillerino.scruse.libs.gson;

import com.google.gson.stream.JsonReader;

import java.io.IOException;

public class JsonReaderHelper {

	public static boolean skipEndObject(JsonReader reader) throws IOException {
		reader.endObject();
		return false;
	}

	public static boolean skipEndArray(JsonReader reader) throws IOException {
		reader.endArray();
		return false;
	}
}
