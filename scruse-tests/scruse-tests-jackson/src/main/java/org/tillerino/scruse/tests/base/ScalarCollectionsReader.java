package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonParser;
import org.tillerino.scruse.annotations.JsonInput;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * All scalar component types are tested in {@link ScalarListsReader}, we test other collections with the Boolean component type here.
 */
interface ScalarCollectionsReader {
	@JsonInput
	Set<Boolean> readBooleanSet(JsonParser parser) throws IOException;

	@JsonInput
	TreeSet<Boolean> readBooleanTreeSet(JsonParser parser) throws IOException;
}
