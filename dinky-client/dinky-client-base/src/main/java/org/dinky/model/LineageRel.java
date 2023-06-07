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

package org.dinky.data.model;

/**
 * LineageResult
 *
 * @since 2022/8/20 21:09
 */
public class LineageRel {

    private final String sourceCatalog;

    private final String sourceDatabase;

    private final String sourceTable;

    private final String sourceColumn;

    private final String targetCatalog;

    private final String targetDatabase;

    private final String targetTable;

    private final String targetColumn;

    private static final String DELIMITER = ".";

    public LineageRel(
            String sourceCatalog,
            String sourceDatabase,
            String sourceTable,
            String sourceColumn,
            String targetCatalog,
            String targetDatabase,
            String targetTable,
            String targetColumn) {
        this.sourceCatalog = sourceCatalog;
        this.sourceDatabase = sourceDatabase;
        this.sourceTable = sourceTable;
        this.sourceColumn = sourceColumn;
        this.targetCatalog = targetCatalog;
        this.targetDatabase = targetDatabase;
        this.targetTable = targetTable;
        this.targetColumn = targetColumn;
    }

    public static LineageRel build(
            String sourceTablePath,
            String sourceColumn,
            String targetTablePath,
            String targetColumn) {
        String[] sourceItems = sourceTablePath.split("\\.");
        String[] targetItems = targetTablePath.split("\\.");

        return new LineageRel(
                sourceItems[0],
                sourceItems[1],
                sourceItems[2],
                sourceColumn,
                targetItems[0],
                targetItems[1],
                targetItems[2],
                targetColumn);
    }

    public static LineageRel build(
            String sourceCatalog,
            String sourceDatabase,
            String sourceTable,
            String sourceColumn,
            String targetCatalog,
            String targetDatabase,
            String targetTable,
            String targetColumn) {
        return new LineageRel(
                sourceCatalog,
                sourceDatabase,
                sourceTable,
                sourceColumn,
                targetCatalog,
                targetDatabase,
                targetTable,
                targetColumn);
    }

    public String getSourceCatalog() {
        return sourceCatalog;
    }

    public String getSourceDatabase() {
        return sourceDatabase;
    }

    public String getSourceTable() {
        return sourceTable;
    }

    public String getSourceColumn() {
        return sourceColumn;
    }

    public String getTargetCatalog() {
        return targetCatalog;
    }

    public String getTargetDatabase() {
        return targetDatabase;
    }

    public String getTargetTable() {
        return targetTable;
    }

    public String getTargetColumn() {
        return targetColumn;
    }

    public String getSourceTablePath() {
        return sourceCatalog + DELIMITER + sourceDatabase + DELIMITER + sourceTable;
    }

    public String getTargetTablePath() {
        return targetCatalog + DELIMITER + targetDatabase + DELIMITER + targetTable;
    }
}
