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

import com.dlink.metadata.constant.SqlServerConstant;
import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.convert.SqlServerTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.SqlServerQuery;
import com.dlink.model.Column;
import com.dlink.model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlServerDriver extends AbstractJdbcDriver {
    @Override
    public IDBQuery getDBQuery() {
        return new SqlServerQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new SqlServerTypeConvert();
    }

    @Override
    String getDriverClass() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    public String getType() {
        return "SqlServer";
    }

    @Override
    public String getName() {
        return "SqlServer数据库";
    }

    @Override
    public String getCreateTableSql(Table table) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE [" + table.getName() + "] (");
        List<Column> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("[" + columns.get(i).getName() + "]" + getTypeConvert().convertToDB(columns.get(i)));
            if (columns.get(i).isNullable()) {
                sb.append(" NOT NULL");
            } else {
                sb.append(" NULL");
            }
        }
        List<String> pks = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).isKeyFlag()) {
                pks.add(columns.get(i).getName());
            }
        }
        if (pks.size() > 0) {
            sb.append(", PRIMARY KEY ( ");
            for (int i = 0; i < pks.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append("[" + pks.get(i) + "]");
            }
            sb.append(" ) ");
        }
        sb.append(") GO ");
        for (Column column : columns) {
            String comment = column.getComment();
            if (comment != null && !comment.isEmpty()) {
                sb.append(String.format(SqlServerConstant.COMMENT_SQL, comment, table.getSchema() == null || table.getSchema().isEmpty() ? "dbo" : table.getSchema(),
                        table.getName(), column.getName()) + " GO ");
            }
        }
        return sb.toString();
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        return new HashMap<>();
    }
}
