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

package org.dinky.connector.printnet.sink;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.operators.StreamingRuntimeContext;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.data.RowData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PrintNetSinkFunction extends RichSinkFunction<RowData> {
    private final String hostname;
    private final int port;
    private final SerializationSchema<RowData> serializer;
    private DynamicTableSink.DataStructureConverter converter;
    private String printIdentifier;
    private byte[] printHeader;
    private volatile boolean running = true;
    private DatagramSocket socket;
    private final InetAddress target;

    public PrintNetSinkFunction(
            String hostname,
            int port,
            SerializationSchema<RowData> serializer,
            DynamicTableSink.DataStructureConverter converter,
            String printIdentifier) {
        this.hostname = hostname;
        this.port = port;
        this.serializer = serializer;
        this.converter = converter;
        this.printIdentifier = printIdentifier;
        printHeader = (printIdentifier + "\n").getBytes();

        try {
            this.target = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        if (serializer != null) {
            serializer.open(null);
        }

        StreamingRuntimeContext context = (StreamingRuntimeContext) getRuntimeContext();
        socket = new DatagramSocket();
    }

    @Override
    public void invoke(RowData value, Context context) throws IOException {

        try {
            byte[] buf = serializer != null
                    ? serializer.serialize(value)
                    : converter.toExternal(value).toString().getBytes();

            byte[] target = new byte[printHeader.length + buf.length];
            System.arraycopy(printHeader, 0, target, 0, printHeader.length);
            System.arraycopy(buf, 0, target, printHeader.length, buf.length);

            DatagramPacket packet = new DatagramPacket(target, target.length, this.target, port);
            socket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
