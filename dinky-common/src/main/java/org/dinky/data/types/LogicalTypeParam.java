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

package org.dinky.data.types;

public class LogicalTypeParam {

    private Boolean isNullable;
    private Integer length;
    private Integer precision;
    private Integer scale;

    public LogicalTypeParam(Boolean isNullable, Integer length, Integer precision, Integer scale) {
        this.isNullable = isNullable;
        this.length = length;
        this.precision = precision;
        this.scale = scale;
    }

    public Boolean getNullable() {
        return isNullable;
    }

    public void setNullable(Boolean nullable) {
        isNullable = nullable;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public static LogicalTypeParam of(Boolean isNullable, Integer length, Integer precision, Integer scale) {
        return new LogicalTypeParam(isNullable, length, precision, scale);
    }

    public static LogicalTypeParam of(Boolean isNullable, Integer length, Integer precision) {
        return new LogicalTypeParam(isNullable, length, precision, null);
    }

    public static LogicalTypeParam of(Boolean isNullable, Integer length) {
        return new LogicalTypeParam(isNullable, length, null, null);
    }

    public static LogicalTypeParam of(Boolean isNullable) {
        return new LogicalTypeParam(isNullable, null, null, null);
    }
}
