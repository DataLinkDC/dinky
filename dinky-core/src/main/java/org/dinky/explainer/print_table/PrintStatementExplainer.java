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

package org.dinky.explainer.print_table;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintStatementExplainer {

    public static final String PATTERN_STR = "PRINT (.+)";
    public static final Pattern PATTERN = Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE);

    public static final String CREATE_SQL_TEMPLATE = "CREATE TABLE print_{0} WITH (''connector'' = ''printnet'', "
            + "''port''=''{2,number,#}'', ''hostName'' = ''{1}'')\n"
            + "AS SELECT * FROM {0}";
    public static final int DEFAULT_PORT = 7125;

    public static String[] getTableNames(String statement) {
        return splitTableNames(statement);
    }

    public static String[] splitTableNames(String statement) {
        Matcher matcher = PATTERN.matcher(statement);
        if (matcher.find()) {
            String tableNames = matcher.group(1);
            return tableNames.replace(" ", "").split(",");
        }
        throw new IllegalArgumentException("Invalid print statement: " + statement);
    }

    public static String getCreateStatement(String tableName, String localIp, Integer localPort) {
        String ip = Strings.isNullOrEmpty(localIp)
                ? getSystemLocalIp().map(InetAddress::getHostAddress).orElse("127.0.0.1")
                : localIp;
        int port = localPort == null ? DEFAULT_PORT : localPort;
        return MessageFormat.format(CREATE_SQL_TEMPLATE, tableName, ip, port);
    }

    private static Optional<InetAddress> getSystemLocalIp() {
        try {
            return Optional.of(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            log.error("get local ip failed: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
