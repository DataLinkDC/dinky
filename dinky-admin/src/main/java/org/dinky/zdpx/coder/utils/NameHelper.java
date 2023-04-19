package org.dinky.zdpx.coder.utils;



import com.google.common.base.CaseFormat;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public final class NameHelper {
    private NameHelper() {}
    private static final AtomicInteger counter = new AtomicInteger();

    public static String generateVariableName(String name) {
        String gn = generateVariableName(name, "");
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, gn);
    }

    public static String generateVariableName(String name, String suffix) {
        return generateVariableName("_", name, suffix);
    }

    public static String generateVariableName(String prefix, String name, String suffix) {
        return prefix + name + counter.incrementAndGet() + suffix;
    }

    public static AtomicInteger getCounter() {
        return counter;
    }
}
