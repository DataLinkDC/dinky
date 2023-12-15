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

import org.dinky.context.SseSessionContextHolder;
import org.dinky.data.enums.SseTopic;
import org.dinky.data.vo.PrintTableVo;
import org.dinky.explainer.print_table.PrintStatementExplainer;
import org.dinky.parser.SqlType;
import org.dinky.service.PrintTableService;
import org.dinky.trans.Operations;
import org.dinky.utils.SqlUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cn.hutool.core.text.StrFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PrintTableServiceImpl implements PrintTableService {

    private static final Pattern FULL_TABLE_NAME_PATTERN = Pattern.compile("^`(\\w+)`\\.`(\\w+)`\\.`(\\w+)`$");

    public PrintTableServiceImpl() {
        PrintTableListener printer = new PrintTableListener(this::send);
        printer.start();
    }

    @Override
    public List<PrintTableVo> getPrintTables(String statement) {
        // TODO: 2023/4/7 this function not support variable sql, because, JobManager and executor
        // couple function
        //  and status and task execute.
        final String[] statements = SqlUtil.getStatements(SqlUtil.removeNote(statement));
        return Arrays.stream(statements)
                .filter(t -> SqlType.PRINT.equals(Operations.getOperationType(t)))
                .flatMap(t -> Arrays.stream(PrintStatementExplainer.splitTableNames(t)))
                .map(t -> new PrintTableVo(t, getFullTableName(t)))
                .collect(Collectors.toList());
    }

    public void send(String message) {
        try {
            String[] data = message.split("\n", 2);
            String topic = StrFormatter.format("{}/{}", SseTopic.PRINT_TABLE.getValue(), data[0]);
            SseSessionContextHolder.sendTopic(topic, data[1]);
        } catch (Exception e) {
            log.error("send message failed: {}", e.getMessage());
        }
    }

    public static String getFullTableName(String table) {
        if (table == null) {
            throw new NullPointerException("table name empty");
        }

        Matcher matcher = FULL_TABLE_NAME_PATTERN.matcher(table);
        return matcher.matches()
                ? matcher.replaceAll("`$1`.`$2`.`print_$3`")
                : String.format("`default_catalog`.`default_database`.`print_%s`", table);
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
            InetAddress host = null;
            try {
                host = InetAddress.getByName("0.0.0.0");
                return new DatagramSocket(port, host);
            } catch (SocketException | UnknownHostException e) {
                log.error(
                        "PrintTableListener:DatagramSocket init failed, host: {}, port {}: {}",
                        host == null ? null : host.getHostAddress(),
                        PORT,
                        e.getMessage());
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
