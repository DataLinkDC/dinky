package org.dinky.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.dinky.service.WatchTableService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
@Service
public class WatchTableServiceImpl implements WatchTableService {

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, Set<String>> registerTableMap = new ConcurrentHashMap<>();

    public WatchTableServiceImpl(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        WatchTableListener watcher = new WatchTableListener(this::send);
        watcher.start();
    }

    public void send(String message) {
        try {
            String tableFullName = message.substring(0, message.indexOf("\n"));
            final Set<String> destinations = registerTableMap.get(tableFullName);
            if (destinations != null) {
                destinations.forEach(d -> this.messagingTemplate.convertAndSend(d, message));
            }
        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void registerListenEntry(Integer userId, String table) {
        String destination = getDestination(userId, table);
        Set<String> destinations = registerTableMap.get(table);
        if (destinations == null) {
            registerTableMap.put(table, new HashSet<>(Arrays.asList(destination)));
        } else {
            destinations.add(destination);
        }
    }

    @Override
    public void unRegisterListenEntry(Integer userId, String table) {
        String destination = getDestination(userId, table);
        Set<String> destinations = registerTableMap.get(table);
        if (destinations != null) {
            destinations.remove(destination);
        }
    }

    private static String getDestination(Integer userId, String table) {
        return String.format("/topic/table/%s/%s", userId, table);
    }

    public static class WatchTableListener extends Thread {

        private final Consumer<String> consumer;
        public static final int PORT = 7125;
        private DatagramSocket socket;
        private boolean running;
        private byte[] buf = new byte[4096];

        public WatchTableListener(Consumer<String> consumer) {
            this.consumer = consumer;
            try {
                this.socket = new DatagramSocket(PORT);
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
                    String received = new String(packet.getData(), 0, packet.getLength());
                    consumer.accept(received);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }

            socket.close();
        }

        public void stopThread() {
            running = false;
        }
    }
}
