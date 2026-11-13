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

package org.dinky.alert;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;

/**
 * Accepts only well-defined HTTP or HTTPS webhook addresses for alert senders.
 */
public final class AlertSenderAddress {

    public static final String NOT_WELL_DEFINED_MESSAGE =
            "Alert sender address is not a well-defined HTTP or HTTPS webhook address";

    private AlertSenderAddress() {}

    /**
     * Whether {@code address} is a well-defined HTTP or HTTPS webhook address with a host and no
     * userinfo.
     */
    public static boolean isWellDefined(String address) {
        if (address == null) {
            return false;
        }
        String trimmed = address.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        final URI uri;
        try {
            uri = new URI(trimmed);
        } catch (URISyntaxException e) {
            return false;
        }

        String scheme = uri.getScheme();
        if (scheme == null) {
            return false;
        }
        String lowerScheme = scheme.toLowerCase(Locale.ROOT);
        if (!"http".equals(lowerScheme) && !"https".equals(lowerScheme)) {
            return false;
        }
        if (uri.getUserInfo() != null) {
            return false;
        }

        String host = uri.getHost();
        if (host == null || host.isEmpty()) {
            try {
                host = new URL(trimmed).getHost();
            } catch (MalformedURLException e) {
                return false;
            }
        }
        return host != null && !host.isEmpty();
    }

    /**
     * Reject blank, malformed, or unexpected-scheme addresses before an alert sender opens a
     * connection.
     */
    public static void requireWellDefined(String address) {
        if (!isWellDefined(address)) {
            throw new AlertException(NOT_WELL_DEFINED_MESSAGE);
        }
    }
}
