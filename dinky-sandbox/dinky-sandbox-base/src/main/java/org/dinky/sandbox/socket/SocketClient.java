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

import org.dinky.assertion.Asserts;
import org.dinky.data.socket.CreateTableEventSocketMessage;
import org.dinky.data.socket.DataChangeEventSocketMessage;
import org.dinky.sandbox.metadata.ColumnInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketClient implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final String host;
    private final int port;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private final AtomicBoolean connected;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.connected = new AtomicBoolean(false);
    }

    public void connect() throws IOException {
        if (connected.compareAndSet(false, true)) {
            try {
                socket = new Socket(host, port);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                logger.info("Successfully connected to the server: {}:{}", host, port);
            } catch (IOException e) {
                connected.set(false);
                logger.error("Failed to connect to the server: {}:{}", host, port, e);
                throw e;
            }
        } else {
            logger.warn("The client has connected to the server.");
        }
    }

    public boolean sendCreateTableEvent(String boxId, String tableId, List<ColumnInfo> columns, String tableType) {
        if (Asserts.isNullString(boxId)) {
            boxId = "public";
        }
        if (!connected.get()) {
            logger.error("The client is not connected to the server.");
            return false;
        }

        try {
            CreateTableEventSocketMessage message =
                    new CreateTableEventSocketMessage(boxId, tableId, columns, tableType);
            outputStream.writeObject(message);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            logger.error("Failed to send data", e);
            return false;
        }
    }

    public boolean sendData(String boxId, String tableId, Object dataRow) {
        if (Asserts.isNullString(boxId)) {
            boxId = "public";
        }
        return sendData(boxId, tableId, dataRow, "UTC");
    }

    public boolean sendData(String boxId, String tableId, Object dataRow, String timeZone) {
        if (!connected.get()) {
            logger.error("The client is not connected to the server.");
            return false;
        }

        try {
            DataChangeEventSocketMessage message = new DataChangeEventSocketMessage(boxId, tableId, dataRow, timeZone);
            outputStream.writeObject(message);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            logger.error("Failed to send data", e);
            return false;
        }
    }

    public boolean isConnected() {
        return connected.get() && socket != null && !socket.isClosed();
    }

    public String getHost() {
        return host;
    }

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
                logger.info("The client connection has been closed.");
            } catch (IOException e) {
                logger.error("An exception occurred while closing the client connection.", e);
            }
        }
    }
}
