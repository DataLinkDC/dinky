/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.utils;

import org.dinky.data.exception.BusException;

import java.util.Locale;
import java.util.Map;

/**
 * Accepts only well-defined JDBC addresses for drivers that Dinky already supports.
 */
public final class JdbcAddress {

    public static final String NOT_WELL_DEFINED_MESSAGE = "JDBC address is not well-defined";

    /**
     * Supported JDBC prefixes, longest first so the first match is the most specific.
     */
    private static final String[] SUPPORTED_PREFIXES = {
        "jdbc:postgresql:",
        "jdbc:sqlserver:",
        "jdbc:clickhouse:",
        "jdbc:greenplum:",
        "jdbc:phoenix:",
        "jdbc:oracle:",
        "jdbc:pivotal:",
        "jdbc:presto:",
        "jdbc:mysql:",
        "jdbc:hive2:"
    };

    private JdbcAddress() {}

    /**
     * Whether {@code address} is a well-defined JDBC URL for a supported driver.
     */
    public static boolean isWellDefined(String address) {
        if (address == null) {
            return false;
        }
        String trimmed = address.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        String lower = trimmed.toLowerCase(Locale.ROOT);
        for (String prefix : SUPPORTED_PREFIXES) {
            if (lower.startsWith(prefix)) {
                return hasDefinedTarget(prefix, trimmed.substring(prefix.length()));
            }
        }
        return false;
    }

    /**
     * Reject blank, malformed, or unexpected-scheme JDBC addresses.
     */
    public static void requireWellDefined(String address) {
        if (!isWellDefined(address)) {
            throw new BusException(NOT_WELL_DEFINED_MESSAGE);
        }
    }

    /**
     * When a connect config carries a JDBC {@code url}, require that it is well-defined.
     *
     * @return {@code true} if a {@code url} was present and accepted
     */
    public static boolean requireWellDefinedUrlIfPresent(Map<String, ?> connectConfig) {
        if (connectConfig == null || !connectConfig.containsKey("url")) {
            return false;
        }
        Object url = connectConfig.get("url");
        requireWellDefined(url == null ? null : String.valueOf(url));
        return true;
    }

    private static boolean hasDefinedTarget(String prefix, String rest) {
        if (rest.isEmpty()) {
            return false;
        }
        if ("jdbc:oracle:".equals(prefix)) {
            int at = rest.indexOf('@');
            if (at < 1) {
                return false;
            }
            return hasHostToken(stripLeadingSlashes(rest.substring(at + 1)));
        }
        if ("jdbc:phoenix:".equals(prefix)) {
            return hasHostToken(stripLeadingSlashes(rest));
        }
        int slashSlash = rest.indexOf("//");
        if (slashSlash < 0) {
            return false;
        }
        String before = rest.substring(0, slashSlash);
        if (!before.isEmpty() && !before.matches("[A-Za-z0-9._-]+:")) {
            return false;
        }
        return hasHostToken(rest.substring(slashSlash + 2));
    }

    private static String stripLeadingSlashes(String value) {
        int index = 0;
        while (index < value.length() && value.charAt(index) == '/') {
            index++;
        }
        return value.substring(index);
    }

    private static boolean hasHostToken(String target) {
        if (target == null || target.isEmpty()) {
            return false;
        }
        if (target.charAt(0) == '[') {
            int close = target.indexOf(']');
            return close > 1;
        }
        char first = target.charAt(0);
        if (!Character.isLetterOrDigit(first) && first != '_') {
            return false;
        }
        int end = 0;
        while (end < target.length()) {
            char current = target.charAt(end);
            if (current == '/' || current == '?' || current == ';' || current == ':') {
                break;
            }
            end++;
        }
        return end > 0;
    }
}
