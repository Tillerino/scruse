package org.tillerino.scruse.tests;

import lombok.Builder;
import lombok.With;

@Builder
public record TestSettingsBase(
	boolean canReadIntArrayNatively,
	boolean canReadLongArrayNatively,
	boolean canReadStringArrayNatively,
	boolean canWriteBooleanArrayNatively,

	boolean canWriteShortArrayNatively,
	boolean canWriteIntArrayNatively,
	boolean canWriteLongArrayNatively,
	boolean canWriteFloatArrayNatively,
	boolean canWriteDoubleArrayNatively,

	boolean canWriteStringArrayNatively
) {

}
