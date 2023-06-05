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

package org.dinky.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Catalog
 *
 * @since 2022/7/17 21:37
 */
@Getter
@Setter
public class Catalog implements Serializable {

    private static final long serialVersionUID = -7535759384541414568L;

    private String name;
    private List<Schema> schemas = new ArrayList<>();

    public Catalog() {}

    public Catalog(String name) {
        this.name = name;
    }

    public Catalog(String name, List<Schema> schemas) {
        this.name = name;
        this.schemas = schemas;
    }

    public static Catalog build(String name) {
        return new Catalog(name);
    }
}
