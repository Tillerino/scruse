package org.tillerino.scruse.tests.base.delegate;

import static org.tillerino.scruse.tests.CodeAssertions.assertThatCode;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.ReferenceTest;

class BoxedScalarsSerdeTest extends ReferenceTest {
    BoxedScalarsWriter boxed = new BoxedScalarsWriterImpl();

    @Test
    void testBoxedBoolean() throws Exception {
        for (Boolean b : SETTINGS.javaData().BOXED_BOOLEANS) {
            outputUtils.roundTrip(
                    b, boxed::writeBoxedBooleanX, boxed::readBoxedBooleanX, new TypeReference<Boolean>() {});
        }
        assertThatCode(BoxedScalarsWriterImpl.class)
                .method("writeBoxedBooleanX")
                .calls("writePrimitiveBooleanX");
        assertThatCode(BoxedScalarsWriterImpl.class).method("readBoxedBooleanX").calls("readPrimitiveBooleanX");
    }

    @Test
    void testBoxedByte() throws Exception {
        for (Byte b : SETTINGS.javaData().BOXED_BYTES) {
            outputUtils.roundTrip(b, boxed::writeBoxedByteX, boxed::readBoxedByteX, new TypeReference<Byte>() {});
        }
        assertThatCode(BoxedScalarsWriterImpl.class).method("writeBoxedByteX").calls("writePrimitiveByteX");
        assertThatCode(BoxedScalarsWriterImpl.class).method("readBoxedByteX").calls("readPrimitiveByteX");
    }

    @Test
    void testBoxedShort() throws Exception {
        for (Short b : SETTINGS.javaData().BOXED_SHORTS) {
            outputUtils.roundTrip(b, boxed::writeBoxedShortX, boxed::readBoxedShortX, new TypeReference<Short>() {});
        }
        assertThatCode(BoxedScalarsWriterImpl.class).method("writeBoxedShortX").calls("writePrimitiveShortX");
        assertThatCode(BoxedScalarsWriterImpl.class).method("readBoxedShortX").calls("readPrimitiveShortX");
    }

    @Test
    void testBoxedInt() throws Exception {
        for (Integer b : SETTINGS.javaData().BOXED_INTS) {
            outputUtils.roundTrip(b, boxed::writeBoxedIntX, boxed::readBoxedIntX, new TypeReference<Integer>() {});
        }
        assertThatCode(BoxedScalarsWriterImpl.class).method("writeBoxedIntX").calls("writePrimitiveIntX");
        assertThatCode(BoxedScalarsWriterImpl.class).method("readBoxedIntX").calls("readPrimitiveIntX");
    }

    @Test
    void testBoxedLong() throws Exception {
        for (Long b : SETTINGS.javaData().BOXED_LONGS) {
            outputUtils.roundTrip(b, boxed::writeBoxedLongX, boxed::readBoxedLongX, new TypeReference<Long>() {});
        }
        assertThatCode(BoxedScalarsWriterImpl.class).method("writeBoxedLongX").calls("writePrimitiveLongX");
        assertThatCode(BoxedScalarsWriterImpl.class).method("readBoxedLongX").calls("readPrimitiveLongX");
    }

    @Test
    void testBoxedChar() throws Exception {
        for (Character b : SETTINGS.javaData().BOXED_CHARS) {
            outputUtils.roundTrip(b, boxed::writeBoxedCharX, boxed::readBoxedCharX, new TypeReference<Character>() {});
        }
        assertThatCode(BoxedScalarsWriterImpl.class).method("writeBoxedCharX").calls("writePrimitiveCharX");
        assertThatCode(BoxedScalarsWriterImpl.class).method("readBoxedCharX").calls("readPrimitiveCharX");
    }

    @Test
    void testBoxedFloat() throws Exception {
        for (Float b : SETTINGS.javaData().boxedFloats) {
            outputUtils.roundTrip(b, boxed::writeBoxedFloatX, boxed::readBoxedFloatX, new TypeReference<Float>() {});
        }
        assertThatCode(BoxedScalarsWriterImpl.class).method("writeBoxedFloatX").calls("writePrimitiveFloatX");
        assertThatCode(BoxedScalarsWriterImpl.class).method("readBoxedFloatX").calls("readPrimitiveFloatX");
    }
}
