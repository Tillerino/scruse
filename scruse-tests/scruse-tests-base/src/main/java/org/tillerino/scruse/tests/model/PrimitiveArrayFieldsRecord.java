package org.tillerino.scruse.tests.model;

import org.tillerino.scruse.tests.JavaData;
import org.tillerino.scruse.tests.function.Zip;

import java.util.ArrayList;
import java.util.List;

public record PrimitiveArrayFieldsRecord(
	boolean[] booleanArray,
	byte[] byteArray,
	char[] charArray,
	short[] shortArray,
	int[] intArray,
	long[] longArray,
	float[] floatArray,
	double[] doubleArray
) {
	public static List<PrimitiveArrayFieldsRecord> INSTANCES = new ArrayList<>();
	static {
		INSTANCES.add(null);
		INSTANCES.addAll(Zip.instantiate8(
			JavaData.BOOLEAN_ARRAYS,
			JavaData.BYTE_ARRAYS,
			JavaData.CHAR_ARRAYS,
			JavaData.SHORT_ARRAYS,
			JavaData.INT_ARRAYS,
			JavaData.LONG_ARRAYS,
			JavaData.FLOAT_ARRAYS,
			JavaData.DOUBLE_ARRAYS,
			PrimitiveArrayFieldsRecord::new
		));
	}
}
