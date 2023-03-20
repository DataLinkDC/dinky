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

package org.dinky.trans.ddl;

import org.dinky.executor.Executor;
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.Operation;

import org.apache.flink.table.api.TableResult;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WatchTableOperation extends AbstractOperation implements Operation {

    private static final String KEY_WORD = "WATCH";

    public static final String PATTERN_STR = "WATCH (.+)";
    public static final Pattern PATTERN = Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE);

    public static final String CREATE_SQL_TEMPLATE =
            "CREATE TABLE print_{0} WITH (''connector'' = ''printnet'', "
                    + "''port''=''{2}'', ''hostName'' = ''{1}'', ''sink.parallelism''=''{3}'')\n"
                    + "LIKE {0};";
    public static final String INSERT_SQL_TEMPLATE = "insert into print_{0} select * from {0};";
    public static final int PORT = 7125;

    public WatchTableOperation() {}

    public WatchTableOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new WatchTableOperation(statement);
    }

    @Override
    public TableResult build(Executor executor) {
        Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        String tableName = matcher.group(1);

        Optional<InetAddress> address = getSystemLocalIp();
        String ip = address.isPresent() ? address.get().getHostAddress() : "127.0.0.1";

        String printCreateSql = MessageFormat.format(CREATE_SQL_TEMPLATE, tableName, ip, PORT, 1);
        executor.getCustomTableEnvironment().executeSql(printCreateSql);

        String printInsertSql = MessageFormat.format(INSERT_SQL_TEMPLATE, tableName);
        return executor.getCustomTableEnvironment().executeSql(printInsertSql);
    }

    private Optional<InetAddress> getSystemLocalIp() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return Optional.of(ip);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
