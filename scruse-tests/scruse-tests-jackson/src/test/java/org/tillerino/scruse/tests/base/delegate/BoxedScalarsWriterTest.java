package org.tillerino.scruse.tests.base.delegate;

import static org.tillerino.scruse.tests.CodeAssertions.assertThatCode;
import static org.tillerino.scruse.tests.OutputUtils.assertIsEqualToDatabind;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

import org.junit.jupiter.api.Test;

class BoxedScalarsWriterTest {
    BoxedScalarsWriter boxed = new BoxedScalarsWriterImpl();

    @Test
    void testBoxedBoolean() throws Exception {
        for (Boolean b : SETTINGS.javaData().BOXED_BOOLEANS) {
            assertIsEqualToDatabind(b, boxed::writeBoxedBooleanX);
        }
        assertThatCode(BoxedScalarsWriterImpl.class)
                .method("writeBoxedBooleanX")
                .calls("writePrimitiveBooleanX");
    }

    @Test
    void testBoxedByte() throws Exception {
        for (Byte b : SETTINGS.javaData().BOXED_BYTES) {
            assertIsEqualToDatabind(b, boxed::writeBoxedByteX);
        }
        assertThatCode(BoxedScalarsWriterImpl.class).method("writeBoxedByteX").calls("writePrimitiveByteX");
    }

    @Test
    void testBoxedShort() throws Exception {
        for (Short b : SETTINGS.javaData().BOXED_SHORTS) {
            assertIsEqualToDatabind(b, boxed::writeBoxedShortX);
        }
        assertThatCode(BoxedScalarsWriterImpl.class).method("writeBoxedShortX").calls("writePrimitiveShortX");
    }

    @Test
    void testBoxedInt() throws Exception {
        for (Integer b : SETTINGS.javaData().BOXED_INTS) {
            assertIsEqualToDatabind(b, boxed::writeBoxedIntX);
        }
        assertThatCode(BoxedScalarsWriterImpl.class).method("writeBoxedIntX").calls("writePrimitiveIntX");
    }

    @Test
    void testBoxedLong() throws Exception {
        for (Long b : SETTINGS.javaData().BOXED_LONGS) {
            assertIsEqualToDatabind(b, boxed::writeBoxedLongX);
        }
        assertThatCode(BoxedScalarsWriterImpl.class).method("writeBoxedLongX").calls("writePrimitiveLongX");
    }

    @Test
    void testBoxedChar() throws Exception {
        for (Character b : SETTINGS.javaData().BOXED_CHARS) {
            assertIsEqualToDatabind(b, boxed::writeBoxedCharX);
        }
        assertThatCode(BoxedScalarsWriterImpl.class).method("writeBoxedCharX").calls("writePrimitiveCharX");
    }

    @Test
    void testBoxedFloat() throws Exception {
        for (Float b : SETTINGS.javaData().boxedFloats) {
            assertIsEqualToDatabind(b, boxed::writeBoxedFloatX);
        }
        assertThatCode(BoxedScalarsWriterImpl.class).method("writeBoxedFloatX").calls("writePrimitiveFloatX");
    }
}
