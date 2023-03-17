package org.apache.flink.connector.udp.table.sink;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.InputTypeConfigurable;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.data.RowData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UdpSinkFunction extends RichSinkFunction<RowData>
        implements InputTypeConfigurable {
    private final String hostname;
    private final int port;
    private final SerializationSchema<RowData> serializer;
    private volatile boolean running = true;
    private DatagramSocket socket;
    private final InetAddress target;


    public UdpSinkFunction(String hostname, int port, SerializationSchema<RowData> serializer) {
        this.hostname = hostname;
        this.port = port;
        this.serializer = serializer;
        try {
            this.target = InetAddress.getByName(hostname);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.serializer.open(null);
        RuntimeContext ctx = getRuntimeContext();
        socket = new DatagramSocket();

    }

    @Override
    public void invoke(RowData value, Context context) throws IOException {
        try {
            byte[] buf = this.serializer.serialize(value);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, target, port);
            socket.send(packet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() {

    }

    @Override
    public void setInputType(TypeInformation<?> type, ExecutionConfig executionConfig) {

    }
}
