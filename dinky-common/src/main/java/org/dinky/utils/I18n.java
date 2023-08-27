package org.dinky.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import java.util.Arrays;

import java.util.Locale;

import java.util.ResourceBundle;

public final class I18n {
    private static final Logger logger = LoggerFactory.getLogger(I18n.class);

    private static final String MESSAGES_BASE = "message";
    private static ResourceBundle bundle;

    private I18n() {
    }

    public static Locale getLocale() {
        return Locale.getDefault();
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

        String message = bundle.getString(key);
        if (getLocale() == Locale.SIMPLIFIED_CHINESE) {
            message = new String(message.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }

        return message;
    }

    public static String getMessage(String key, Object... arguments) {
        return MessageFormat.format(getMessage(key), arguments);
    }
}