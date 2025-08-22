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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Socket Server Sandbox 实现
 * 提供 Socket 服务器功能，监听端口数据并将 Flink 数据流写入对应的表
 */
public class SandboxSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(SandboxSocketServer.class);

    private final Sandbox delegateSandbox;
    private final int port;
    private final String host;
    private final int maxConnections;

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private final AtomicBoolean running;
    private Thread serverThread;

    /**
     * 构造函数
     *
     * @param delegateSandbox 委托的 Sandbox 实现
     * @param port 监听端口
     */
    public SandboxSocketServer(Sandbox delegateSandbox, int port) {
        this(delegateSandbox, "0.0.0.0", port, 100);
    }

    /**
     * 构造函数
     *
     * @param delegateSandbox 委托的 Sandbox 实现
     * @param host 监听主机
     * @param port 监听端口
     * @param maxConnections 最大连接数
     */
    public SandboxSocketServer(Sandbox delegateSandbox, String host, int port, int maxConnections) {
        this.delegateSandbox = delegateSandbox;
        this.host = host;
        this.port = port;
        this.maxConnections = maxConnections;
        this.running = new AtomicBoolean(false);
    }

    /**
     * 启动 Socket 服务器
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            try {
                serverSocket = new ServerSocket(port);
                executorService = Executors.newFixedThreadPool(maxConnections);

                serverThread = new Thread(this::runServer, "SandboxSocketServer-" + port);
                serverThread.setDaemon(true);
                serverThread.start();

                logger.info("Sandbox Socket Server 启动成功，监听地址: {}:{}", host, port);
            } catch (IOException e) {
                running.set(false);
                logger.error("启动 Sandbox Socket Server 失败", e);
                throw new RuntimeException("启动 Sandbox Socket Server 失败", e);
            }
        } else {
            logger.warn("Sandbox Socket Server 已经在运行中");
        }
    }

    /**
     * 停止 Socket 服务器
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }

                if (executorService != null) {
                    executorService.shutdown();
                }

                if (serverThread != null) {
                    serverThread.interrupt();
                }

                logger.info("Sandbox Socket Server 已停止");
            } catch (IOException e) {
                logger.error("停止 Sandbox Socket Server 时发生异常", e);
            }
        }
    }

    /**
     * 运行服务器
     */
    private void runServer() {
        while (running.get() && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info(
                        "接受新的客户端连接: {}", clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                SocketDataProcessor processor = new SocketDataProcessor(clientSocket, delegateSandbox);
                executorService.submit(processor);

            } catch (IOException e) {
                if (running.get()) {
                    logger.error("接受客户端连接时发生异常", e);
                }
            }
        }
    }

    /**
     * 检查服务器是否正在运行
     */
    public boolean isRunning() {
        return running.get() && serverSocket != null && !serverSocket.isClosed();
    }

    /**
     * 获取监听端口
     */
    public int getPort() {
        return port;
    }

    /**
     * 获取监听主机
     */
    public String getHost() {
        return host;
    }
}
