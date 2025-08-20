package org.tillerino.scruse.processor.util;

import java.util.function.Supplier;
import org.apache.commons.lang3.exception.ContextedRuntimeException;

public class Exceptions {
    public static <T> T getWithContext(Supplier<T> supplier, String label, Object value) {
        try {
            return supplier.get();
        } catch (ContextedRuntimeException e) {
            throw e.addContextValue(label, value);
        }
    }

    public static void runWithContext(Runnable runnable, String label, Object value) {
        try {
            runnable.run();
        } catch (ContextedRuntimeException e) {
            throw e.addContextValue(label, value);
        }
    }
}
