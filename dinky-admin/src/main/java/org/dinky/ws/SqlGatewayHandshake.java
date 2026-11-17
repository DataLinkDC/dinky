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

package org.dinky.ws;

import org.dinky.data.constant.CommonConstant;
import org.dinky.data.model.ClusterInstance;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;

/**
 * Helpers for SQL Gateway handshake session, cluster address, and init SQL handling.
 */
public final class SqlGatewayHandshake {

    public static final String TOKEN_QUERY_PARAM = "token";
    public static final String TOKEN_NAME = "dinky-token";

    private SqlGatewayHandshake() {}

    public static String resolveToken(
            Map<String, List<String>> queryParams, Map<String, List<String>> headers) {
        String token = firstParameter(queryParams, TOKEN_QUERY_PARAM);
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        token = firstParameter(queryParams, TOKEN_NAME);
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        token = firstHeader(headers, TOKEN_NAME);
        if (StrUtil.isNotBlank(token)) {
            return token;
        }
        return readCookie(headers, TOKEN_NAME);
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
        target.add(normalizeAddress(trimmed));
        for (String part : trimmed.split(",")) {
            if (StrUtil.isNotBlank(part)) {
                target.add(normalizeAddress(part.trim()));
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

    public static boolean isKnownClusterAddress(String connectAddress, Collection<String> knownAddresses) {
        if (StrUtil.isBlank(connectAddress) || knownAddresses == null || knownAddresses.isEmpty()) {
            return false;
        }
        String normalized = normalizeAddress(connectAddress.trim());
        if (knownAddresses.contains(normalized)) {
            return true;
        }
        String[] parts = connectAddress.split(",");
        if (parts.length <= 1) {
            return false;
        }
        for (String part : parts) {
            if (StrUtil.isBlank(part) || !knownAddresses.contains(normalizeAddress(part.trim()))) {
                return false;
            }
        }
        return true;
    }

    public static Set<String> findPlaceholderNames(String statement) {
        if (StrUtil.isBlank(statement)) {
            return Collections.emptySet();
        }
        Matcher matcher = CommonConstant.GLOBAL_VARIABLE_PATTERN.matcher(statement);
        Set<String> names = new LinkedHashSet<>();
        while (matcher.find()) {
            names.add(matcher.group(1));
        }
        return names;
    }

    public static String normalizeAddress(String address) {
        return address.trim().toLowerCase(Locale.ROOT);
    }

    private static String firstParameter(Map<String, List<String>> params, String key) {
        if (params == null || StrUtil.isBlank(key)) {
            return "";
        }
        List<String> values = params.get(key);
        if (values == null || values.isEmpty()) {
            return "";
        }
        String value = values.get(0);
        return value == null ? "" : value;
    }

    private static String firstHeader(Map<String, List<String>> headers, String name) {
        List<String> values = headerValues(headers, name);
        if (values.isEmpty() || StrUtil.isBlank(values.get(0))) {
            return "";
        }
        return values.get(0).trim();
    }

    private static String readCookie(Map<String, List<String>> headers, String cookieName) {
        for (String header : headerValues(headers, "Cookie")) {
            if (StrUtil.isBlank(header)) {
                continue;
            }
            for (String part : header.split(";")) {
                String[] pair = part.trim().split("=", 2);
                if (pair.length == 2 && cookieName.equals(pair[0].trim())) {
                    return urlDecode(pair[1].trim());
                }
            }
        }
        return "";
    }

    private static List<String> headerValues(Map<String, List<String>> headers, String name) {
        if (headers == null || StrUtil.isBlank(name)) {
            return Collections.emptyList();
        }
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey() != null && name.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue() == null ? Collections.emptyList() : entry.getValue();
            }
        }
        return Collections.emptyList();
    }

    private static String urlDecode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}
