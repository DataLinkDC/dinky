package com.dlink.context;

/**
 * request context
 */
public class RequestContext {
    private static final ThreadLocal<Object> threadLocal = new ThreadLocal<>();

    public static void set(Object value) {
        threadLocal.set(value);
    }

    public static Object get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}