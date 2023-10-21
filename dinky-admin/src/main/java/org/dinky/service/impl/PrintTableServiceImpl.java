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

package org.dinky.service.impl;

import org.dinky.service.PrintTableService;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PrintTableServiceImpl implements PrintTableService {

    private final Map<String, Set<Target>> registerTableMap = new ConcurrentHashMap<>();

    private static final Pattern FULL_TABLE_NAME_PATTERN = Pattern.compile("^`(\\w+)`\\.`(\\w+)`\\.`(\\w+)`$");

    public PrintTableServiceImpl() {
        PrintTableListener printer = new PrintTableListener(this::send);
        printer.start();
    }

    public void send(String message) {
        try {
            String[] data = message.split("\n", 2);
            final Set<Target> targets = registerTableMap.get(data[0]);
            if (targets != null) {
                targets.forEach(d -> d.send(data[1], "data"));
            }
        } catch (Exception e) {
            log.error("send message failed: {}", e.getMessage());
        }
    }

    @Override
    public SseEmitter registerListenEntry(String table) {
        String fullName = getFullTableName(table);
        String destination = getDestinationByFullName(fullName);
        Set<Target> targets = registerTableMap.get(fullName);

        SseEmitter emitter = getSseEmitter(targets, fullName);

        Set<Target> newTargets = new HashSet<>();
        newTargets.add(Target.of(emitter, destination));
        if (targets != null) {
            newTargets.addAll(targets);
        }
        registerTableMap.put(fullName, newTargets);
        return emitter;
    }

    private SseEmitter getSseEmitter(Set<Target> targets, String fullName) {
        SseEmitter emitter = new SseEmitter();
        emitter.onCompletion(() -> {
            log.info(MessageFormat.format("SseEmitter {0} complete", emitter));
            Set<Target> remains = targets.stream()
                    .filter(t -> !t.getEmitter().equals(emitter))
                    .collect(Collectors.toSet());
            registerTableMap.put(fullName, remains);
        });

        emitter.onError(e -> {
            log.error("SseEmitter error: {}", e.getMessage());
            emitter.complete();
        });

        emitter.onTimeout(() -> {
            log.debug("SseEmitter timeout: {}", emitter.getTimeout());
            emitter.complete();
        });
        return emitter;
    }

    @Override
    public void unRegisterListenEntry(String table) {
        String fullName = getFullTableName(table);
        String destination = getDestination(fullName);
        Set<Target> destinations = registerTableMap.get(fullName);
        if (destinations != null) {
            destinations.stream().filter(d -> d.destination.equals(destination)).forEach(Target::complete);
            destinations.removeIf(d -> d.destination.equals(destination));
        }
    }

    public static String getFullTableName(String table) {
        if (table == null) {
            throw new NullPointerException("table name empty");
        }

        Matcher matcher = FULL_TABLE_NAME_PATTERN.matcher(table);
        return matcher.matches() ?
                matcher.replaceAll("`$1`.`$2`.`print_$3`") :
                String.format("`default_catalog`.`default_database`.`print_%s`", table);
    }

    public static String getDestination(String table) {
        String fn = getFullTableName(table);
        return String.format("/topic/table/%s", fn);
    }

    public static String getDestinationByFullName(String tableFullName) {
        return String.format("/topic/table/%s", tableFullName);
    }

    public static final class Target {
        private final String destination;
        public final SseEmitter emitter;

        public Target(String destination, SseEmitter emitter) {
            this.destination = destination;
            this.emitter = emitter;
        }

        public static Target of(SseEmitter emitter, String destination) {
            return new Target(destination, emitter);
        }

        public void send(String message, String type) {
            try {
                SseEmitter.SseEventBuilder builder =
                        SseEmitter.event().data(message).name("pt_" + type);
                emitter.send(builder);
            } catch (Exception e) {
                log.debug("SseEmitter {}, send message failed: {}", emitter, e.getMessage());
            }
        }

        public String getDestination() {
            return destination;
        }

        public SseEmitter getEmitter() {
            return emitter;
        }

        public void complete() {
            emitter.complete();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Target target = (Target) o;
            return Objects.equals(destination, target.destination) && Objects.equals(emitter, target.emitter);
        }

        @Override
        public int hashCode() {
            return Objects.hash(destination, emitter);
        }
    }

    public static class PrintTableListener {

        private final Consumer<String> consumer;
        public static final int PORT = 7125;
        private DatagramSocket socket;
        private final byte[] buf = new byte[4096];

        private final ExecutorService executor;

        public PrintTableListener(Consumer<String> consumer) {
            this.consumer = consumer;
            this.socket = getDatagramSocket(PORT);
            executor = Executors.newSingleThreadExecutor();
        }

        public void start() {
            executor.execute(this::run);
        }

        private static DatagramSocket getDatagramSocket(int port) {
            try {
                return new DatagramSocket(port);
            } catch (SocketException e) {
                log.error("PrintTableListener:DatagramSocket init failed, port {}: {}", PORT, e.getMessage());
            }
            return null;
        }

        public void run() {
            if (socket == null) {
                log.warn("PrintTableListener:socket is null, try to initial it");
                socket = getDatagramSocket(PORT);
                if (socket == null) return;
            }

            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    consumer.accept(received);
                } catch (Exception e) {
                    log.error("print table receive data:" + e.getMessage());
                }
            }
        }
    }
}
