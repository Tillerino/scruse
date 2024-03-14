package org.tillerino.scruse.tests.model;

import org.tillerino.scruse.tests.JavaData;
import org.tillerino.scruse.tests.function.Zip;

import java.util.ArrayList;
import java.util.List;

public record ReferenceArrayFieldsRecord(
	Boolean[] boxedBooleanArray,
	Byte[] boxedByteArray,
	Character[] boxedCharArray,
	Short[] boxedShortArray,
	Integer[] boxedIntArray,
	Long[] boxedLongArray,
	Float[] boxedFloatArray,
	Double[] boxedDoubleArray,
	String[] stringArray,
	AnEnum[] enumArray
) {
	static List<ReferenceArrayFieldsRecord> INSTANCES = new ArrayList<>();
	static {
		INSTANCES.add(null);
		INSTANCES.addAll(Zip.instantiate10(
			JavaData.BOXED_BOOLEAN_ARRAYS,
			JavaData.BOXED_BYTE_ARRAYS,
			JavaData.BOXED_CHAR_ARRAYS,
			JavaData.BOXED_SHORT_ARRAYS,
			JavaData.BOXED_INT_ARRAYS,
			JavaData.BOXED_LONG_ARRAYS,
			JavaData.BOXED_FLOAT_ARRAYS,
			JavaData.BOXED_DOUBLE_ARRAYS,
			JavaData.STRING_ARRAYS,
			JavaData.ENUM_ARRAYS,
			ReferenceArrayFieldsRecord::new
		));
	}
}
