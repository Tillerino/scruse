package org.tillerino.scruse.tests.base.features;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.tillerino.scruse.tests.CodeAssertions.assertThatCode;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.CodeAssertions;
import org.tillerino.scruse.tests.ReferenceTest;
import org.tillerino.scruse.tests.SerdeUtil;
import org.tillerino.scruse.tests.base.features.DelegationSerde.AdditionalArgumentsSerde;
import org.tillerino.scruse.tests.base.features.DelegationSerde.SelfReferencingSerde;
import org.tillerino.scruse.tests.model.features.DelegationModel.SelfReferencingRecord;

public class DelegationTest {

    @Nested
    class SelfReferencingSerdeTest extends ReferenceTest {
        SelfReferencingSerde serde = SerdeUtil.impl(SelfReferencingSerde.class);

        @Test
        void selfReferencingSerialization() throws Exception {
            for (SelfReferencingRecord instance : SelfReferencingRecord.INSTANCES) {
                outputUtils.roundTrip(instance, serde::serialize, serde::deserialize, new TypeReference<>() {});
            }
        }
    }

    @Nested
    class AdditionalArgumentsTest extends ReferenceTest {
        AdditionalArgumentsSerde serde = SerdeUtil.impl(AdditionalArgumentsSerde.class);

        @Test
        void output() throws Exception {
            ArrayList<Integer> l = new ArrayList<>();
            String json = outputUtils.serialize2(Map.of("a", 1), l, serde::writeStringIntMap);
            assertThatJson(json).isEqualTo("{\"a\":\"\"}");
            assertThat(l).containsExactly(1);
        }

        @Test
        void input() throws Exception {
            Queue<Integer> l = new LinkedList<>(List.of(1));
            Map<String, Integer> map = inputUtils.deserialize2("{\"a\":\"\"}", l, serde::readStringIntMap);
            assertThat(map).containsExactly(Map.entry("a", 1));
            assertThat(l).isEmpty();
        }
    }

    @Nested
    class BoxedScalarsSerdeTest extends ReferenceTest {
        DelegationSerde.BoxedScalarsWriter boxed = new DelegationSerde$BoxedScalarsWriterImpl();

        @Test
        void testBoxedBoolean() throws Exception {
            for (Boolean b : SETTINGS.javaData().BOXED_BOOLEANS) {
                outputUtils.roundTrip(
                        b, boxed::writeBoxedBooleanX, boxed::readBoxedBooleanX, new TypeReference<Boolean>() {});
            }
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("writeBoxedBooleanX")
                    .calls("writePrimitiveBooleanX");
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("readBoxedBooleanX")
                    .calls("readPrimitiveBooleanX");
        }

        @Test
        void testBoxedByte() throws Exception {
            for (Byte b : SETTINGS.javaData().BOXED_BYTES) {
                outputUtils.roundTrip(b, boxed::writeBoxedByteX, boxed::readBoxedByteX, new TypeReference<Byte>() {});
            }
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("writeBoxedByteX")
                    .calls("writePrimitiveByteX");
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("readBoxedByteX")
                    .calls("readPrimitiveByteX");
        }

        @Test
        void testBoxedShort() throws Exception {
            for (Short b : SETTINGS.javaData().BOXED_SHORTS) {
                outputUtils.roundTrip(
                        b, boxed::writeBoxedShortX, boxed::readBoxedShortX, new TypeReference<Short>() {});
            }
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("writeBoxedShortX")
                    .calls("writePrimitiveShortX");
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("readBoxedShortX")
                    .calls("readPrimitiveShortX");
        }

        @Test
        void testBoxedInt() throws Exception {
            for (Integer b : SETTINGS.javaData().BOXED_INTS) {
                outputUtils.roundTrip(b, boxed::writeBoxedIntX, boxed::readBoxedIntX, new TypeReference<Integer>() {});
            }
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("writeBoxedIntX")
                    .calls("writePrimitiveIntX");
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("readBoxedIntX")
                    .calls("readPrimitiveIntX");
        }

        @Test
        void testBoxedLong() throws Exception {
            for (Long b : SETTINGS.javaData().BOXED_LONGS) {
                outputUtils.roundTrip(b, boxed::writeBoxedLongX, boxed::readBoxedLongX, new TypeReference<Long>() {});
            }
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("writeBoxedLongX")
                    .calls("writePrimitiveLongX");
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("readBoxedLongX")
                    .calls("readPrimitiveLongX");
        }

        @Test
        void testBoxedChar() throws Exception {
            for (Character b : SETTINGS.javaData().BOXED_CHARS) {
                outputUtils.roundTrip(
                        b, boxed::writeBoxedCharX, boxed::readBoxedCharX, new TypeReference<Character>() {});
            }
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("writeBoxedCharX")
                    .calls("writePrimitiveCharX");
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("readBoxedCharX")
                    .calls("readPrimitiveCharX");
        }

        @Test
        void testBoxedFloat() throws Exception {
            for (Float b : SETTINGS.javaData().boxedFloats) {
                outputUtils.roundTrip(
                        b, boxed::writeBoxedFloatX, boxed::readBoxedFloatX, new TypeReference<Float>() {});
            }
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("writeBoxedFloatX")
                    .calls("writePrimitiveFloatX");
            assertThatCode(DelegationSerde$BoxedScalarsWriterImpl.class)
                    .method("readBoxedFloatX")
                    .calls("readPrimitiveFloatX");
        }
    }

    @Nested
    class PrimitiveArrayFieldsRecordTest extends ReferenceTest {

        @Test
        void delegationWorks() throws Exception {
            assertThatCode(DelegationSerde$PrimitiveArrayFieldsRecordSerdeImpl.class)
                    .method("writePrimitiveArrayFieldsRecord")
                    .calls("writeBooleanArrayX")
                    .calls("writeByteArrayX")
                    .calls("writeCharArrayX")
                    .calls("writeShortArrayX")
                    .calls("writeIntArrayX")
                    .calls("writeLongArrayX")
                    .calls("writeFloatArrayX")
                    .calls("writeDoubleArrayX");
        }
    }

    @Nested
    class ScalarArraysSerdeTest extends ReferenceTest {
        DelegationSerde.ScalarArraysWriter impl = new DelegationSerde$ScalarArraysWriterImpl();

        @Test
        void testBooleanArray() throws Exception {
            for (boolean[] object : SETTINGS.javaData().BOOLEAN_ARRAYS) {
                outputUtils.roundTrip(
                        object, impl::writeBooleanArrayX, impl::readBooleanArrayX, new TypeReference<boolean[]>() {});
            }
            assertThatCalls("writeBooleanArrayX", "writePrimitiveBooleanX", !SETTINGS.canWriteBooleanArrayNatively());
            assertThatCalls("readBooleanArrayX", "readPrimitiveBooleanX", true);
        }

        @Test
        void testByteArray() throws Exception {
            for (byte[] object : SETTINGS.javaData().BYTE_ARRAYS) {
                outputUtils.roundTrip(
                        object, impl::writeByteArrayX, impl::readByteArrayX, new TypeReference<byte[]>() {});
            }
            assertThatCalls("writeByteArrayX", "writePrimitiveByteX", false);
            assertThatCalls("readByteArrayX", "readPrimitiveByteX", true);
        }

        @Test
        void testShortArray() throws Exception {
            for (short[] object : SETTINGS.javaData().SHORT_ARRAYS) {
                outputUtils.roundTrip(
                        object, impl::writeShortArrayX, impl::readShortArrayX, new TypeReference<short[]>() {});
            }
            assertThatCalls("writeShortArrayX", "writePrimitiveShortX", !SETTINGS.canWriteShortArrayNatively());
            assertThatCalls("readShortArrayX", "readPrimitiveShortX", true);
        }

        @Test
        void testIntArray() throws Exception {
            for (int[] object : SETTINGS.javaData().INT_ARRAYS) {
                outputUtils.roundTrip(object, impl::writeIntArrayX, impl::readIntArrayX, new TypeReference<int[]>() {});
            }
            assertThatCalls("writeIntArrayX", "writePrimitiveIntX", !SETTINGS.canWriteIntArrayNatively());
            assertThatCalls("readIntArrayX", "readPrimitiveIntX", !SETTINGS.canReadIntArrayNatively());
        }

        @Test
        void testLongArray() throws Exception {
            for (long[] object : SETTINGS.javaData().LONG_ARRAYS) {
                outputUtils.roundTrip(
                        object, impl::writeLongArrayX, impl::readLongArrayX, new TypeReference<long[]>() {});
            }
            assertThatCalls("writeLongArrayX", "writePrimitiveLongX", !SETTINGS.canWriteLongArrayNatively());
            assertThatCalls("readLongArrayX", "readPrimitiveLongX", !SETTINGS.canReadLongArrayNatively());
        }

        @Test
        void testFloatArray() throws Exception {
            for (float[] object : SETTINGS.javaData().floatArrays) {
                outputUtils.roundTrip(
                        object, impl::writeFloatArrayX, impl::readFloatArrayX, new TypeReference<float[]>() {});
            }
            assertThatCalls("writeFloatArrayX", "writePrimitiveFloatX", !SETTINGS.canWriteFloatArrayNatively());
            assertThatCalls("readFloatArrayX", "readPrimitiveFloatX", true);
        }

        @Test
        void testDoubleArray() throws Exception {
            for (double[] object : SETTINGS.javaData().DOUBLE_ARRAYS) {
                outputUtils.roundTrip(
                        object, impl::writeDoubleArrayX, impl::readDoubleArrayX, new TypeReference<double[]>() {});
            }
            assertThatCalls("writeDoubleArrayX", "writePrimitiveDoubleX", !SETTINGS.canWriteDoubleArrayNatively());
            assertThatCalls("readDoubleArrayX", "readPrimitiveDoubleX", true);
        }

        @Test
        void testBoxedBooleanArray() throws Exception {
            for (Boolean[] object : SETTINGS.javaData().BOXED_BOOLEAN_ARRAYS) {
                outputUtils.roundTrip(
                        object,
                        impl::writeBoxedBooleanArrayX,
                        impl::readBoxedBooleanArrayX,
                        new TypeReference<Boolean[]>() {});
            }
            assertThatCalls("writeBoxedBooleanArrayX", "writeBoxedBooleanX", true);
            assertThatCalls("readBoxedBooleanArrayX", "readBoxedBooleanX", true);
        }

        @Test
        void testBoxedByteArray() throws Exception {
            for (Byte[] object : SETTINGS.javaData().BOXED_BYTE_ARRAYS) {
                outputUtils.roundTrip(
                        object, impl::writeBoxedByteArrayX, impl::readBoxedByteArrayX, new TypeReference<Byte[]>() {});
            }
            assertThatCalls("writeBoxedByteArrayX", "writeBoxedByteX", true);
            assertThatCalls("readBoxedByteArrayX", "readBoxedByteX", true);
        }

        @Test
        void testBoxedShortArray() throws Exception {
            for (Short[] object : SETTINGS.javaData().BOXED_SHORT_ARRAYS) {
                outputUtils.roundTrip(
                        object,
                        impl::writeBoxedShortArrayX,
                        impl::readBoxedShortArrayX,
                        new TypeReference<Short[]>() {});
            }
            assertThatCalls("writeBoxedShortArrayX", "writeBoxedShortX", true);
            assertThatCalls("readBoxedShortArrayX", "readBoxedShortX", true);
        }

        @Test
        void testBoxedIntArray() throws Exception {
            for (Integer[] object : SETTINGS.javaData().BOXED_INT_ARRAYS) {
                outputUtils.roundTrip(
                        object, impl::writeBoxedIntArrayX, impl::readBoxedIntArrayX, new TypeReference<Integer[]>() {});
            }
            assertThatCalls("writeBoxedIntArrayX", "writeBoxedIntX", true);
            assertThatCalls("readBoxedIntArrayX", "readBoxedIntX", true);
        }

        @Test
        void testBoxedLongArray() throws Exception {
            for (Long[] object : SETTINGS.javaData().BOXED_LONG_ARRAYS) {
                outputUtils.roundTrip(
                        object, impl::writeBoxedLongArrayX, impl::readBoxedLongArrayX, new TypeReference<Long[]>() {});
            }
            assertThatCalls("writeBoxedLongArrayX", "writeBoxedLongX", true);
            assertThatCalls("readBoxedLongArrayX", "readBoxedLongX", true);
        }

        @Test
        void testBoxedFloatArray() throws Exception {
            for (Float[] object : SETTINGS.javaData().boxedFloatArrays) {
                outputUtils.roundTrip(
                        object,
                        impl::writeBoxedFloatArrayX,
                        impl::readBoxedFloatArrayX,
                        new TypeReference<Float[]>() {});
            }
            assertThatCalls("writeBoxedFloatArrayX", "writeBoxedFloatX", true);
            assertThatCalls("readBoxedFloatArrayX", "readBoxedFloatX", true);
        }

        @Test
        void testBoxedDoubleArray() throws Exception {
            for (Double[] object : SETTINGS.javaData().BOXED_DOUBLE_ARRAYS) {
                outputUtils.roundTrip(
                        object,
                        impl::writeBoxedDoubleArrayX,
                        impl::readBoxedDoubleArrayX,
                        new TypeReference<Double[]>() {});
            }
            assertThatCalls("writeBoxedDoubleArrayX", "writeBoxedDoubleX", true);
            assertThatCalls("readBoxedDoubleArrayX", "readBoxedDoubleX", true);
        }

        @Test
        void testStringArray() throws Exception {
            for (String[] object : SETTINGS.javaData().STRING_ARRAYS) {
                outputUtils.roundTrip(
                        object, impl::writeStringArrayX, impl::readStringArrayX, new TypeReference<String[]>() {});
            }
            assertThatCalls("writeStringArrayX", "writeStringX", !SETTINGS.canWriteStringArrayNatively());
            assertThatCalls("readStringArrayX", "readStringX", !SETTINGS.canReadStringArrayNatively());
        }

        private static void assertThatCalls(String caller, String callee, boolean doesCall) throws Exception {
            CodeAssertions.MethodAssert method =
                    assertThatCode(DelegationSerde$ScalarArraysWriterImpl.class).method(caller);
            if (doesCall) {
                method.calls(callee);
            } else {
                method.doesNotCall(callee);
            }
        }
    }
}
