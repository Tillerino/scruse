package org.tillerino.scruse.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import org.tillerino.scruse.api.ScruseWriter;

public class JacksonJsonGeneratorAdapter implements ScruseWriter<IOException> {
    protected final JsonGenerator generator;

    /**
     * Create a new JacksonJsonGeneratorAdapter.
     *
     * @param generator you can easily create a {@link JsonGenerator} through
     *     {@link com.fasterxml.jackson.core.JsonFactory#createGenerator(java.io.OutputStream)} and similar methods.
     */
    public JacksonJsonGeneratorAdapter(JsonGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void startObject() throws IOException {
        generator.writeStartObject();
    }

    @Override
    public void endObject() throws IOException {
        generator.writeEndObject();
    }

    @Override
    public void startArray() throws IOException {
        generator.writeStartArray();
    }

    @Override
    public void endArray() throws IOException {
        generator.writeEndArray();
    }

    @Override
    public void writeNull() throws IOException {
        generator.writeNull();
    }

    @Override
    public void write(boolean value) throws IOException {
        generator.writeBoolean(value);
    }

    @Override
    public void write(byte value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void write(short value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void write(int value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void write(long value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void write(float value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void write(double value) throws IOException {
        generator.writeNumber(value);
    }

    @Override
    public void write(String value) throws IOException {
        generator.writeString(value);
    }

    @Override
    public void writeFieldName(String name) throws IOException {
        generator.writeFieldName(name);
    }
}
