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

import org.dinky.sandbox.Sandbox;
import org.dinky.sandbox.metadata.TableId;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Socket 数据处理器
 * 负责处理 Socket 连接接收到的数据并写入 Sandbox
 */
public class SocketDataProcessor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SocketDataProcessor.class);

    private final Socket socket;
    private final Sandbox sandbox;
    private final AtomicBoolean running;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public SocketDataProcessor(Socket socket, Sandbox sandbox) throws IOException {
        this.socket = socket;
        this.sandbox = sandbox;
        this.running = new AtomicBoolean(true);
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        String clientAddress = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        logger.info("开始处理客户端连接: {}", clientAddress);

        try {
            while (running.get() && !socket.isClosed()) {
                try {
                    // 读取 Socket 消息
                    Object obj = inputStream.readObject();

                    if (obj instanceof SocketMessage) {
                        SocketMessage message = (SocketMessage) obj;
                        processMessage(message);
                    } else {
                        logger.warn("接收到未知类型的消息: {}", obj.getClass().getName());
                    }
                } catch (java.io.EOFException e) {
                    // 客户端正常断开连接
                    logger.info("客户端断开连接: {}", clientAddress);
                    break;
                } catch (IOException e) {
                    if (running.get()) {
                        logger.error("读取 Socket 数据失败: {}", clientAddress, e);
                    }
                    break;
                } catch (ClassNotFoundException e) {
                    logger.error("反序列化消息失败: {}", clientAddress, e);
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("处理客户端连接时发生异常: {}", clientAddress, e);
        } finally {
            close();
            logger.info("客户端连接处理结束: {}", clientAddress);
        }
    }

    /**
     * 处理接收到的消息
     */
    private void processMessage(SocketMessage message) {
        processDataMessage(message);
    }

    /**
     * 处理数据消息
     */
    private void processDataMessage(SocketMessage message) {
        try {
            String tableName = message.getTableName();
            String databaseName = message.getDatabaseName();

            if (tableName == null || tableName.trim().isEmpty()) {
                throw new IllegalArgumentException("表名不能为空");
            }

            // 创建 TableId
            TableId tableId = TableId.of(databaseName, tableName);

            // 检查表是否存在
            if (!sandbox.existTable(tableId)) {
                logger.warn("表不存在: {}, 跳过数据写入", tableId);
                return;
            }

            // 写入数据到 Sandbox
            String timeZone = message.getTimeZone() != null ? message.getTimeZone() : "UTC";
            sandbox.writeRowData(tableId, message.getDataRow(), timeZone);
        } catch (Exception e) {
            logger.error("处理数据消息失败: {}", message, e);
        }
    }

    /**
     * 停止处理器
     */
    public void stop() {
        running.set(false);
        close();
    }

    /**
     * 关闭资源
     */
    private void close() {
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
        } catch (IOException e) {
            logger.error("关闭 Socket 资源时发生异常", e);
        }
    }
}
