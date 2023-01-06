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

package org.dinky.metadata.constant;

/**
 * Phoenix common data types and corresponding codes
 */
public enum PhoenixEnum {

    INTEGER(4), BIGINT(-5), TINYINT(-6), SMALLINT(5), FLOAT(6), DOUBLE(8), DECIMAL(3), BOOLEAN(16), TIME(92), DATE(
            91), TIMESTAMP(93), VARCHAR(12), CHAR(1), BINARY(-2), VARBINARY(-3);

    int dataTypeCode;

    PhoenixEnum(int dataTypeCode) {
        this.dataTypeCode = dataTypeCode;
    }

    public int getDataTypeCode() {
        return dataTypeCode;
    }

    /**
     * 获取数字 对应的数据类型  默认返回VARCHAR（无对应）  ， 传参为空时返回为null
     *
     * @param dataTypeCode
     * @return
     */
    public static PhoenixEnum getDataTypeEnum(Integer dataTypeCode) {
        if (dataTypeCode == null) {
            return null;
        } else {
            for (PhoenixEnum typeEnum : PhoenixEnum.values()) {
                if (dataTypeCode.equals(typeEnum.dataTypeCode)) {
                    return typeEnum;
                }
            }
        }
        return PhoenixEnum.VARCHAR;
    }
}
