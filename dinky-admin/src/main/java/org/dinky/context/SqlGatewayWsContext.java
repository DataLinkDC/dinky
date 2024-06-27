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

package org.dinky.context;

import org.dinky.gateway.SqlCliMode;
import org.dinky.gateway.SqlClientOptions;
import org.dinky.gateway.sqlgateway.cli.SqlClientAdapter;
import org.dinky.utils.CloseUtil;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.LogUtil;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ServerEndpoint("/ws/sql-gateway/")
public class SqlGatewayWsContext {

    private Session session;

    private SqlClientAdapter client;

    private PipedInputStream in2web;

    private long lastHeartTime = System.currentTimeMillis();
    private boolean isRunning = true;

    /**
     * 最大化减少线程占用，默认线程为0，无最大限制，不保持线程，任务直接提交给线程
     * */
    private static final ExecutorService executor = ThreadUtil.newExecutor();

    private void startClient(SqlClientOptions options) {
        try {
            PipedInputStream in2client = new PipedInputStream();

            in2web = new PipedInputStream();
            PipedOutputStream clientWrite2web = new PipedOutputStream(in2web);
            clientWrite2web.write("Dinky Sql Client\n".getBytes());

            client = new SqlClientAdapter(in2client, clientWrite2web, options);

            executor.execute(() -> {
                try {
                    log.info("Sql Client Start : " + options.getConnectAddress());
                    client.startClient();
                } catch (Exception e) {
                    sendError(e);
                }
            });
            executor.execute(() -> {
                try {
                    int data;
                    byte[] bytes = new byte[1024];
                    while ((data = in2web.read(bytes)) != -1) {
                        session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes, 0, data));
                    }
                    log.info("Sql Client Read Terminal Thread Closed :" + options.getConnectAddress());
                } catch (IOException e) {
                    log.error("sql client receive error", e);
                }
            });
        } catch (Exception e) {
            sendError(e);
        }
        log.info("Sql Client Start Success : " + options.getConnectAddress());
    }

    private void sendError(Throwable err) {
        try {
            log.error("send error to client", err);
            ByteBuffer byteBuffer = ByteBuffer.wrap(LogUtil.getError(err).getBytes());
            session.getBasicRemote().sendBinary(byteBuffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;

        String cols = getParameter("cols", true);
        String rows = getParameter("rows", true);
        SqlClientOptions.TerminalSize size =
                new SqlClientOptions.TerminalSize(Integer.parseInt(cols), Integer.parseInt(rows));

        SqlClientOptions options = SqlClientOptions.builder()
                .mode(SqlCliMode.fromString(getParameter("mode", true)))
                .sessionId(getParameter("sessionId"))
                .connectAddress(getParameter("connectAddress", true))
                .initSql(getParameter("initSql"))
                .historyFilePath("./tmp/flink-sql-history/history")
                .terminalSize(size)
                .build();

        startClient(options);

        executor.execute(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(1000);
                    if (System.currentTimeMillis() - lastHeartTime > 1000 * 60) {
                        onClose();
                    }
                } catch (Exception e) {
                    log.error("SQl Client Heart Thread Error: ", e);
                }
            }
            log.info("Sql Client Heart Thread Closed :");
        });
    }

    @OnClose
    public void onClose() {
        isRunning = false;
        CloseUtil.closeNoErrorPrint(client, in2web, session);
    }

    @OnMessage
    public void onMessage(String messages) {
        SqlClientAdapter.WsEvent wsEvent = JsonUtils.parseObject(messages, SqlClientAdapter.WsEvent.class);
        if (wsEvent == null) {
            throw new RuntimeException("parse wsEvent error");
        } else {
            SqlClientAdapter.WsEvent.EventType eventType =
                    SqlClientAdapter.WsEvent.EventType.getEventType(wsEvent.getType());
            if (eventType == SqlClientAdapter.WsEvent.EventType.TERM_HEART_EVENT) {
                lastHeartTime = System.currentTimeMillis();
            } else {
                try {
                    client.onMessage(wsEvent, eventType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private String getParameter(String key) {
        return getParameter(key, false);
    }

    private String getParameter(String key, boolean required) {
        List<String> list = session.getRequestParameterMap().get(key);
        if (list == null || list.size() == 0) {
            if (required) {
                throw new RuntimeException("parameter " + key + " is required");
            } else {
                return "";
            }
        }
        return list.get(0);
    }
}
