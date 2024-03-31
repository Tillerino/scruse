package org.tillerino.scruse.api;

/**
 * Context for a single serialization operation. This can be used in a
 * {@link org.tillerino.scruse.annotations.JsonOutput} for additional features like polymorphic deserialization.
 *
 * <p>This class can be extended to pass additional state between custom serializer implementations.
 *
 * <p>To reduce heap allocations, you can reuse instances.
 */
public class SerializationContext {
    /**
     * Marks if the object being written is a child class of a polymorphic type and needs to have its discriminator
     * written.
     */
    public String pendingDiscriminatorProperty = null;
    /** Only relevant when {@link #pendingDiscriminatorProperty} is not null. */
    public String pendingDiscriminatorValue = null;

    public void setPendingDiscriminator(String property, String value) {
        pendingDiscriminatorProperty = property;
        pendingDiscriminatorValue = value;
    }

    public boolean isDiscriminatorPending() {
        return pendingDiscriminatorProperty != null;
    }
}
