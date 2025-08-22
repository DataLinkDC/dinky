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

package org.dinky.sandbox.socket;

import org.apache.flink.types.Row;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Socket 客户端
 * 用于向 SocketServerSandbox 发送数据
 */
public class SocketClient implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final String host;
    private final int port;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private final AtomicBoolean connected;

    /**
     * 构造函数
     *
     * @param host 服务器主机
     * @param port 服务器端口
     */
    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.connected = new AtomicBoolean(false);
    }

    /**
     * 连接到服务器
     */
    public void connect() throws IOException {
        if (connected.compareAndSet(false, true)) {
            try {
                socket = new Socket(host, port);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                logger.info("成功连接到服务器: {}:{}", host, port);
            } catch (IOException e) {
                connected.set(false);
                logger.error("连接服务器失败: {}:{}", host, port, e);
                throw e;
            }
        } else {
            logger.warn("客户端已经连接到服务器");
        }
    }

    /**
     * 发送数据消息
     *
     * @param tableName 表名
     * @param dataRow Flink 数据行
     * @return 是否发送成功
     */
    public boolean sendData(String tableName, Row dataRow) {
        return sendData(tableName, null, dataRow, "UTC");
    }

    /**
     * 发送数据消息
     *
     * @param tableName 表名
     * @param databaseName 数据库名
     * @param dataRow Flink 数据行
     * @param timeZone 时区
     * @return 是否发送成功
     */
    public boolean sendData(String tableName, String databaseName, Row dataRow, String timeZone) {
        if (!connected.get()) {
            logger.error("客户端未连接到服务器");
            return false;
        }

        try {
            SocketMessage message = new SocketMessage(tableName, databaseName, dataRow, timeZone);
            outputStream.writeObject(message);
            outputStream.flush();

            logger.debug("成功发送数据到表: {}, 数据: {}", tableName, dataRow);
            return true;
        } catch (IOException e) {
            logger.error("发送数据失败", e);
            return false;
        }
    }

    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return connected.get() && socket != null && !socket.isClosed();
    }

    /**
     * 获取服务器主机
     */
    public String getHost() {
        return host;
    }

    /**
     * 获取服务器端口
     */
    public int getPort() {
        return port;
    }

    @Override
    public void close() throws Exception {
        if (connected.compareAndSet(true, false)) {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
                logger.info("客户端连接已关闭");
            } catch (IOException e) {
                logger.error("关闭客户端连接时发生异常", e);
            }
        }
    }
}
