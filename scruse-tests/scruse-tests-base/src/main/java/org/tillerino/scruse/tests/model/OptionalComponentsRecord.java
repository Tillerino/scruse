package org.tillerino.scruse.tests.model;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public record OptionalComponentsRecord(
        Optional<String> optionalString,
        OptionalInt optionalInt,
        OptionalLong optionalLong,
        OptionalDouble optionalDouble,
        Optional<Optional<String>> nestedOptionalBecauseWeCan) {}
