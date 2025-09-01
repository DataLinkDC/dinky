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

package org.dinky.data.socket;

import java.io.Serializable;

public class CreateTableEventSocketMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Sandbox id, mapping historyId
     */
    private String boxId;

    /**
     * Table Name: default_catalog.default_database.table_name
     */
    private String tableId;

    /**
     * Flink Column
     */
    private Object columns;

    /**
     * Table Type: PRIMARY_KEY_TABLE,APPEND_TABLE,CHANGE_LOG;
     */
    private String tableType;

    public CreateTableEventSocketMessage() {}

    public CreateTableEventSocketMessage(String tableId, Object columns, String tableType) {
        this.tableId = tableId;
        this.columns = columns;
        this.tableType = tableType;
    }

    public CreateTableEventSocketMessage(String boxId, String tableId, Object columns, String tableType) {
        this.boxId = boxId;
        this.tableId = tableId;
        this.columns = columns;
        this.tableType = tableType;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Object getColumns() {
        return columns;
    }

    public void setColumns(Object columns) {
        this.columns = columns;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    @Override
    public String toString() {
        return "CreateTableEventSocketMessage{" + "boxId='"
                + boxId + '\'' + ", tableId='"
                + tableId + '\'' + ", columns="
                + columns + ", tableType='"
                + tableType + '\'' + '}';
    }
}
