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
import org.dinky.sandbox.Sandbox;
import org.dinky.sandbox.metadata.ColumnInfo;
import org.dinky.sandbox.metadata.TableId;
import org.dinky.sandbox.metadata.TableType;

import org.apache.flink.types.Row;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        logger.info("Start processing the client connection: {}", clientAddress);

        try {
            while (running.get() && !socket.isClosed()) {
                try {
                    Object obj = inputStream.readObject();

                    if (obj instanceof DataChangeEventSocketMessage) {
                        processDataMessage((DataChangeEventSocketMessage) obj);
                    } else if (obj instanceof CreateTableEventSocketMessage) {
                        processCreateTableSocketMessage((CreateTableEventSocketMessage) obj);
                    } else {
                        logger.warn(
                                "Received a message of an unknown type: {}",
                                obj.getClass().getName());
                    }
                } catch (java.io.EOFException e) {
                    logger.info("The client disconnected: {}", clientAddress);
                    break;
                } catch (java.io.IOException e) {
                    if (running.get()) {
                        logger.error("Failed to read data from the Socket: {}", clientAddress, e);
                    }
                    break;
                } catch (ClassNotFoundException e) {
                    logger.error("Failed to deserialize the message: {}", clientAddress, e);
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("An exception occurred while processing the client connection: {}", clientAddress, e);
        } finally {
            close();
            logger.info("The processing of the client connection is completed: {}", clientAddress);
        }
    }

    private void processCreateTableSocketMessage(CreateTableEventSocketMessage message) {
        try {
            String tableIdentifier = message.getTableId();

            if (tableIdentifier == null || tableIdentifier.trim().isEmpty()) {
                throw new IllegalArgumentException("The table name cannot be empty.");
            }

            TableId tableId = TableId.parse(tableIdentifier);
            if (Asserts.isNotNullString(message.getBoxId())) {
                tableId = TableId.withPrivate(message.getBoxId(), tableIdentifier);
            }

            if (sandbox.existTable(tableId)) {
                logger.warn("The table exists: {}, skip automatic table creation", tableId);
                return;
            }
            if (Asserts.isNotNull(message.getColumns()) && message.getColumns() instanceof List) {
                sandbox.registerTable(
                        tableId, TableType.valueOf(message.getTableType()), (List<ColumnInfo>) message.getColumns());
            } else {
                sandbox.registerTable(tableId, TableType.valueOf(message.getTableType()), new ArrayList<>());
            }
        } catch (Exception e) {
            logger.error("Failed to process the data message: {}", message, e);
        }
    }

    private void processDataMessage(DataChangeEventSocketMessage message) {
        try {
            String tableIdentifier = message.getTableId();

            if (tableIdentifier == null || tableIdentifier.trim().isEmpty()) {
                throw new IllegalArgumentException("The table name cannot be empty.");
            }

            TableId tableId = TableId.parse(tableIdentifier);
            if (Asserts.isNotNullString(message.getBoxId())) {
                tableId = TableId.withPrivate(message.getBoxId(), tableIdentifier);
            }

            if (!sandbox.existTable(tableId)) {
                logger.warn("The table exists: {}, skip automatic table creation", tableId);
                return;
            }
            String timeZone = message.getTimeZone() != null ? message.getTimeZone() : "UTC";
            sandbox.writeRowData(tableId, (Row) message.getDataRow(), timeZone);
        } catch (Exception e) {
            logger.error("Failed to process the data message: {}", message, e);
        }
    }

    public void stop() {
        running.set(false);
        close();
    }

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
            logger.error("An exception occurred while closing Socket resources.", e);
        }
    }
}
