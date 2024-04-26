package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record JsonIgnoreOnRecordComponent(@JsonIgnore String s) {}
