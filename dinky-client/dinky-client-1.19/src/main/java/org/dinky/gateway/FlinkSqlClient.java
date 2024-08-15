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

import org.dinky.utils.CloseUtil;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.plugin.PluginUtils;
import org.apache.flink.table.client.SqlClientException;
import org.apache.flink.table.client.cli.CliClient;
import org.apache.flink.table.client.gateway.Executor;
import org.apache.flink.table.client.gateway.SingleSessionManager;
import org.apache.flink.table.gateway.SqlGateway;
import org.apache.flink.table.gateway.api.endpoint.SqlGatewayEndpointFactoryUtils;
import org.apache.flink.table.gateway.rest.SqlGatewayRestEndpointFactory;
import org.apache.flink.table.gateway.rest.util.SqlGatewayRestOptions;
import org.apache.flink.table.gateway.service.context.DefaultContext;
import org.apache.flink.util.NetUtils;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Supplier;

import org.jline.terminal.Terminal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FlinkSqlClient implements Closeable, ISqlClient {

    public static final String CLI_NAME = "Dinky Flink SQL WEB CLI";

    private final Supplier<Terminal> terminalFactory;
    private final SqlClientOptions sqlClientOptions;

    private Executor executor;
    private EmbeddedGateway embeddedGateway;

    public FlinkSqlClient(SqlClientOptions sqlClientOptions, Supplier<Terminal> terminalFactory) {
        this.sqlClientOptions = sqlClientOptions;
        this.terminalFactory = terminalFactory;
    }

    public void startCli(Configuration configuration) {
        InetSocketAddress inetSocketAddress;
        DefaultContext context = createDefaultContext(configuration);
        if (sqlClientOptions.getMode() == SqlCliMode.MODE_EMBEDDED) {
            embeddedGateway = EmbeddedGateway.create(context);
            inetSocketAddress =
                    InetSocketAddress.createUnresolved(embeddedGateway.getAddress(), embeddedGateway.getPort());
        } else if (sqlClientOptions.getMode() == SqlCliMode.MODE_GATEWAY) {
            inetSocketAddress = sqlClientOptions
                    .buildConnectAddress()
                    .orElseThrow(() -> new SqlClientException("Gateway address is not set."));
        } else {
            throw new SqlClientException("Unsupported mode: " + sqlClientOptions.getMode());
        }

        executor = Executor.create(context, inetSocketAddress, sqlClientOptions.getSessionId());

        Path historyFilePath = Paths.get(sqlClientOptions.getHistoryFilePath());
        try (CliClient cli = new CliClient(terminalFactory, executor, historyFilePath)) {
            if (sqlClientOptions.getInitSql() != null) {
                boolean success = cli.executeInitialization(sqlClientOptions.getInitSql());
            }
            cli.executeInInteractiveMode();
        } finally {
            close();
        }
        log.info("Sql Client exit : " + sqlClientOptions.getConnectAddress());
    }

    private DefaultContext createDefaultContext(Configuration configuration) {
        FileSystem.initialize(configuration, PluginUtils.createPluginManagerFromRootFolder(configuration));
        return new DefaultContext(configuration, new ArrayList<>());
    }

    public void close() {
        CloseUtil.closeNoErrorPrint(executor, embeddedGateway);
    }

    // --------------------------------------------------------------------------------------------

    private static class EmbeddedGateway implements Closeable {

        private static final String ADDRESS = "localhost";

        private final NetUtils.Port port;
        private final SqlGateway sqlGateway;

        public static EmbeddedGateway create(DefaultContext defaultContext) {
            NetUtils.Port port = NetUtils.getAvailablePort();

            Configuration defaultConfig = defaultContext.getFlinkConfig();
            Configuration restConfig = new Configuration();
            // always use localhost
            restConfig.set(SqlGatewayRestOptions.ADDRESS, ADDRESS);
            restConfig.set(SqlGatewayRestOptions.BIND_ADDRESS, ADDRESS);
            restConfig.set(SqlGatewayRestOptions.PORT, port.getPort());
            restConfig.set(SqlGatewayRestOptions.BIND_PORT, port.getPort() + "");
            defaultConfig.addAll(
                    restConfig,
                    SqlGatewayEndpointFactoryUtils.getSqlGatewayOptionPrefix(SqlGatewayRestEndpointFactory.IDENTIFIER));
            SqlGateway sqlGateway = new SqlGateway(defaultConfig, new SingleSessionManager(defaultContext));
            try {
                sqlGateway.start();
                log.info("Start embedded gateway on port {}", port.getPort());
            } catch (Throwable t) {
                closePort(port);
                throw new SqlClientException("Failed to start the embedded sql-gateway.", t);
            }

            return new EmbeddedGateway(sqlGateway, port);
        }

        private EmbeddedGateway(SqlGateway sqlGateway, NetUtils.Port port) {
            this.sqlGateway = sqlGateway;
            this.port = port;
        }

        String getAddress() {
            return ADDRESS;
        }

        int getPort() {
            return port.getPort();
        }

        @Override
        public void close() {
            sqlGateway.stop();
            closePort(port);
        }

        private static void closePort(NetUtils.Port port) {
            try {
                port.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
