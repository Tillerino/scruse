package org.tillerino.scruse.api;

import org.tillerino.scruse.input.EmptyArrays;

/**
 * Context for a single deserialization operation.
 * This can be used in a {@link org.tillerino.scruse.annotations.JsonInput} for additional features
 * like polymorphic deserialization.
 */
public class DeserializationContext {
    /**
     * Marks if the start-object-token has been read for the current object.
     * This is relevant for polymorphic deserialization, where the type of the object is determined
     * before dispatching to the actual deserializer.
     */
    private boolean objectOpen = false;

    public boolean isObjectOpen(boolean clear) {
        boolean result = objectOpen;
        if (clear) {
            objectOpen = false;
        }
        return result;
    }

    public void markObjectOpen() {
        this.objectOpen = true;
    }
}
