package org.tillerino.scruse.tests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import org.tillerino.scruse.annotations.JsonOutput;

public class SimpleClass {
	String stringField;
	boolean booleanField;
	int ignoredIntField;


	public SimpleClass(String stringField, boolean booleanField, int ignoredIntField) {
		this.stringField = stringField;
		this.booleanField = booleanField;
		this.ignoredIntField = ignoredIntField;
	}

	public String getStringField() {
		return stringField;
	}

	public void setStringField(String stringField) {
		this.stringField = stringField;
	}

	public boolean isBooleanField() {
		return booleanField;
	}

	public void setBooleanField(boolean booleanField) {
		this.booleanField = booleanField;
	}

	@JsonIgnore
	public int getIgnoredIntField() {
		return ignoredIntField;
	}

	public void setIgnoredIntField(int ignoredIntField) {
		this.ignoredIntField = ignoredIntField;
	}

	interface Writer {
		@JsonOutput
		void write(SimpleClass object, JsonGenerator generator) throws java.io.IOException;
		@JsonOutput
		void write(SimpleClass object, com.google.gson.stream.JsonWriter generator) throws java.io.IOException;
		@JsonOutput
		JsonNode write(SimpleClass object);
	}
}
