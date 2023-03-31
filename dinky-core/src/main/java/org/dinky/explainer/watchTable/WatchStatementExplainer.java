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

package org.dinky.explainer.watchTable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WatchStatementExplainer {

    public static final String PATTERN_STR = "WATCH (.+)";
    public static final Pattern PATTERN = Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE);

    public static final String CREATE_SQL_TEMPLATE =
            "CREATE TABLE print_{0} WITH (''connector'' = ''printnet'', "
                    + "''port''=''{2,number,#}'', ''hostName'' = ''{1}'', ''sink.parallelism''=''{3}'')\n"
                    + "AS SELECT * FROM {0}";
    public static final int PORT = 7125;

    private final String statement;

    public WatchStatementExplainer(String statement) {
        this.statement = statement;
    }

    public String getTableName() {
        Matcher matcher = PATTERN.matcher(statement);
        matcher.find();
        return matcher.group(1);
    }

    public String getCreateStatement(String tableName) {
        Optional<InetAddress> address = getSystemLocalIp();
        String ip = address.isPresent() ? address.get().getHostAddress() : "127.0.0.1";
        return MessageFormat.format(CREATE_SQL_TEMPLATE, tableName, ip, PORT, 1);
    }


    private static Optional<InetAddress> getSystemLocalIp() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return Optional.of(ip);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
