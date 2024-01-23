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

package org.dinky.sql;

import org.dinky.data.model.SystemConfiguration;

/**
 * FlinkQuery
 *
 * @since 2022/7/18 18:43
 */
public class FlinkQuery {

    public static String separator() {
        return SystemConfiguration.getInstances().getSqlSeparator();
    }

    public static String defaultCatalog() {
        return "default_catalog";
    }

    public static String defaultDatabase() {
        return "default_database";
    }

    public static String showCatalogs() {
        return "SHOW CATALOGS";
    }

    public static String useCatalog(String catalog) {
        return String.format("USE CATALOG %s", catalog);
    }

    public static String showDatabases() {
        return "SHOW DATABASES";
    }

    public static String useDatabase(String database) {
        return String.format("USE %s", database);
    }

    public static String showTables() {
        return "SHOW TABLES";
    }

    public static String showViews() {
        return "SHOW VIEWS";
    }

    public static String showFunctions() {
        return "SHOW FUNCTIONS";
    }

    public static String showUserFunctions() {
        return "SHOW USER FUNCTIONS";
    }

    public static String showModules() {
        return "SHOW MODULES";
    }

    public static String descTable(String table) {
        return String.format("DESC %s", table);
    }

    public static String columnName() {
        return "name";
    }

    public static String columnType() {
        return "type";
    }

    public static String columnNull() {
        return "null";
    }

    public static String columnKey() {
        return "key";
    }

    public static String columnExtras() {
        return "extras";
    }

    public static String columnWatermark() {
        return "watermark";
    }
}
