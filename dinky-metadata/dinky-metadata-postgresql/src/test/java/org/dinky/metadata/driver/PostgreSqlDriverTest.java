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

package org.dinky.metadata.driver;

import static org.junit.jupiter.api.Assertions.*;

import org.dinky.data.model.Column;
import org.dinky.data.model.Table;
import org.dinky.data.types.DataTypes;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostgreSqlDriverTest {

    private Table table;

    @BeforeEach
    void setUp() {
        List<Column> columns = new ArrayList<>();
        columns.add(Column.builder()
                .name("id")
                .type("BIGINT")
                .dataType(DataTypes.BIGINT.toColumnType(false, 10))
                .keyFlag(true)
                .comment("用户id")
                .build());
        columns.add(Column.builder()
                .name("username")
                .type("VARCHAR")
                .length(255)
                .dataType(DataTypes.STRING.toColumnType(true, 50))
                .comment("用户名")
                .build());
        columns.add(Column.builder()
                .name("password")
                .type("VARCHAR")
                .length(255)
                .dataType(DataTypes.STRING.toColumnType(true, 50))
                .comment("密码")
                .build());
        columns.add(Column.builder()
                .name("email")
                .type("VARCHAR")
                .length(255)
                .dataType(DataTypes.STRING.toColumnType(true, 50))
                .comment("邮箱")
                .build());
        columns.add(Column.builder()
                .name("phone")
                .type("VARCHAR")
                .length(20)
                .dataType(DataTypes.STRING.toColumnType(true, 50))
                .comment("电话号码")
                .build());
        columns.add(Column.builder()
                .name("age")
                .type("INT")
                .dataType(DataTypes.INT.toColumnType(true, 10))
                .isNullable(true)
                .comment("年龄")
                .build());
        columns.add(Column.builder()
                .name("gender")
                .type("BOOLEAN")
                .dataType(DataTypes.BOOLEAN.toColumnType(true, 1))
                .isNullable(true)
                .comment("性别")
                .build());
        columns.add(Column.builder()
                .name("height")
                .type("DECIMAL")
                .precision(5)
                .scale(2)
                .dataType(DataTypes.DECIMAL.toColumnType(true, 7, 5, 2))
                .isNullable(true)
                .comment("身高")
                .build());
        columns.add(Column.builder()
                .name("birthday")
                .type("DATE")
                .dataType(DataTypes.DATE.toColumnType(true, 50))
                .isNullable(true)
                .comment("生日")
                .build());
        columns.add(Column.builder()
                .name("register_time")
                .type("TIMESTAMP")
                .dataType(DataTypes.TIMESTAMP.toColumnType(true, 50))
                .comment("注册时间")
                .build());

        table = new Table("user", "public", columns);
        table.setComment("用户表");
    }

    @Test
    void getCreateTableSql() {

        PostgreSqlDriver postgreSqlDriver = new PostgreSqlDriver();
        String tableDDL = postgreSqlDriver.getCreateTableSql(table);
        String expect = "CREATE TABLE \"public\".\"user\" (\n"
                + "\t\"id\" BIGINT NOT NULL,\n"
                + "\t\"username\" VARCHAR(255) NOT NULL,\n"
                + "\t\"password\" VARCHAR(255) NOT NULL,\n"
                + "\t\"email\" VARCHAR(255) NOT NULL,\n"
                + "\t\"phone\" VARCHAR(20) NOT NULL,\n"
                + "\t\"age\" INT,\n"
                + "\t\"gender\" BOOLEAN,\n"
                + "\t\"height\" DECIMAL(5,2),\n"
                + "\t\"birthday\" DATE,\n"
                + "\t\"register_time\" TIMESTAMP NOT NULL, \n"
                + "\tPRIMARY KEY (\"id\")\n"
                + ");\n"
                + "COMMENT ON TABLE \"public\".\"user\" IS '用户表';\n"
                + "COMMENT ON COLUMN \"public\".\"user\".\"id\" IS '用户id';\n"
                + "COMMENT ON COLUMN \"public\".\"user\".\"username\" IS '用户名';\n"
                + "COMMENT ON COLUMN \"public\".\"user\".\"password\" IS '密码';\n"
                + "COMMENT ON COLUMN \"public\".\"user\".\"email\" IS '邮箱';\n"
                + "COMMENT ON COLUMN \"public\".\"user\".\"phone\" IS '电话号码';\n"
                + "COMMENT ON COLUMN \"public\".\"user\".\"age\" IS '年龄';\n"
                + "COMMENT ON COLUMN \"public\".\"user\".\"gender\" IS '性别';\n"
                + "COMMENT ON COLUMN \"public\".\"user\".\"height\" IS '身高';\n"
                + "COMMENT ON COLUMN \"public\".\"user\".\"birthday\" IS '生日';\n"
                + "COMMENT ON COLUMN \"public\".\"user\".\"register_time\" IS '注册时间';\n";
        assertEquals(expect, tableDDL);
    }
}
