package org.tillerino.scruse.tests.base;

import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.JavaData;
import org.tillerino.scruse.tests.model.AnEnum;

class ScalarsWriterTest {
    PrimitiveScalarsWriter primitive = new PrimitiveScalarsWriterImpl();
    BoxedScalarsWriter boxed = new BoxedScalarsWriterImpl();

    @Test
    void testBoolean() throws IOException {
        for (boolean b : JavaData.BOOLEANS) {
            assertIsEqualToDatabind(b, primitive::writePrimitiveBooleanX);
        }
    }

    @Test
    void testByte() throws IOException {
        for (byte b : JavaData.BYTES) {
            assertIsEqualToDatabind(b, primitive::writePrimitiveByteX);
        }
    }

    @Test
    void testShort() throws IOException {
        for (short s : JavaData.SHORTS) {
            assertIsEqualToDatabind(s, primitive::writePrimitiveShortX);
        }
    }

    @Test
    void testInt() throws IOException {
        for (int i : JavaData.INTS) {
            assertIsEqualToDatabind(i, primitive::writePrimitiveIntX);
        }
    }

    @Test
    void testLong() throws IOException {
        for (long l : JavaData.LONGS) {
            assertIsEqualToDatabind(l, primitive::writePrimitiveLongX);
        }
    }

    @Test
    void testChar() throws IOException {
        for (char c : JavaData.CHARS) {
            assertIsEqualToDatabind(c, primitive::writePrimitiveCharX);
        }
    }

    @Test
    void testFloat() throws IOException {
        for (float f : JavaData.FLOATS) {
            assertIsEqualToDatabind(f, primitive::writePrimitiveFloatX);
        }
    }

    @Test
    void testDouble() throws IOException {
        for (double d : JavaData.DOUBLES) {
            assertIsEqualToDatabind(d, primitive::writePrimitiveDoubleX);
        }
    }

    @Test
    void testBoxedBoolean() throws IOException {
        for (Boolean b : JavaData.BOXED_BOOLEANS) {
            assertIsEqualToDatabind(b, boxed::writeBoxedBoolean);
        }
    }

    @Test
    void testBoxedByte() throws IOException {
        for (Byte b : JavaData.BOXED_BYTES) {
            assertIsEqualToDatabind(b, boxed::writeBoxedByte);
        }
    }

    @Test
    void testBoxedShort() throws IOException {
        for (Short s : JavaData.BOXED_SHORTS) {
            assertIsEqualToDatabind(s, boxed::writeBoxedShort);
        }
    }

    @Test
    void testBoxedInteger() throws IOException {
        for (Integer i : JavaData.BOXED_INTS) {
            assertIsEqualToDatabind(i, boxed::writeBoxedInt);
        }
    }

    @Test
    void testBoxedLong() throws IOException {
        for (Long l : JavaData.BOXED_LONGS) {
            assertIsEqualToDatabind(l, boxed::writeBoxedLong);
        }
    }

    @Test
    void testBoxedChar() throws IOException {
        for (Character c : JavaData.BOXED_CHARS) {
            assertIsEqualToDatabind(c, boxed::writeBoxedChar);
        }
    }

    @Test
    void testBoxedFloat() throws IOException {
        for (Float f : JavaData.BOXED_FLOATS) {
            assertIsEqualToDatabind(f, boxed::writeBoxedFloat);
        }
    }

    @Test
    void testBoxedDouble() throws IOException {
        for (Double d : JavaData.BOXED_DOUBLES) {
            assertIsEqualToDatabind(d, boxed::writeBoxedDouble);
        }
    }

    @Test
    void testString() throws IOException {
        for (String s : JavaData.STRINGS) {
            assertIsEqualToDatabind(s, boxed::writeString);
        }
    }

    @Test
    void testEnum() throws IOException {
        for (AnEnum e : JavaData.ENUMS) {
            assertIsEqualToDatabind(e, boxed::writeEnum);
        }
    }
}
