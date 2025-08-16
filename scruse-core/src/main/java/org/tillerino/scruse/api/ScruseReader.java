package org.tillerino.scruse.api;

import java.io.IOException;

public interface ScruseReader<E extends Exception> {
    boolean isObjectStart(Advance advance) throws E;

    boolean isObjectEnd(Advance advance) throws E;

    boolean isArrayStart(Advance advance) throws E;

    boolean isArrayEnd(Advance advance) throws E;

    boolean isNull(Advance advance) throws E;

    boolean isBoolean() throws E;

    boolean isNumber() throws E;

    boolean isText() throws E;

    boolean isFieldName() throws E;

    boolean getBoolean(Advance advance) throws E;

    byte getByte(Advance advance) throws E;

    short getShort(Advance advance) throws E;

    int getInt(Advance advance) throws E;

    long getLong(Advance advance) throws E;

    float getFloat(Advance advance) throws E;

    double getDouble(Advance advance) throws E;

    String getText(Advance advance) throws E;

    String getFieldName(Advance advance) throws E;

    String getDiscriminator(String expectedName, boolean visible) throws E;

    void skipChildren(Advance advance) throws IOException;

    E unexpectedToken(String expectedToken);

    enum Advance {
        KEEP,
        CONSUME,
    }
}
