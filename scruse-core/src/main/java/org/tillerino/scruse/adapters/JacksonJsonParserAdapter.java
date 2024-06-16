package org.tillerino.scruse.adapters;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import org.tillerino.scruse.api.ScruseReader;

public class JacksonJsonParserAdapter implements ScruseReader<IOException> {
    protected final JsonParser parser;

    /**
     * Create a new JacksonJsonParserAdapter.
     *
     * @param parser you can easily create a {@link JsonParser} through
     *     {@link com.fasterxml.jackson.core.JsonFactory#createParser(java.io.InputStream)} and similar methods.
     * @throws IOException when retrieving the first token fails
     */
    public JacksonJsonParserAdapter(JsonParser parser) throws IOException {
        this.parser = parser;
        if (!parser.hasCurrentToken()) {
            parser.nextToken();
        }
    }

    @Override
    public boolean isObjectStart(Advance advance) throws IOException {
        if (parser.currentToken() == JsonToken.START_OBJECT) {
            if (advance == Advance.CONSUME) {
                parser.nextToken();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isObjectEnd(Advance advance) throws IOException {
        if (parser.currentToken() == JsonToken.END_OBJECT) {
            if (advance == Advance.CONSUME) {
                parser.nextToken();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isArrayStart(Advance advance) throws IOException {
        if (parser.currentToken() == JsonToken.START_ARRAY) {
            if (advance == Advance.CONSUME) {
                parser.nextToken();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isArrayEnd(Advance advance) throws IOException {
        if (parser.currentToken() == JsonToken.END_ARRAY) {
            if (advance == Advance.CONSUME) {
                parser.nextToken();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isNull(Advance advance) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            if (advance == Advance.CONSUME) {
                parser.nextToken();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isBoolean() throws IOException {
        return parser.currentToken().isBoolean();
    }

    @Override
    public boolean isNumber() throws IOException {
        return parser.currentToken().isNumeric();
    }

    @Override
    public boolean isText() throws IOException {
        return parser.currentToken() == JsonToken.VALUE_STRING;
    }

    @Override
    public boolean isFieldName() throws IOException {
        return parser.currentToken() == JsonToken.FIELD_NAME;
    }

    @Override
    public boolean getBoolean(Advance advance) throws IOException {
        boolean value = parser.getBooleanValue();
        if (advance == Advance.CONSUME) {
            parser.nextToken();
        }
        return value;
    }

    @Override
    public byte getByte(Advance advance) throws IOException {
        byte value = parser.getByteValue();
        if (advance == Advance.CONSUME) {
            parser.nextToken();
        }
        return value;
    }

    @Override
    public short getShort(Advance advance) throws IOException {
        short value = parser.getShortValue();
        if (advance == Advance.CONSUME) {
            parser.nextToken();
        }
        return value;
    }

    @Override
    public int getInt(Advance advance) throws IOException {
        int value = parser.getIntValue();
        if (advance == Advance.CONSUME) {
            parser.nextToken();
        }
        return value;
    }

    @Override
    public long getLong(Advance advance) throws IOException {
        long value = parser.getLongValue();
        if (advance == Advance.CONSUME) {
            parser.nextToken();
        }
        return value;
    }

    @Override
    public float getFloat(Advance advance) throws IOException {
        float value = parser.getFloatValue();
        if (advance == Advance.CONSUME) {
            parser.nextToken();
        }
        return value;
    }

    @Override
    public double getDouble(Advance advance) throws IOException {
        double value = parser.getDoubleValue();
        if (advance == Advance.CONSUME) {
            parser.nextToken();
        }
        return value;
    }

    @Override
    public String getText(Advance advance) throws IOException {
        String value = parser.getText();
        if (advance == Advance.CONSUME) {
            parser.nextToken();
        }
        return value;
    }

    @Override
    public String getFieldName(Advance advance) throws IOException {
        String value = parser.currentName();
        if (advance == Advance.CONSUME) {
            parser.nextToken();
        }
        return value;
    }

    @Override
    public String getDiscriminator(String expectedName, boolean visible) throws IOException {
        if (visible) {
            throw new UnsupportedOperationException("visible discriminator not implemented yet");
        }
        String fieldName = getFieldName(Advance.CONSUME);
        if (!fieldName.equals(expectedName)) {
            throw new IOException("Expected discriminator " + expectedName + " but got " + fieldName + " at "
                    + parser.currentLocation());
        }
        if (!isText()) {
            throw unexpectedToken("text");
        }
        return getText(Advance.CONSUME);
    }

    @Override
    public IOException unexpectedToken(String expectedToken) {
        return new IOException(
                "Expected " + expectedToken + " but got " + parser.currentToken() + " at " + parser.currentLocation());
    }
}
