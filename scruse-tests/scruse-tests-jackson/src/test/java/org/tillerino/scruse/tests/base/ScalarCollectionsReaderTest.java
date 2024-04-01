package org.tillerino.scruse.tests.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.tillerino.scruse.tests.TestSettings.SETTINGS;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.tillerino.scruse.tests.OutputUtils;

class ScalarCollectionsReaderTest {
    ScalarCollectionsReader impl = new ScalarCollectionsReaderImpl();

    @Test
    void testBooleanSet() throws IOException {
        TypeReference<Set<Boolean>> typeRef = new TypeReference<>() {};
        for (List<Boolean> booleanList : SETTINGS.javaData().BOOLEAN_LISTS) {
            Set<Boolean> asSet = booleanList != null ? new LinkedHashSet<>(booleanList) : null;
            Set<Boolean> set = OutputUtils.roundTrip(asSet, impl::writeBooleanSet, impl::readBooleanSet, typeRef);
            if (set != null) {
                assertThat(set).isInstanceOf(LinkedHashSet.class);
            }
        }
    }

    @Test
    void testBooleanTreeSet() throws IOException {
        TypeReference<TreeSet<Boolean>> typeRef = new TypeReference<>() {};
        for (List<Boolean> booleanList : SETTINGS.javaData().BOOLEAN_LISTS) {
            TreeSet<Boolean> asSet = booleanList != null
                    ? booleanList.stream().filter(Objects::nonNull).collect(Collectors.toCollection(TreeSet::new))
                    : null;
            TreeSet<Boolean> set =
                    OutputUtils.roundTrip(asSet, impl::writeBooleanTreeSet, impl::readBooleanTreeSet, typeRef);
            if (set != null) {
                assertThat(set).isInstanceOf(TreeSet.class);
            }
        }
    }
}
