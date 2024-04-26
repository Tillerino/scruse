package org.tillerino.scruse.tests.model.annotations;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JsonPropertyCustomName(@JsonProperty("notS") String s) {}
