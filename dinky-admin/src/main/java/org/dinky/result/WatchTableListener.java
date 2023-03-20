package org.dinky.result;

import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static org.reflections.Reflections.log;

@Slf4j
@Service
public class WatchTableListener extends Thread {
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[4096];

    public WatchTableListener() {
        try {
            this.socket = new DatagramSocket(7125);
            start();
        } catch (SocketException e) {
            log.error(e.getMessage());
        }
    }

    public void run() {
        running = true;
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            String received = new String(packet.getData(), 0, packet.getLength());
            log.debug(received);
        }

        socket.close();
    }

    public void stopThread() {
        running = false;
    }
}
