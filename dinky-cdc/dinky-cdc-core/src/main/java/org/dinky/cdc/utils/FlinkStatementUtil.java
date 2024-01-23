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

package org.dinky.cdc.utils;

import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Table;
import org.dinky.utils.SqlUtil;

import org.apache.flink.runtime.util.EnvironmentInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlinkStatementUtil {

    private FlinkStatementUtil() {}

    public static String getCDCInsertSql(Table table, String targetName, String sourceName) {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append("`").append(targetName).append("`");
        sb.append(" SELECT\n");
        for (int i = 0; i < table.getColumns().size(); i++) {
            sb.append("    ");
            if (i > 0) {
                sb.append(",");
            }
            sb.append(String.format("`%s`", table.getColumns().get(i).getName()))
                    .append(" \n");
        }
        sb.append(" FROM `");
        sb.append(sourceName);
        sb.append("`");
        return sb.toString();
    }

    public static String getFlinkDDL(
            Table table,
            String tableName,
            FlinkCDCConfig config,
            String sinkSchemaName,
            String sinkTableName,
            String pkList) {
        StringBuilder sb = new StringBuilder();
        if (Integer.parseInt(EnvironmentInformation.getVersion().split("\\.")[1]) < 13) {
            sb.append("CREATE TABLE  `");
        } else {
            sb.append("CREATE TABLE IF NOT EXISTS `");
        }
        sb.append(tableName);
        sb.append("` (\n");
        List<String> pks = new ArrayList<>();
        for (int i = 0; i < table.getColumns().size(); i++) {
            String type = table.getColumns().get(i).getFlinkType();
            sb.append("    ");
            if (i > 0) {
                sb.append(",");
            }
            sb.append("`");
            sb.append(table.getColumns().get(i).getName());
            sb.append("` ");
            sb.append(convertSinkColumnType(type, config));
            sb.append("\n");
            if (table.getColumns().get(i).isKeyFlag()) {
                pks.add(table.getColumns().get(i).getName());
            }
        }
        StringBuilder pksb = new StringBuilder("PRIMARY KEY ( ");
        for (int i = 0; i < pks.size(); i++) {
            if (i > 0) {
                pksb.append(",");
            }
            pksb.append("`");
            pksb.append(pks.get(i));
            pksb.append("`");
        }
        pksb.append(" ) NOT ENFORCED\n");
        if (!pks.isEmpty()) {
            sb.append("    ,");
            sb.append(pksb);
        }
        sb.append(") WITH (\n");
        sb.append(getSinkConfigurationString(config, sinkSchemaName, sinkTableName, pkList));
        sb.append(")\n");
        return sb.toString();
    }

    public static String getCreateCatalogStatement(FlinkCDCConfig config) {
        String catalogName = config.getSink().get("catalog.name");
        List<String> catalogParamKeys = config.getSink().keySet().stream()
                .filter(s -> s.startsWith("catalog."))
                .collect(Collectors.toList());
        StringBuilder sb = new StringBuilder("CREATE CATALOG ");
        sb.append(catalogName);
        sb.append(" WITH (\n");
        catalogParamKeys.remove("catalog.name");
        for (int i = 0; i < catalogParamKeys.size(); i++) {
            sb.append("    ");
            if (i > 0) {
                sb.append(",");
            }
            sb.append("'")
                    .append(catalogParamKeys.get(i).replace("catalog.", ""))
                    .append("' = '")
                    .append(config.getSink().get(catalogParamKeys.get(i)))
                    .append("' \n");
        }
        sb.append(")\n");
        return sb.toString();
    }

    private static String convertSinkColumnType(String type, FlinkCDCConfig config) {
        if (config.getSink().get("connector").equals("hudi") && (type.equals("TIMESTAMP"))) {
            return "TIMESTAMP(3)";
        }
        return type;
    }

    private static String getSinkConfigurationString(
            FlinkCDCConfig config, String sinkSchemaName, String sinkTableName, String pkList) {
        String configurationString =
                SqlUtil.replaceAllParam(config.getSinkConfigurationString(), "schemaName", sinkSchemaName);
        configurationString = SqlUtil.replaceAllParam(configurationString, "tableName", sinkTableName);
        if (configurationString.contains("#{pkList}")) {
            configurationString = SqlUtil.replaceAllParam(configurationString, "pkList", pkList);
        }
        return configurationString;
    }
}
