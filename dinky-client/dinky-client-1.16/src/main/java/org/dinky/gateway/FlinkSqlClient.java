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

package org.dinky.gateway;

import org.apache.flink.configuration.Configuration;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Supplier;

import org.jline.terminal.Terminal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FlinkSqlClient implements Closeable, ISqlClient {

    public static final String CLI_NAME = "Dinky Flink SQL WEB CLI";
    private final Supplier<Terminal> terminalFactory;

    public FlinkSqlClient(SqlClientOptions sqlClientOptions, Supplier<Terminal> terminalFactory) {
        this.terminalFactory = terminalFactory;
    }

    public void startCli(Configuration configuration) {
        terminalFactory.get().writer().printf("Flink 1.14 ~ 1.16 Not Support yet! \n Flink 1.14 ~ 1.16 暂不支持!敬请期待 \n");
        try {
            terminalFactory.get().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {}
}
