package org.tillerino.scruse.tests.base;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import org.tillerino.scruse.annotations.JsonInput;
import org.tillerino.scruse.annotations.JsonOutput;

/**
 * All scalar component types are tested in {@link ScalarListsSerde}, we test other collections with the Boolean
 * component type here.
 */
interface ScalarCollectionsReader {
    @JsonOutput
    void writeBooleanSet(Set<Boolean> set, JsonGenerator generator) throws IOException;

    @JsonInput
    Set<Boolean> readBooleanSet(JsonParser parser) throws IOException;

    @JsonOutput
    void writeBooleanTreeSet(TreeSet<Boolean> set, JsonGenerator generator) throws IOException;

    @JsonInput
    TreeSet<Boolean> readBooleanTreeSet(JsonParser parser) throws IOException;
}
