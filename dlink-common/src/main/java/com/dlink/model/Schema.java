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

package com.dlink.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Schema
 *
 * @author wenmo
 * @since 2021/7/19 23:27
 */
@Getter
@Setter
public class Schema implements Serializable, Comparable<Schema> {

    private static final long serialVersionUID = 4278304357661271040L;

    private String name;
    private List<Table> tables = new ArrayList<>();
    private List<String> views = new ArrayList<>();
    private List<String> functions = new ArrayList<>();
    private List<String> userFunctions = new ArrayList<>();
    private List<String> modules = new ArrayList<>();

    /**
     * 需要保留一个空构造方法，否则序列化有问题
     * */
    public Schema() {
    }

    public Schema(String name) {
        this.name = name;
    }

    public Schema(String name, List<Table> tables) {
        this.name = name;
        this.tables = tables;
    }

    public static Schema build(String name) {
        return new Schema(name);
    }

    @Override
    public int compareTo(Schema o) {
        return this.name.compareTo(o.getName());
    }
}
