package org.apache.flink.connector.printnet.table.sink;

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
    private volatile boolean running = true;
    private DatagramSocket socket;
    private final InetAddress target;


    public PrintNetSinkFunction(String hostname, int port, SerializationSchema<RowData> serializer,
                                DynamicTableSink.DataStructureConverter converter) {
        this.hostname = hostname;
        this.port = port;
        this.serializer = serializer;
        this.converter = converter;
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
            byte[] buf = serializer != null ?
                    serializer.serialize(value)
                    : converter.toExternal(value).toString().getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, target, port);
            socket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
