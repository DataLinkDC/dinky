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

package org.dinky.gateway;

import java.net.InetSocketAddress;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SqlClientOptions {
    private SqlCliMode mode;
    private String sessionId;
    private String connectAddress;
    private String initSql;
    private String historyFilePath;
    private TerminalSize terminalSize;

    public Optional<InetSocketAddress> buildConnectAddress() {
        if (connectAddress == null) {
            return Optional.empty();
        }
        String[] hosts = connectAddress.split(",");
        if (hosts.length > 0) {
            String[] hostPort = hosts[0].split(":");
            if (hostPort.length == 2) {
                return Optional.of(new InetSocketAddress(hostPort[0], Integer.parseInt(hostPort[1])));
            }
        }
        throw new IllegalArgumentException("Invalid connect address: " + connectAddress);
    }

    @Data
    @AllArgsConstructor
    public static class TerminalSize {
        private int columns;
        private int rows;
    }
}
