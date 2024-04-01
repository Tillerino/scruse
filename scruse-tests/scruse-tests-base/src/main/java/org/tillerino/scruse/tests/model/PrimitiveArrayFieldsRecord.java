package org.tillerino.scruse.tests.model;

import java.util.ArrayList;
import org.tillerino.scruse.tests.TestSettingsBase;
import org.tillerino.scruse.tests.function.Zip;

public record PrimitiveArrayFieldsRecord(
        boolean[] booleanArray,
        byte[] byteArray,
        char[] charArray,
        short[] shortArray,
        int[] intArray,
        long[] longArray,
        float[] floatArray,
        double[] doubleArray) {

    public static ArrayList<PrimitiveArrayFieldsRecord> instances(TestSettingsBase settings) {
        ArrayList<PrimitiveArrayFieldsRecord> instances = new ArrayList<>();
        instances.add(null);
        instances.addAll(Zip.instantiate8(
                settings.javaData().BOOLEAN_ARRAYS,
                settings.javaData().BYTE_ARRAYS,
                settings.javaData().CHAR_ARRAYS,
                settings.javaData().SHORT_ARRAYS,
                settings.javaData().INT_ARRAYS,
                settings.javaData().LONG_ARRAYS,
                settings.javaData().floatArrays,
                settings.javaData().DOUBLE_ARRAYS,
                PrimitiveArrayFieldsRecord::new));
        return instances;
    }
}
