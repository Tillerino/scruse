package com.grack.nanojson;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Nanojson's {@link JsonReader} is not suitable for parsing, as its next method does not support writing delegators.
 * Simply instantiate this class to parse with Nanojson.
 */
public class TokenerWrapper {
    private final JsonTokener tokener;
    private int token = JsonTokener.TOKEN_VALUE_MIN;

    public static final int TOKEN_EOF = JsonTokener.TOKEN_EOF;
    public static final int TOKEN_COMMA = JsonTokener.TOKEN_COMMA;
    public static final int TOKEN_COLON = JsonTokener.TOKEN_COLON;
    public static final int TOKEN_OBJECT_END = JsonTokener.TOKEN_OBJECT_END;
    public static final int TOKEN_ARRAY_END = JsonTokener.TOKEN_ARRAY_END;
    public static final int TOKEN_NULL = JsonTokener.TOKEN_NULL;
    public static final int TOKEN_TRUE = JsonTokener.TOKEN_TRUE;
    public static final int TOKEN_FALSE = JsonTokener.TOKEN_FALSE;
    public static final int TOKEN_STRING = JsonTokener.TOKEN_STRING;
    public static final int TOKEN_NUMBER = JsonTokener.TOKEN_NUMBER;
    public static final int TOKEN_OBJECT_START = JsonTokener.TOKEN_OBJECT_START;
    public static final int TOKEN_ARRAY_START = JsonTokener.TOKEN_ARRAY_START;

    public TokenerWrapper(Reader reader) throws JsonParserException {
        tokener = new JsonTokener(reader);
        token = tokener.advanceToToken();
    }

    public TokenerWrapper(InputStream stm) throws JsonParserException {
        tokener = new JsonTokener(stm);
        token = tokener.advanceToToken();
    }

    public int current() {
        return token;
    }

    public int next() throws JsonParserException {
        token = tokener.advanceToToken();
        return token;
    }

    public boolean arrayLoop() throws JsonParserException {
        if (this.token == TOKEN_COMMA) {
            next();
        }
        if (this.token == TOKEN_ARRAY_END) {
            next();
            return false;
        }
        return this.token != TOKEN_EOF;
    }

    public boolean objectLoop() throws JsonParserException {
        if (this.token == TOKEN_COMMA) {
            next();
        }
        if (this.token == TOKEN_OBJECT_END) {
            next();
            return false;
        }
        return this.token != TOKEN_EOF;
    }

    public String fieldNameAndSkipColon() throws JsonParserException, IOException {
        String fieldName = string();
        if (next() != TOKEN_COLON) {
            throw new IOException("Expected colon, got " + this.token);
        }
        next();
        return fieldName;
    }

    public boolean nextIfCurrentTokenIs(int token) throws JsonParserException, IOException {
        if (this.token == JsonTokener.TOKEN_EOF) {
            throw new IOException("Unexpected EOF");
        }
        if (this.token == token) {
            next();
            return true;
        }
        return false;
    }

    public String readDiscriminator(String discriminatorName) throws IOException, JsonParserException {
        if (current() != TOKEN_STRING) {
            throw new IOException("Expected field name, got " + current());
        } else if (!string().equals(discriminatorName)) {
            throw new IOException("Expected field name " + discriminatorName + ", got " + string());
        } else if (next() != TOKEN_COLON) {
            throw new IOException("Expected colon, got " + current());
        } else if (next() != TOKEN_STRING) {
            throw new IOException("Expected string, got " + current());
        } else {
            String value = string();
            next();
            return value;
        }
    }

    public String string() {
        return tokener.reusableBuffer.toString();
    }

    public boolean bool() {
        return token == TOKEN_TRUE;
    }

    public int intVal() {
        String s = tokener.reusableBuffer.toString();
        return tokener.isDouble ? (int) Double.parseDouble(s) : Integer.parseInt(s);
    }

    public long longVal() throws JsonParserException {
        String s = tokener.reusableBuffer.toString();
        return tokener.isDouble ? (long) Double.parseDouble(s) : Long.parseLong(s);
    }

    public float floatVal() throws JsonParserException {
        String s = tokener.reusableBuffer.toString();
        return Float.parseFloat(s);
    }

    public double doubleVal() throws JsonParserException {
        String s = tokener.reusableBuffer.toString();
        return Double.parseDouble(s);
    }

    public void skipChildren() throws JsonParserException {
        if (current() == TOKEN_ARRAY_START) {
            next();
            while (current() != TOKEN_ARRAY_END && current() != TOKEN_EOF) {
                skipChildren();
                next();
            }
        }
        if (current() == TOKEN_OBJECT_START) {
            next();
            while (current() != TOKEN_OBJECT_END && current() != TOKEN_EOF) {
                skipChildren();
                next();
            }
        }
    }
}
