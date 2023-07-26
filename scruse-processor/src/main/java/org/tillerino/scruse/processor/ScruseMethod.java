package org.tillerino.scruse.processor;

import org.tillerino.scruse.processor.FullyQualifiedName.FullyQualifiedClassName;

import javax.lang.model.element.ExecutableElement;

public record ScruseMethod(FullyQualifiedClassName className, String name, ExecutableElement methodElement) {
}
