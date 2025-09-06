package org.tillerino.jagger.api;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.tillerino.jagger.api.SerializationContext.ClassAndScope;

/**
 * Context for a single deserialization operation. This can be used in a
 * {@link org.tillerino.jagger.annotations.JsonInput} for additional features like polymorphic deserialization.
 *
 * <p>This class can be extended to pass additional state between custom serializer implementations.
 *
 * <p>To reduce heap allocations, you can reuse instances.
 */
public class DeserializationContext {
    /**
     * Marks if the start-object-token has been read for the current object. This is relevant for polymorphic
     * deserialization, where the type of the object is determined before dispatching to the actual deserializer.
     */
    private boolean objectOpen = false;

    private Map<ClassAndScope, ObjectIdResolver> objectIdResolvers;

    public <T extends ObjectIdResolver> Object resolveId(Class<T> type, Class<?> scope, Object key) {
        if (objectIdResolvers == null) {
            return null;
        }
        ObjectIdResolver objectIdResolver = objectIdResolvers.get(new ClassAndScope(type, scope));
        if (objectIdResolver == null) {
            return null;
        }
        return objectIdResolver.resolveId(new IdKey(type, scope, key));
    }

    public <T extends ObjectIdResolver> void bindItem(
            Class<T> type, Class<?> scope, Supplier<T> instantiator, Object key, Object dto) {
        if (objectIdResolvers == null) {
            objectIdResolvers = new LinkedHashMap<>();
        }
        objectIdResolvers
                .computeIfAbsent(new ClassAndScope(type, scope), __ -> instantiator.get())
                .bindItem(new IdKey(type, scope, key), dto);
    }

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
