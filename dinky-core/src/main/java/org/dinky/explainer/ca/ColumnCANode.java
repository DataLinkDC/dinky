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
 * ColumnCANode
 *
 * @author wenmo
 * @since 2021/6/23 11:03
 **/
@Getter
@Setter
public class ColumnCANode implements Serializable {

    private static final long serialVersionUID = 122624200268430762L;
    private Integer id;
    private Integer tableId;
    private String name;
    private String title;
    private String value;
    private String type;
    private String operation;
    private List<ColumnCANode> children;

    public ColumnCANode() {
    }

}
