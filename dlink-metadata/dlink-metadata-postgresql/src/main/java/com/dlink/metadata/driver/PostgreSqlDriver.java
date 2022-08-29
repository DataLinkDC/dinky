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

package com.dlink.metadata.driver;

import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.convert.PostgreSqlTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.PostgreSqlQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * PostgreSqlDriver
 *
 * @author wenmo
 * @since 2021/7/22 9:28
 **/
public class PostgreSqlDriver extends AbstractJdbcDriver {
    @Override
    String getDriverClass() {
        return "org.postgresql.Driver";
    }

    @Override
    public IDBQuery getDBQuery() {
        return new PostgreSqlQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new PostgreSqlTypeConvert();
    }

    @Override
    public String getType() {
        return "PostgreSql";
    }

    @Override
    public String getName() {
        return "PostgreSql 数据库";
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        return new HashMap<>();
    }

    @Override
    public String generateCreateSchemaSql(String schemaName) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE SCHEMA ").append(schemaName);
        return sb.toString();
    }
}
