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

package org.dinky.explainer.ca;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * TableCANode
 *
 * @author wenmo
 * @since 2021/6/23 11:03
 **/
@Getter
@Setter
public class TableCANode implements Serializable {
    private static final long serialVersionUID = 356665302973181483L;
    private String id;
    private Integer tableId;
    private String name;
    private String title;
    private String value;
    private String type;
    private Integer columnSize;
    //    private Tables tables;
    private List<String> columns;
    private List<TableCANode> children;

    public TableCANode() {
    }

    public TableCANode(String name) {
        this.name = name;
    }

    public TableCANode(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public TableCANode(Integer id, String name, List<String> columns) {
        this.id = id.toString();
        this.name = name;
        this.title = name;
        this.columnSize = columns.size();
        this.columns = columns;
    }
}
