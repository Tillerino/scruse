package org.tillerino.jagger.api;

public interface JaggerWriter<E extends Exception> {
    void startObject() throws E;

    void endObject() throws E;

    void startArray() throws E;

    void endArray() throws E;

    void writeNull() throws E;

    void write(boolean value) throws E;

    void write(byte value) throws E;

    void write(short value) throws E;

    void write(int value) throws E;

    void write(long value) throws E;

    void write(float value) throws E;

    void write(double value) throws E;

    void write(String value) throws E;

    void writeFieldName(String name) throws E;

    default void startObjectField(String name) throws E {
        writeFieldName(name);
        startObject();
    }

    default void startArrayField(String name) throws E {
        writeFieldName(name);
        startArray();
    }

    default void writeNullField(String name) throws E {
        writeFieldName(name);
        writeNull();
    }

    default void writeField(String name, boolean value) throws E {
        writeFieldName(name);
        write(value);
    }

    default void writeField(String name, byte value) throws E {
        writeFieldName(name);
        write(value);
    }

    default void writeField(String name, short value) throws E {
        writeFieldName(name);
        write(value);
    }

    default void writeField(String name, int value) throws E {
        writeFieldName(name);
        write(value);
    }

    default void writeField(String name, long value) throws E {
        writeFieldName(name);
        write(value);
    }

    default void writeField(String name, float value) throws E {
        writeFieldName(name);
        write(value);
    }

    default void writeField(String name, double value) throws E {
        writeFieldName(name);
        write(value);
    }

    default void writeField(String name, String value) throws E {
        writeFieldName(name);
        write(value);
    }
}
