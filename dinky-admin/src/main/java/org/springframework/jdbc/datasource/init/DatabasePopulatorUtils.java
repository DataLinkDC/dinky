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

package org.springframework.jdbc.datasource.init;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.util.Assert;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.db.Db;

/**
 * Utility methods for executing a {@link DatabasePopulator}.
 *
 * @author Juergen Hoeller
 * @author Oliver Gierke
 * @author Sam Brannen
 * @since 3.1
 */
public abstract class DatabasePopulatorUtils {
    private static boolean isInit = true;

    /**
     * Execute the given {@link DatabasePopulator} against the given {@link DataSource}.
     * <p>As of Spring Framework 5.3.11, the {@link Connection} for the supplied
     * {@code DataSource} will be {@linkplain Connection#commit() committed} if
     * it is not configured for {@link Connection#getAutoCommit() auto-commit} and
     * is not {@linkplain DataSourceUtils#isConnectionTransactional transactional}.
     *
     * @param populator  the {@code DatabasePopulator} to execute
     * @param dataSource the {@code DataSource} to execute against
     * @throws DataAccessException if an error occurs, specifically a {@link ScriptException}
     * @see DataSourceUtils#isConnectionTransactional(Connection, DataSource)
     */
    public static void execute(DatabasePopulator populator, DataSource dataSource) throws DataAccessException {
        Assert.notNull(populator, "DatabasePopulator must not be null");
        Assert.notNull(dataSource, "DataSource must not be null");
        try {
            // Override this method and do not run it if the table exists（重写此方法，如果有表存在，则不运行）
            if (isInit && CollUtil.isNotEmpty(Db.use(dataSource).query("show tables"))) {
                return;
            }
            isInit = false;
            Connection connection = DataSourceUtils.getConnection(dataSource);
            try {
                populator.populate(connection);
                if (!connection.getAutoCommit() && !DataSourceUtils.isConnectionTransactional(connection, dataSource)) {
                    connection.commit();
                }
            } finally {
                DataSourceUtils.releaseConnection(connection, dataSource);
            }
        } catch (ScriptException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new UncategorizedScriptException("Failed to execute database script", ex);
        }
    }
}
