package org.dinky.context;

import org.dinky.config.Dialect;

import java.util.Optional;

public class TaskContextHolder {
    private static final ThreadLocal<Dialect> DIALECT_THREAD_LOCAL= new InheritableThreadLocal<>();

    public static Dialect getDialect() {
        return Optional.ofNullable(DIALECT_THREAD_LOCAL.get()).orElseThrow(()->new RuntimeException("task dialect is null"));
    }

    public static void setDialect(Dialect dialect) {
        DIALECT_THREAD_LOCAL.set(dialect);
    }
}
