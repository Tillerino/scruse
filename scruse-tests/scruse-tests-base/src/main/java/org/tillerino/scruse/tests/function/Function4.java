package org.tillerino.scruse.tests.function;

public interface Function4<A, B, C, D, T> {
    T apply(A a, B b, C c, D d);
}
