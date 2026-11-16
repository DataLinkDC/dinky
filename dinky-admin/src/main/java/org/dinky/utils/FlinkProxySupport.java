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

import org.dinky.data.model.ClusterInstance;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * Helpers for Flink proxy login session and known cluster address handling.
 */
public final class FlinkProxySupport {

    public static final String TOKEN_NAME = "dinky-token";
    public static final String TOKEN_QUERY_PARAM = "token";
    public static final String AUTHORITY_PARAM = "_authority";

    public enum AddressCheck {
        OK,
        MALFORMED,
        UNKNOWN
    }

    private FlinkProxySupport() {}

    public static String resolveToken(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String token = firstNonBlank(request.getParameter(TOKEN_QUERY_PARAM), request.getParameter(TOKEN_NAME));
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        token = request.getHeader(TOKEN_NAME);
        if (StrUtil.isNotBlank(token)) {
            return token.trim();
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return "";
        }
        for (Cookie cookie : cookies) {
            if (cookie != null && TOKEN_NAME.equals(cookie.getName()) && StrUtil.isNotBlank(cookie.getValue())) {
                return cookie.getValue();
            }
        }
        return "";
    }

    public static boolean hasValidLoginSession(HttpServletRequest request) {
        try {
            if (StpUtil.isLogin()) {
                return true;
            }
        } catch (Exception ignored) {
            // fall through to the request token
        }
        return hasValidLoginSession(resolveToken(request));
    }

    public static boolean hasValidLoginSession(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        try {
            return ObjectUtil.isNotEmpty(StpUtil.getLoginIdByToken(token));
        } catch (Exception e) {
            return false;
        }
    }

    public static void collectClusterAddress(Collection<String> target, String raw) {
        if (target == null || StrUtil.isBlank(raw)) {
            return;
        }
        String trimmed = raw.trim();
        target.add(trimmed.toLowerCase(Locale.ROOT));
        String normalizedWhole = normalizeAddress(trimmed);
        if (StrUtil.isNotBlank(normalizedWhole)) {
            target.add(normalizedWhole);
        }
        for (String part : trimmed.split(",")) {
            if (StrUtil.isBlank(part)) {
                continue;
            }
            target.add(part.trim().toLowerCase(Locale.ROOT));
            String normalized = normalizeAddress(part);
            if (StrUtil.isNotBlank(normalized)) {
                target.add(normalized);
            }
        }
    }

    public static List<String> collectClusterAddresses(Collection<ClusterInstance> clusters) {
        List<String> addresses = new ArrayList<>();
        if (clusters == null) {
            return addresses;
        }
        for (ClusterInstance cluster : clusters) {
            if (cluster == null) {
                continue;
            }
            collectClusterAddress(addresses, cluster.getHosts());
            collectClusterAddress(addresses, cluster.getJobManagerHost());
        }
        return addresses;
    }

    public static AddressCheck checkAddress(String address, Collection<String> knownAddresses) {
        if (StrUtil.isBlank(address)) {
            return AddressCheck.MALFORMED;
        }
        String trimmed = address.trim();
        if (trimmed.contains(",")) {
            String[] parts = trimmed.split(",");
            if (parts.length <= 1) {
                return AddressCheck.MALFORMED;
            }
            for (String part : parts) {
                AddressCheck partCheck = checkSingleAddress(part, knownAddresses);
                if (partCheck != AddressCheck.OK) {
                    return partCheck;
                }
            }
            return AddressCheck.OK;
        }
        return checkSingleAddress(trimmed, knownAddresses);
    }

    public static boolean isKnownClusterAddress(String address, Collection<String> knownAddresses) {
        return checkAddress(address, knownAddresses) == AddressCheck.OK;
    }

    public static String normalizeAddress(String address) {
        if (StrUtil.isBlank(address)) {
            return "";
        }
        String trimmed = address.trim();
        String lower = trimmed.toLowerCase(Locale.ROOT);
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            try {
                URI uri = new URI(trimmed);
                if (uri.getHost() == null || uri.getUserInfo() != null) {
                    return "";
                }
                if (uri.getPort() >= 0) {
                    return (uri.getHost() + ":" + uri.getPort()).toLowerCase(Locale.ROOT);
                }
                return uri.getHost().toLowerCase(Locale.ROOT);
            } catch (URISyntaxException e) {
                return "";
            }
        }

        int cut = indexOfFirst(trimmed, "/?#");
        if (cut >= 0) {
            trimmed = trimmed.substring(0, cut);
        }
        trimmed = trimmed.trim();
        if (trimmed.isEmpty() || containsAny(trimmed, "@\\ $")) {
            return "";
        }

        if (trimmed.startsWith("[")) {
            int close = trimmed.indexOf(']');
            if (close <= 1) {
                return "";
            }
            String host = trimmed.substring(0, close + 1);
            String rest = trimmed.substring(close + 1);
            if (rest.isEmpty()) {
                return host.toLowerCase(Locale.ROOT);
            }
            if (!rest.startsWith(":") || !isValidPort(rest.substring(1))) {
                return "";
            }
            return (host + rest).toLowerCase(Locale.ROOT);
        }

        int colon = trimmed.lastIndexOf(':');
        if (colon >= 0) {
            if (trimmed.indexOf(':') != colon) {
                return "";
            }
            String host = trimmed.substring(0, colon);
            String port = trimmed.substring(colon + 1);
            if (host.isEmpty() || !isValidPort(port)) {
                return "";
            }
            return (host + ":" + port).toLowerCase(Locale.ROOT);
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }

    public static String extractTargetAddress(String path) {
        if (StrUtil.isBlank(path)) {
            return "";
        }
        String value = path.trim();
        String lower = value.toLowerCase(Locale.ROOT);
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            return normalizeAddress(value);
        }
        int slash = value.indexOf('/');
        String address = slash >= 0 ? value.substring(0, slash) : value;
        int query = address.indexOf('?');
        if (query >= 0) {
            address = address.substring(0, query);
        }
        return address;
    }

    private static AddressCheck checkSingleAddress(String address, Collection<String> knownAddresses) {
        if (StrUtil.isBlank(address)) {
            return AddressCheck.MALFORMED;
        }
        String normalized = normalizeAddress(address);
        if (StrUtil.isBlank(normalized)) {
            return AddressCheck.MALFORMED;
        }
        if (knownAddresses == null || knownAddresses.isEmpty()) {
            return AddressCheck.UNKNOWN;
        }
        String lowered = address.trim().toLowerCase(Locale.ROOT);
        if (knownAddresses.contains(normalized) || knownAddresses.contains(lowered)) {
            return AddressCheck.OK;
        }
        return AddressCheck.UNKNOWN;
    }

    private static boolean isValidPort(String port) {
        if (StrUtil.isBlank(port) || port.length() > 5) {
            return false;
        }
        int value = 0;
        for (int i = 0; i < port.length(); i++) {
            char ch = port.charAt(i);
            if (ch < '0' || ch > '9') {
                return false;
            }
            value = value * 10 + (ch - '0');
        }
        return value >= 1 && value <= 65535;
    }

    private static String firstNonBlank(String first, String second) {
        if (StrUtil.isNotBlank(first)) {
            return first;
        }
        return StrUtil.isNotBlank(second) ? second : "";
    }

    private static int indexOfFirst(String value, String chars) {
        int found = -1;
        for (int i = 0; i < chars.length(); i++) {
            int index = value.indexOf(chars.charAt(i));
            if (index >= 0 && (found < 0 || index < found)) {
                found = index;
            }
        }
        return found;
    }

    private static boolean containsAny(String value, String chars) {
        for (int i = 0; i < chars.length(); i++) {
            if (value.indexOf(chars.charAt(i)) >= 0) {
                return true;
            }
        }
        return false;
    }
}
