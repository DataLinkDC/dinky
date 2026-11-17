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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Captures SQL Gateway handshake headers for the current upgrade request.
 */
public class SqlGatewayConfigurator extends ServerEndpointConfig.Configurator {

    public static final String HANDSHAKE_HEADERS = "sqlGatewayHandshakeHeaders";

    private static final ThreadLocal<Map<String, List<String>>> HEADER_HOLDER = new ThreadLocal<>();

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        Map<String, List<String>> headers = request.getHeaders();
        if (headers == null) {
            HEADER_HOLDER.set(Collections.emptyMap());
        } else {
            HEADER_HOLDER.set(headers);
        }
    }

    public static Map<String, List<String>> takeHeaders() {
        try {
            Map<String, List<String>> headers = HEADER_HOLDER.get();
            return headers == null ? Collections.emptyMap() : headers;
        } finally {
            HEADER_HOLDER.remove();
        }
    }
}
