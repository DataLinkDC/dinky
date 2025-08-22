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

package org.dinky.sandbox.socket;

import org.apache.flink.types.Row;

import java.io.Serializable;

/**
 * Socket 消息数据结构
 * 包含表名和 Flink 数据流
 */
public class SocketMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 数据库名（可选）
     */
    private String databaseName;

    /**
     * Flink 数据行
     */
    private Row dataRow;

    /**
     * 时区
     */
    private String timeZone;

    public SocketMessage() {}

    public SocketMessage(String tableName, Row dataRow) {
        this.tableName = tableName;
        this.dataRow = dataRow;
    }

    public SocketMessage(String tableName, String databaseName, Row dataRow, String timeZone) {
        this.tableName = tableName;
        this.databaseName = databaseName;
        this.dataRow = dataRow;
        this.timeZone = timeZone;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public Row getDataRow() {
        return dataRow;
    }

    public void setDataRow(Row dataRow) {
        this.dataRow = dataRow;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Override
    public String toString() {
        return "SocketMessage{" + "tableName='"
                + tableName + '\'' + ", databaseName='"
                + databaseName + '\'' + ", dataRow="
                + dataRow + ", timeZone='"
                + timeZone + '\'' + '}';
    }
}
