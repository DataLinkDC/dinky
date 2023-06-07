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

import org.dinky.constant.FlinkParamConstant;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Table;

import org.apache.flink.api.java.utils.ParameterTool;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FlinkBaseUtil
 *
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
}
