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

package org.dinky.gateway.sqlgateway.cli;

import org.dinky.gateway.FlinkSqlClient;
import org.dinky.gateway.ISqlClient;
import org.dinky.gateway.SqlClientOptions;
import org.dinky.utils.CloseUtil;
import org.dinky.utils.JsonUtils;

import org.apache.flink.client.deployment.executors.RemoteExecutor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.GlobalConfiguration;
import org.apache.flink.configuration.RestOptions;
import org.apache.http.util.TextUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.util.Objects;

import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlClientAdapter implements Closeable {

    private final Terminal terminal;
    private final PipedOutputStream webWrite2client;
    private final PipedInputStream in2client;
    private final OutputStream clientWrite2web;
    private final SqlClientOptions options;
    private final ISqlClient flinkSqlClient;

    public SqlClientAdapter(PipedInputStream in2client, OutputStream clientWrite2web, SqlClientOptions options)
            throws IOException {
        this.options = options;
        this.in2client = in2client;
        this.clientWrite2web = clientWrite2web;

        webWrite2client = new PipedOutputStream(in2client);
        flinkSqlClient = new FlinkSqlClient(options, this::getDefaultTerminal);

        SqlClientOptions.TerminalSize terminalSize = options.getTerminalSize();
        terminal = TerminalBuilder.builder()
                .name(FlinkSqlClient.CLI_NAME)
                .streams(in2client, clientWrite2web)
                .size(new Size(terminalSize.getColumns(), terminalSize.getRows()))
                .build();
    }

    public void startClient() {
        options.setSessionId(checkSessionId(options.getSessionId()));

        Configuration configuration = GlobalConfiguration.loadConfiguration();
        InetSocketAddress clusterAddress = options.buildConnectAddress()
                .orElseThrow(() -> new IllegalArgumentException("Flink Cluster address is not set."));
        configuration.set(RestOptions.BIND_ADDRESS, clusterAddress.getHostName());
        configuration.set(RestOptions.BIND_PORT, clusterAddress.getPort() + "");
        configuration.set(RestOptions.ADDRESS, clusterAddress.getHostName());
        configuration.set(RestOptions.PORT, clusterAddress.getPort());
        configuration.set(DeploymentOptions.TARGET, RemoteExecutor.NAME);
        //            当客户端程序终止时，作业执行也会终止。
        configuration.set(DeploymentOptions.ATTACHED, true);
        // 如果作业是在attached模式下提交的,在CLI突然终止时执行群集关闭,例如响应用户中断,例如键入Ctrl+C
        // dinky的CLI只作为调试用，不鼓励CLI提交任务，所以开启此选项
        configuration.set(DeploymentOptions.SHUTDOWN_IF_ATTACHED, true);

        flinkSqlClient.startCli(configuration);
    }

    public void onMessage(WsEvent wsEvent, WsEvent.EventType eventType) throws IOException {
        switch (Objects.requireNonNull(eventType)) {
            case TERM_KEY_EVENT:
                String msg = wsEvent.getData();
                webWrite2client.write(msg.getBytes());
                webWrite2client.flush();
                break;
            case TERM_RESIZE:
                Size size = JsonUtils.parseObject(wsEvent.getData(), Size.class);
                terminal.setSize(size);
                break;
            case TERM_CLOSE_EVENT:
                // TODO: 目前没有好办法可以全程优雅的关闭，只能触发exception强制关闭
                terminal.close();
                // String exitMsg = Command.QUIT.name() + ";\n";
                // webWrite2client.write("\u0003\r".getBytes());
                // webWrite2client.write(exitMsg.getBytes());
                break;
            default:
                throw new RuntimeException("unknown message type:" + wsEvent.type);
        }
    }

    private String checkSessionId(String sessionId) {
        if (TextUtils.isEmpty(sessionId)) {
            return "default";
        }
        if (!sessionId.matches("[a-zA-Z0-9_\\-.]+")) {
            throw new IllegalArgumentException("Session identifier must only consists of 'a-zA-Z0-9_-.'.");
        }
        return sessionId;
    }

    private Terminal getDefaultTerminal() {
        return terminal;
    }

    @Override
    public void close() throws IOException {
        CloseUtil.closeNoErrorPrint(flinkSqlClient, webWrite2client, in2client, clientWrite2web);
    }

    @Data
    public static class WsEvent {

        public enum EventType {
            TERM_KEY_EVENT,
            TERM_HEART_EVENT,
            TERM_CLOSE_EVENT,
            TERM_RESIZE;

            public static EventType getEventType(String type) {
                switch (type) {
                    case "TERM_KEY_EVENT":
                        return TERM_KEY_EVENT;
                    case "TERM_HEART_EVENT":
                        return TERM_HEART_EVENT;
                    case "TERM_RESIZE":
                        return TERM_RESIZE;
                    case "TERM_CLOSE_EVENT":
                        return TERM_CLOSE_EVENT;

                    default:
                        throw new RuntimeException("unknown event type:" + type);
                }
            }
        }

        private String type;
        private String data;
        private String sessionId;
    }
}
