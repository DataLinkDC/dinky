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

package org.dinky.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Opt;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuntimeUtils {
    static final List<Process> RUNNING = new CopyOnWriteArrayList<>();

    public static void run(String shell) {
        run(shell, log::info, log::error);
    }

    public static int run(
            String shell, Consumer<String> outputConsumer, Consumer<String> errorConsumer) {
        Process process;
        int waitValue = 1;
        try {
            process = Runtime.getRuntime().exec(shell);
            RUNNING.add(process);
            new Thread(
                            () -> {
                                InputStream inputStream = process.getInputStream();
                                InputStreamReader inputStreamReader =
                                        new InputStreamReader(inputStream);
                                BufferedReader reader = new BufferedReader(inputStreamReader);

                                String line;
                                try {
                                    while ((line = reader.readLine()) != null) {
                                        if (outputConsumer != null) {
                                            outputConsumer.accept(line);
                                        }
                                    }
                                    reader.close();
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            })
                    .start();
            waitValue = process.waitFor();
            RUNNING.remove(process);
            if (waitValue != 0) {
                LineNumberReader lineNumberReader =
                        new LineNumberReader(new InputStreamReader(process.getErrorStream()));
                String errMsg = lineNumberReader.lines().collect(Collectors.joining("\n"));
                Opt.ofNullable(errorConsumer).ifPresent(x -> x.accept(errMsg));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return waitValue;
    }
}
