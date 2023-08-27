package org.dinky.utils;

import java.text.MessageFormat;

import java.util.Arrays;

import java.util.Locale;

import java.util.ResourceBundle;

public final class I18n {

    private final static String MESSAGES_BASE = "message";
    private static ResourceBundle bundle;

    private I18n() {
    }

    public static Locale getLocale() {
        Locale defaultLocale = Locale.getDefault();
        return defaultLocale;
    }

    public static boolean isSupported(Locale l) {
        Locale[] availableLocales = Locale.getAvailableLocales();
        return Arrays.asList(availableLocales).contains(l);
    }

    public static void setLocale(Locale l) {
        Locale.setDefault(l);
    }

    public static String getMessage(String key) {
        if(bundle == null) {
            bundle = ResourceBundle.getBundle(MESSAGES_BASE);
        }
        return bundle.getString(key);
    }

    public static String getMessage(String key, Object ... arguments) {
        return MessageFormat.format(getMessage(key), arguments);
    }
}