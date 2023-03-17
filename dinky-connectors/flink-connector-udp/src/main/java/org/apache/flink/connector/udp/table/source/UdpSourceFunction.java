package org.apache.flink.connector.udp.table.source;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.table.data.RowData;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UdpSourceFunction extends RichSourceFunction<RowData>
        implements ResultTypeQueryable<RowData> {
    private final String hostname;
    private final int port;
    private final DeserializationSchema<RowData> deserializer;
    private volatile boolean running = true;
    private DatagramSocket socket;
    private byte[] buf = new byte[1024];

    public UdpSourceFunction(
            String hostname, int port, DeserializationSchema<RowData> deserializer) {
        this.hostname = hostname;
        this.port = port;
        this.deserializer = deserializer;
    }

    @Override
    public TypeInformation<RowData> getProducedType() {
        return deserializer.getProducedType();
    }

    @Override
    public void run(SourceContext<RowData> sourceContext) throws Exception {
        this.socket = createDatagramSocket(port);
        this.deserializer.open(null);
        while (running) {

            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                sourceContext.collect(deserializer.deserialize((packet.getData())));
            } catch (Exception e) {
                System.out.println(e);
                if (socket == null) {
                    return;
                }
            }
        }
    }

    @Override
    public void cancel() {
        running = false;
        socket.close();
    }

    DatagramSocket createDatagramSocket(int port) {
        try {
            return new DatagramSocket(port);
        } catch (SocketException e) {
            return null;
        }
    }
}
