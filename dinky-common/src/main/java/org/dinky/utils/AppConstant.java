package org.dinky.utils;

public final class AppConstant {
    public static final String JDK_VERSION = System.getProperty("java.specification.version");
    public static final boolean JDK_ABOVE_1_8 = JDK_VERSION.compareTo("1.8") > 0;

    private AppConstant() {
    }
}
