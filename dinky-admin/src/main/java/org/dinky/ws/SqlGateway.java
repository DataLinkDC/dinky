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

import org.dinky.crypto.CryptoComponent;
import org.dinky.data.model.FragmentVariable;
import org.dinky.executor.VariableManager;
import org.dinky.gateway.SqlCliMode;
import org.dinky.gateway.SqlClientOptions;
import org.dinky.gateway.sqlgateway.cli.SqlClientAdapter;
import org.dinky.utils.CloseUtil;
import org.dinky.utils.FragmentVariableUtils;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.LogUtil;
import org.dinky.utils.SqlUtil;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.simple.SimpleDataSource;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ServerEndpoint("/api/ws/sql-gateway/")
public class SqlGateway {

    private Session session;

    private SqlClientAdapter client;

    private PipedInputStream in2web;

    private long lastHeartTime = System.currentTimeMillis();
    private volatile boolean isRunning = true;

    private static String url;
    private static String username;
    private static String password;
    private static Db db;

    private static CryptoComponent cryptoComponent;

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
                while (isRunning) {
                    try {
                        int data;
                        byte[] bytes = new byte[1024];
                        while ((data = in2web.read(bytes)) != -1) {
                            session.getBasicRemote().sendBinary(ByteBuffer.wrap(bytes, 0, data));
                        }
                        log.info("Sql Client Read Terminal Thread Closed :" + options.getConnectAddress());
                        onClose();
                    } catch (IOException e) {
                        log.error("sql client receive error", e);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            log.error("Sql Client Thread Interrupted Error: ", e);
                        }
                    }
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
    public void onOpen(Session session) throws UnsupportedEncodingException, SQLException {
        this.session = session;

        String cols = getParameter("cols", true);
        String rows = getParameter("rows", true);
        SqlClientOptions.TerminalSize size =
                new SqlClientOptions.TerminalSize(Integer.parseInt(cols), Integer.parseInt(rows));

        if (db == null) {
            db = Db.use(new SimpleDataSource(url, username, password));
        }
        Entity option = Entity.create("dinky_fragment").set("enabled", true);
        List<FragmentVariable> entities = db.find(option, FragmentVariable.class);
        Map<String, String> variableMap = new LinkedHashMap<>();
        if (entities != null) {
            for (FragmentVariable variable : entities) {
                if (FragmentVariableUtils.isSensitive(variable.getName()) && variable.getFragmentValue() != null) {
                    variableMap.put(variable.getName(), cryptoComponent.decryptText(variable.getFragmentValue()));
                } else {
                    variableMap.put(variable.getName(), variable.getFragmentValue());
                }
            }
        }
        VariableManager variableManager = new VariableManager();
        variableManager.registerVariable(variableMap);
        String initSql = URLDecoder.decode(getParameter("initSql"), "UTF-8");
        initSql = SqlUtil.removeNote(initSql);
        initSql = variableManager.parseVariable(initSql);

        SqlClientOptions options = SqlClientOptions.builder()
                .mode(SqlCliMode.fromString(getParameter("mode", true)))
                .sessionId(getParameter("sessionId"))
                .connectAddress(getParameter("connectAddress", true))
                .initSql(initSql)
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

    @Autowired
    public void setCryptoComponent(CryptoComponent cryptoComponent) {
        SqlGateway.cryptoComponent = cryptoComponent;
    }

    @Value("${spring.datasource.url}")
    public void setUrl(String url) {
        SqlGateway.url = url;
    }

    @Value("${spring.datasource.username}")
    public void setUsername(String username) {
        SqlGateway.username = username;
    }

    @Value("${spring.datasource.password}")
    public void setPassword(String password) {
        SqlGateway.password = password;
    }
}
