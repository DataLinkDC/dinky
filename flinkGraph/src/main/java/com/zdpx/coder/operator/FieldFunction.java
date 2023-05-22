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

package com.zdpx.coder.operator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定义sql语句中每个字段调用的函数, 函数可以嵌套. <b>该函数在freemarker中用于类型判断,所以禁止重命名</b>
 *
 * <p>例: 根据以下配置文件:
 *
 * <pre>{@code
 * "fieldFunctions": [
 *                   {
 *                     "fieldOutName": "type",
 *                     "functionName": "CAST",
 *                     "delimiter": "AS",
 *                     "fieldParameters": [
 *                       "t",
 *                       "STRING"
 *                     ]
 *                   },
 *                   {
 *                     "fieldOutName": "data"
 *                   },
 *                   {
 *                     "fieldOutName": "taskId",
 *                     "functionName": "JSON_VALUE",
 *                     "fieldParameters": [
 *                       "task",
 *                       "'$.taskId'"
 *                     ]
 *                   },
 *                   {
 *                     "fieldOutName": "taskStatus",
 *                     "functionName": "CAST",
 *                     "delimiter": "AS",
 *                     "fieldParameters": [
 *                       {
 *                         "functionName": "JSON_VALUE",
 *                         "fieldParameters": [
 *                           "task",
 *                           "'$.taskStatus'"
 *                         ]
 *                       },
 *                       "INT"
 *                     ]
 *                   },
 *                   {
 *                     "fieldOutName": "proc_time",
 *                     "fieldParameters": [
 *                       "PROCTIME()"
 *                     ]
 *                   }
 *                 ]
 *
 * }</pre>
 *
 * <p>可生成代码:
 *
 * <pre>{@code
 *     SELECT
 *         CAST( t AS STRING ) AS type,
 *         data,
 *         JSON_VALUE( task , '$.taskId' ) AS taskId,
 *         CAST( JSON_VALUE( task , '$.taskStatus' ) AS INT ) AS taskStatus,
 *         PROCTIME() AS proc_time
 *     FROM
 *         _BroadcastResult6
 *         }
 * </code>
 * </pre>
 *
 * @author Licho Sun
 */
public class FieldFunction {

    /** 函数需要的参数, 如果类型为{@link FieldFunction}, 会按照嵌套函数处理, */
    List<Object> parameters = new ArrayList<>();
    /** 字段输入名称,通过<b>AS</b>关键字进行字段重命名 */
    private String outName;
    /** 字段输出类型, 约定(deprecated). */
    private String outType;
    /** 自定义调用的函数名称 */
    private String functionName;
    /** 函数参数分隔符,如内置函数CAST的分隔符可视为<b>AS</b> */
    private String delimiter;

    /**
     * 解析字段处理方法配置, 生成{@link FieldFunction}定义
     *
     * @param fos 字段配置信息
     * @return 方法定义
     */
    static FieldFunction processFieldConfigure(String tableName, Map<String, Object> fos) {
        FieldFunction fo = new FieldFunction();
        fo.setOutName((String) fos.get("outName"));
        fo.setFunctionName((String) fos.get("functionName"));
        fo.setDelimiter((String) fos.get("delimiter"));
        @SuppressWarnings("unchecked")
        List<Object> fieldParameters = (List<Object>) fos.get("parameters");

        if (fieldParameters == null) {
            fo.setOutName(insertTableName(tableName, fo, fo.getOutName()));
            return fo;
        }

        List<Object> result = new ArrayList<>();
        for (Object fieldParameter : fieldParameters) {
            if (fieldParameter instanceof Map) {
                // 表示函数需要递归处理
                @SuppressWarnings("unchecked")
                Map<String, Object> fp = (Map<String, Object>) fieldParameter;
                result.add(processFieldConfigure(tableName, fp));
            } else if (fieldParameter instanceof String) {
                String field = (String) fieldParameter;
                field = insertTableName(tableName, fo, field);
                result.add(field);
            } else {
                result.add(fieldParameter);
            }
        }
        fo.setParameters(result);

        return fo;
    }

    public static String insertTableName(String primaryTableName, FieldFunction fo, String param) {
        if (param.startsWith("@")
                || fo == null
                || fo.getFunctionName() == null
                || fo.getFunctionName().isEmpty()) {
            if (param.startsWith("@")) {
                param = param.substring(1);
            }
            param = primaryTableName + "." + param;
        }
        return param;
    }

    /**
     * 分析字段配置, 转软化为{@link FieldFunction} 形式.
     *
     * @param funcs 字段处理函数配置
     * @return {@link FieldFunction}形式的字段处理定义
     */
    public static List<FieldFunction> analyzeParameters(
            String primaryTableName, List<Map<String, Object>> funcs) {
        List<FieldFunction> fieldFunctions = new ArrayList<>();
        for (Map<String, Object> fos : funcs) {
            FieldFunction fo = processFieldConfigure(primaryTableName, fos);
            fieldFunctions.add(fo);
        }
        return fieldFunctions;
    }

    // region g/s

    public String getOutType() {
        return outType;
    }

    public void setOutType(String outType) {
        this.outType = outType;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public String getOutName() {
        return outName;
    }

    public void setOutName(String outName) {
        this.outName = outName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
    // endregion
}
