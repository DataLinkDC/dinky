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

package org.dinky.utils;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.runtime.util.EnvironmentInformation;
import org.dinky.constant.FlinkParamConstant;
import org.dinky.model.Column;
import org.dinky.model.ColumnType;
import org.dinky.model.FlinkCDCConfig;
import org.dinky.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FlinkBaseUtil
 *
 * @author wenmo
 * @since 2022/3/9 19:15
 */
public class FlinkBaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(FlinkBaseUtil.class);

    public static Map<String, String> getParamsFromArgs(String[] args) {
        Map<String, String> params = new HashMap<>();
        ParameterTool parameters = ParameterTool.fromArgs(args);
        params.put(FlinkParamConstant.ID, parameters.get(FlinkParamConstant.ID, null));
        params.put(FlinkParamConstant.DRIVER, parameters.get(FlinkParamConstant.DRIVER, null));
        params.put(FlinkParamConstant.URL, parameters.get(FlinkParamConstant.URL, null));
        params.put(FlinkParamConstant.USERNAME, parameters.get(FlinkParamConstant.USERNAME, null));
        params.put(FlinkParamConstant.PASSWORD, parameters.get(FlinkParamConstant.PASSWORD, null));
        params.put(
                FlinkParamConstant.DINKY_ADDR, parameters.get(FlinkParamConstant.DINKY_ADDR, null));
        return params;
    }

    public static String getCDCSqlInsert(
            Table table, String targetName, String sourceName, FlinkCDCConfig config) {
        StringBuilder sb = new StringBuilder("INSERT INTO `");
        sb.append(targetName);
        sb.append("` SELECT\n");
        for (int i = 0; i < table.getColumns().size(); i++) {
            sb.append("    ");
            if (i > 0) {
                sb.append(",");
            }
            sb.append(getColumnProcessing(table.getColumns().get(i), config)).append(" \n");
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
        if (pks.size() > 0) {
            sb.append("    ,");
            sb.append(pksb);
        }
        sb.append(") WITH (\n");
        sb.append(getSinkConfigurationString(table, config, sinkSchemaName, sinkTableName, pkList));
        sb.append(")\n");
        return sb.toString();
    }

    public static String getSinkConfigurationString(
            Table table,
            FlinkCDCConfig config,
            String sinkSchemaName,
            String sinkTableName,
            String pkList) {
        String configurationString =
                SqlUtil.replaceAllParam(
                        config.getSinkConfigurationString(), "schemaName", sinkSchemaName);
        configurationString =
                SqlUtil.replaceAllParam(configurationString, "tableName", sinkTableName);
        if (configurationString.contains("${pkList}")) {
            configurationString = SqlUtil.replaceAllParam(configurationString, "pkList", pkList);
        }
        return configurationString;
    }

    public static String convertSinkColumnType(String type, FlinkCDCConfig config) {
        if (config.getSink().get("connector").equals("hudi")) {
            if (type.equals("TIMESTAMP")) {
                return "TIMESTAMP(3)";
            }
        }
        return type;
    }

    public static String getColumnProcessing(Column column, FlinkCDCConfig config) {
        if ("true".equals(config.getSink().get(FlinkCDCConfig.COLUMN_REPLACE_LINE_BREAK))
                && ColumnType.STRING.equals(column.getJavaType())) {
            return String.format(
                    "REGEXP_REPLACE(`%s`, '\\n', '') AS `%s`", column.getName(), column.getName());
        } else {
            return String.format("`%s`", column.getName());
        }
    }
}
