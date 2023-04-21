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

/** */
public class Parameter {
    /** 参数键 */
    private String key;
    /** 参数值 */
    private Object value;
    /** 参数名称 */
    private String name;
    /** 参数描述 */
    private String description;
    /** 参数是否可选 */
    private boolean optional;

    public Parameter(String key) {
        this(key, null);
    }

    public Parameter(String key, Object value) {
        this(key, value, false);
    }

    public Parameter(String key, boolean optional) {
        this(key, null, optional);
    }

    public Parameter(String key, Object value, boolean optional) {
        this.key = key;
        this.value = value;
        this.optional = optional;
    }

    // region g/s

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }
    // endregion
}
