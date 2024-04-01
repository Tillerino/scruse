package org.tillerino.scruse.tests.base;

import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.model.AnEnum;

class ScalarsWriterTest {
    PrimitiveScalarsWriter primitive = new PrimitiveScalarsWriterImpl();
    BoxedScalarsWriter boxed = new BoxedScalarsWriterImpl();

    @Test
    void testBoolean() throws IOException {
        for (boolean b : SETTINGS.javaData().BOOLEANS) {
            assertIsEqualToDatabind(b, primitive::writePrimitiveBooleanX);
        }
    }

    @Test
    void testByte() throws IOException {
        for (byte b : SETTINGS.javaData().BYTES) {
            assertIsEqualToDatabind(b, primitive::writePrimitiveByteX);
        }
    }

    @Test
    void testShort() throws IOException {
        for (short s : SETTINGS.javaData().SHORTS) {
            assertIsEqualToDatabind(s, primitive::writePrimitiveShortX);
        }
    }

    @Test
    void testInt() throws IOException {
        for (int i : SETTINGS.javaData().INTS) {
            assertIsEqualToDatabind(i, primitive::writePrimitiveIntX);
        }
    }

    @Test
    void testLong() throws IOException {
        for (long l : SETTINGS.javaData().LONGS) {
            assertIsEqualToDatabind(l, primitive::writePrimitiveLongX);
        }
    }

    @Test
    void testChar() throws IOException {
        for (char c : SETTINGS.javaData().CHARS) {
            assertIsEqualToDatabind(c, primitive::writePrimitiveCharX);
        }
    }

    @Test
    void testFloat() throws IOException {
        for (float f : SETTINGS.javaData().floats) {
            assertIsEqualToDatabind(f, primitive::writePrimitiveFloatX);
        }
    }

    @Test
    void testDouble() throws IOException {
        for (double d : SETTINGS.javaData().DOUBLES) {
            assertIsEqualToDatabind(d, primitive::writePrimitiveDoubleX);
        }
    }

    @Test
    void testBoxedBoolean() throws IOException {
        for (Boolean b : SETTINGS.javaData().BOXED_BOOLEANS) {
            assertIsEqualToDatabind(b, boxed::writeBoxedBoolean);
        }
    }

    @Test
    void testBoxedByte() throws IOException {
        for (Byte b : SETTINGS.javaData().BOXED_BYTES) {
            assertIsEqualToDatabind(b, boxed::writeBoxedByte);
        }
    }

    @Test
    void testBoxedShort() throws IOException {
        for (Short s : SETTINGS.javaData().BOXED_SHORTS) {
            assertIsEqualToDatabind(s, boxed::writeBoxedShort);
        }
    }

    @Test
    void testBoxedInteger() throws IOException {
        for (Integer i : SETTINGS.javaData().BOXED_INTS) {
            assertIsEqualToDatabind(i, boxed::writeBoxedInt);
        }
    }

    @Test
    void testBoxedLong() throws IOException {
        for (Long l : SETTINGS.javaData().BOXED_LONGS) {
            assertIsEqualToDatabind(l, boxed::writeBoxedLong);
        }
    }

    @Test
    void testBoxedChar() throws IOException {
        for (Character c : SETTINGS.javaData().BOXED_CHARS) {
            assertIsEqualToDatabind(c, boxed::writeBoxedChar);
        }
    }

    @Test
    void testBoxedFloat() throws IOException {
        for (Float f : SETTINGS.javaData().boxedFloats) {
            assertIsEqualToDatabind(f, boxed::writeBoxedFloat);
        }
    }

    @Test
    void testBoxedDouble() throws IOException {
        for (Double d : SETTINGS.javaData().BOXED_DOUBLES) {
            assertIsEqualToDatabind(d, boxed::writeBoxedDouble);
        }
    }

    @Test
    void testString() throws IOException {
        for (String s : SETTINGS.javaData().STRINGS) {
            assertIsEqualToDatabind(s, boxed::writeString);
        }
    }

    @Test
    void testEnum() throws IOException {
        for (AnEnum e : SETTINGS.javaData().ENUMS) {
            assertIsEqualToDatabind(e, boxed::writeEnum);
        }
    }
}
