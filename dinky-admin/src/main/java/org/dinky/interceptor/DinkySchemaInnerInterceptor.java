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

package org.dinky.interceptor;

import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;

/**
 * MyBatis-Plus interceptor that dynamically prepends a custom PostgreSQL schema prefix to
 * all Dinky business table names (those with the {@code dinky_} prefix).
 *
 * <p>When the configured schema is {@code public} or empty, this interceptor is a no-op,
 * preserving the original behavior of querying tables in the default schema.</p>
 *
 * <p>Using a prefix-based match (dinky_*) instead of an explicit table-name set means
 * any future Dinky table will automatically receive the schema prefix without code changes.</p>
 *
 * <p><b>Interceptor ordering note:</b> Must be registered <em>after</em>
 * {@link com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor}
 * and <em>before</em> {@link com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor}
 * so that tenant's {@code ignoreTable} still matches the original (un-prefixed) table names.</p>
 */
public class DinkySchemaInnerInterceptor extends DynamicTableNameInnerInterceptor {

    /** Only table names starting with this prefix receive the schema prefix. */
    private static final String DINKY_TABLE_PREFIX = "dinky_";

    public DinkySchemaInnerInterceptor(String schema) {
        // schema is empty, null, or "public" → no-op, keep original table names
        if (schema == null || schema.isEmpty() || "public".equalsIgnoreCase(schema)) {
            return;
        }

        final String prefix = schema + ".";
        TableNameHandler handler = (sql, tableName) -> {
            if (tableName != null && tableName.startsWith(DINKY_TABLE_PREFIX)) {
                return prefix + tableName;
            }
            return tableName;
        };
        setTableNameHandler(handler);
    }
}
