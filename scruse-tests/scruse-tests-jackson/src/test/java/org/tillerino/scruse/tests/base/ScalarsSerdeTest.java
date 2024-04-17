package org.tillerino.scruse.tests.base;

import static org.tillerino.scruse.tests.OutputUtils.roundTrip;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.model.AnEnum;

class ScalarsSerdeTest {
    PrimitiveScalarsSerde primitive = new PrimitiveScalarsSerdeImpl();
    BoxedScalarsSerde boxed = new BoxedScalarsSerdeImpl();

    @Test
    void testBoolean() throws Exception {
        for (boolean b : SETTINGS.javaData().BOOLEANS) {
            roundTrip(
                    b,
                    primitive::writePrimitiveBooleanX,
                    primitive::readPrimitiveBooleanX,
                    new TypeReference<Boolean>() {});
        }
    }

    @Test
    void testByte() throws Exception {
        for (byte b : SETTINGS.javaData().BYTES) {
            roundTrip(b, primitive::writePrimitiveByteX, primitive::readPrimitiveByteX, new TypeReference<Byte>() {});
        }
    }

    @Test
    void testShort() throws Exception {
        for (short s : SETTINGS.javaData().SHORTS) {
            roundTrip(
                    s, primitive::writePrimitiveShortX, primitive::readPrimitiveShortX, new TypeReference<Short>() {});
        }
    }

    @Test
    void testInt() throws Exception {
        for (int i : SETTINGS.javaData().INTS) {
            roundTrip(i, primitive::writePrimitiveIntX, primitive::readPrimitiveIntX, new TypeReference<Integer>() {});
        }
    }

    @Test
    void testLong() throws Exception {
        for (long l : SETTINGS.javaData().LONGS) {
            roundTrip(l, primitive::writePrimitiveLongX, primitive::readPrimitiveLongX, new TypeReference<Long>() {});
        }
    }

    @Test
    void testChar() throws Exception {
        for (char c : SETTINGS.javaData().CHARS) {
            roundTrip(
                    c,
                    primitive::writePrimitiveCharX,
                    primitive::readPrimitiveCharX,
                    new TypeReference<Character>() {});
        }
    }

    @Test
    void testFloat() throws Exception {
        for (float f : SETTINGS.javaData().floats) {
            roundTrip(
                    f, primitive::writePrimitiveFloatX, primitive::readPrimitiveFloatX, new TypeReference<Float>() {});
        }
    }

    @Test
    void testDouble() throws Exception {
        for (double d : SETTINGS.javaData().DOUBLES) {
            roundTrip(
                    d,
                    primitive::writePrimitiveDoubleX,
                    primitive::readPrimitiveDoubleX,
                    new TypeReference<Double>() {});
        }
    }

    @Test
    void testBoxedBoolean() throws Exception {
        for (Boolean b : SETTINGS.javaData().BOXED_BOOLEANS) {
            roundTrip(b, boxed::writeBoxedBoolean, boxed::readBoxedBoolean, new TypeReference<Boolean>() {});
        }
    }

    @Test
    void testBoxedByte() throws Exception {
        for (Byte b : SETTINGS.javaData().BOXED_BYTES) {
            roundTrip(b, boxed::writeBoxedByte, boxed::readBoxedByte, new TypeReference<Byte>() {});
        }
    }

    @Test
    void testBoxedShort() throws Exception {
        for (Short s : SETTINGS.javaData().BOXED_SHORTS) {
            roundTrip(s, boxed::writeBoxedShort, boxed::readBoxedShort, new TypeReference<Short>() {});
        }
    }

    @Test
    void testBoxedInteger() throws Exception {
        for (Integer i : SETTINGS.javaData().BOXED_INTS) {
            roundTrip(i, boxed::writeBoxedInt, boxed::readBoxedInt, new TypeReference<Integer>() {});
        }
    }

    @Test
    void testBoxedLong() throws Exception {
        for (Long l : SETTINGS.javaData().BOXED_LONGS) {
            roundTrip(l, boxed::writeBoxedLong, boxed::readBoxedLong, new TypeReference<Long>() {});
        }
    }

    @Test
    void testBoxedChar() throws Exception {
        for (Character c : SETTINGS.javaData().BOXED_CHARS) {
            roundTrip(c, boxed::writeBoxedChar, boxed::readBoxedChar, new TypeReference<Character>() {});
        }
    }

    @Test
    void testBoxedFloat() throws Exception {
        for (Float f : SETTINGS.javaData().boxedFloats) {
            roundTrip(f, boxed::writeBoxedFloat, boxed::readBoxedFloat, new TypeReference<Float>() {});
        }
    }

    @Test
    void testBoxedDouble() throws Exception {
        for (Double d : SETTINGS.javaData().BOXED_DOUBLES) {
            roundTrip(d, boxed::writeBoxedDouble, boxed::readBoxedDouble, new TypeReference<Double>() {});
        }
    }

    @Test
    void testString() throws Exception {
        for (String s : SETTINGS.javaData().STRINGS) {
            roundTrip(s, boxed::writeString, boxed::readString, new TypeReference<String>() {});
        }
    }

    @Test
    void testEnum() throws Exception {
        for (AnEnum e : SETTINGS.javaData().ENUMS) {
            roundTrip(e, boxed::writeEnum, boxed::readEnum, new TypeReference<AnEnum>() {});
        }
    }
}
