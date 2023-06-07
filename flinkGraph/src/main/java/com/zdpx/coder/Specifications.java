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

package com.zdpx.coder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.zdpx.coder.operator.Column;
import com.zdpx.coder.operator.FieldFunction;

/**
 * Shared constants for a code generation specification
 *
 * @author licho
 */
public final class Specifications {
    private static final Logger logger = LoggerFactory.getLogger(Specifications.class);

    public static final String PACKAGE_NAME = Specifications.class.getPackage().getName();
    public static final String ENV = "env";
    public static final String TABLE_ENV = "tableEnv";
    public static final String EXECUTE_SQL = "$L.executeSql($S)";
    public static final String TABLE_NAME = "tableName";

    public static final ClassName EXCEPTION = ClassName.get("java.lang", "Exception");
    public static final ClassName STRING = ClassName.get("java.lang", "String");
    public static final ClassName SEE =
            ClassName.get(
                    "org.apache.flink.streaming.api.environment", "StreamExecutionEnvironment");

    public static final ClassName STE =
            ClassName.get("org.apache.flink.table.api.bridge.java", "StreamTableEnvironment");

    public static final String COM_ZDPX_CJPG = "zdpx.coder";

    public static final ClassName MAP_STATE_DESCRIPTOR =
            ClassName.get("org.apache.flink.api.common.state", "MapStateDescriptor");

    public static final ClassName BROADCAST_CONNECTED_STREAM =
            ClassName.get("org.apache.flink.streaming.api.datastream", "BroadcastConnectedStream");

    public static final ClassName TYPES =
            ClassName.get("org.apache.flink.api.common.typeinfo", "Types");

    public static final ClassName BROADCAST_PROCESS_FUNCTION =
            ClassName.get(
                    "org.apache.flink.streaming.api.functions.co", "BroadcastProcessFunction");

    public static final ClassName SINGLE_OUTPUT_STREAM_OPERATOR =
            ClassName.get(
                    "org.apache.flink.streaming.api.datastream", "SingleOutputStreamOperator");

    public static final ClassName ROW = ClassName.get("org.apache.flink.types", "Row");

    public static final ClassName VOID = ClassName.get("java.lang", "Void");

    public static final ClassName DATA_STREAM =
            ClassName.get("org.apache.flink.streaming.api.datastream", "DataStream");

    public static final ClassName READ_ONLY_BROADCAST_STATE =
            ClassName.get("org.apache.flink.api.common.state", "ReadOnlyBroadcastState");

    public static final ClassName COLLECTOR = ClassName.get("org.apache.flink.util", "Collector");

    public static final TypeName DATA_STREAM_ROW = ParameterizedTypeName.get(DATA_STREAM, ROW);

    public static final ClassName TABLE = ClassName.get("org.apache.flink.table.api", "Table");
    /** freemarker 定义文件 */
    public static final String TEMPLATE_FILE = "selectFunction.ftlh";

    public static final ClassName RUNTIME_EXECUTION_MODE =
            ClassName.get("org.apache.flink.api.common", "RuntimeExecutionMode");

    private Specifications() {}

    /**
     * 根据字段的配置, 生成待输出的数据列结构定义, 可用于设置输出端口数据信息
     *
     * @param ffs 字段配置
     * @return 输出数据定义
     */
    public static List<Column> convertFieldFunctionToColumns(List<FieldFunction> ffs) {
        return ffs.stream()
                .map(t -> new Column(t.getOutName(), t.getOutType()))
                .collect(Collectors.toList());
    }

    public static String readSpecializationFileByClassName(String filePathJson) {
        String path =
                Specifications.class
                        .getClassLoader()
                        .getResource("operatorSpecialization")
                        .getPath();
        return readSpecializationFile(path + "/" + filePathJson + ".json");
    }

    public static String readSpecializationFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                contentBuilder.append(sCurrentLine).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
}
