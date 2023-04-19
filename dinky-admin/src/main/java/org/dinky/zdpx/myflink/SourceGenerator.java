/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dinky.zdpx.myflink;

import org.apache.flink.util.FileUtils;
import org.apache.flink.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.TimeZone;
import java.util.function.Consumer;

public class SourceGenerator {
    /**
     * Formatter for SQL string representation of a time value.
     */
    static final DateTimeFormatter SQL_TIME_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .toFormatter();
    /**
     * Formatter for SQL string representation of a timestamp value (without UTC timezone).
     */
    static final DateTimeFormatter SQL_TIMESTAMP_FORMAT = new DateTimeFormatterBuilder()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(SQL_TIME_FORMAT)
            .toFormatter();
    private static final Logger logger = LoggerFactory.getLogger(SourceGenerator.class);
    private static final long SPEED = 1000;
    private static final String MAX = "MAX";
    private static final String FROM_BEGINNING = "from-beginning";
    private static final File checkpoint = new File("checkpoint");

    public static void main(String[] args) {
        File userBehaviorFile = new File("datagen/user_behavior.log");
        long speed = SPEED;
        long endLine = Long.MAX_VALUE;
        Consumer<String> consumer = new ConsolePrinter();

        // parse arguments
        int argOffset = 0;
        while (argOffset < args.length) {

            String arg = args[argOffset++];
            switch (arg) {
                case "--input":
                    String basePath = args[argOffset++];
                    userBehaviorFile = new File(basePath);
                    break;
                case "--output":
                    String sink = args[argOffset++];
                    switch (sink) {
                        case "console":
                            consumer = new ConsolePrinter();
                            break;
                        case "kafka":
                            String brokers = args[argOffset++];
                            consumer = new KafkaProducer("user_behavior", brokers);
                            break;
                        default: {
                            var err = "Unknown output configuration";
                            throw new IllegalArgumentException(err);
                        }
                    }
                    break;
                case "--speedup":
                    String spd = args[argOffset++];
                    if (MAX.equalsIgnoreCase(spd)) {
                        speed = Long.MAX_VALUE;
                    } else {
                        speed = Long.parseLong(spd);
                    }
                    break;
                case "--endline":
                    String end = args[argOffset++];
                    endLine = Long.parseLong(end);
                    break;
                default: {
                    var err = "Unknown parameter";
                    logger.error(err);
                    throw new IllegalArgumentException(err);
                }
            }
        }

        long startLine = 0;
        if (checkpoint.exists()) {
            String line = null;
            try {
                line = FileUtils.readFileUtf8(checkpoint);
            } catch (IOException e) {
                logger.error("exception", e);
            }
            if (!StringUtils.isNullOrWhitespaceOnly(line)) {
                startLine = Long.parseLong(line);
            }
        }

        if (!checkpoint.exists()) {
            try {
                checkpoint.createNewFile();
            } catch (IOException e) {
                logger.error("exception", e);
            }
        }
        checkpointState(startLine);

        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        try (InputStream inputStream = new FileInputStream(userBehaviorFile)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            int counter = 0;
            long start = System.nanoTime();
            long currentLine = 0;
            while (reader.ready()) {
                String line = reader.readLine();
                if (currentLine < startLine) {
                    currentLine++;
                    continue;
                }
                currentLine++;
                checkpointState(currentLine);
                String[] splits = line.split(",");
                long ts = Long.parseLong(splits[4]) * 1000;
                Instant instant = Instant.ofEpochMilli(ts + tz.getOffset(ts));
                LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Z"));
                String outline = String.format(
                        "{\"user_id\": \"%s\", \"item_id\":\"%s\", \"category_id\": \"%s\", \"behavior\": \"%s\", " +
                                "\"ts\": \"%s\"}",
                        splits[0],
                        splits[1],
                        splits[2],
                        splits[3],
                        SQL_TIMESTAMP_FORMAT.format(dateTime));
                consumer.accept(outline);
                counter++;
                if (counter >= speed) {
                    long end = System.nanoTime();
                    long diff = end - start;
                    while (diff < 1000_000_000) {
                        Thread.sleep(1);
                        end = System.nanoTime();
                        diff = end - start;
                    }
                    start = end;
                    counter = 0;
                }
                if (currentLine == 10) {
                    logger.info("Start sending messages to Kafka...");
                }
                if (currentLine >= endLine) {
                    logger.info("send {} lines.", currentLine);
                    break;
                }
            }
            reader.close();
        } catch (IOException | InterruptedException e) {
            logger.error("exception", e);
        }
    }

    private static void checkpointState(long lineState) {
        try {
            FileUtils.writeFileUtf8(checkpoint, String.valueOf(lineState));
        } catch (IOException e) {
            logger.error("exception", e);
        }
    }
}
