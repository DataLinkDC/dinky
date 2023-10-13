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

import lombok.Getter;
import lombok.Setter;

/**
 * FlinkColumn
 *
 * @since 2022/7/18 19:55
 */
@Getter
@Setter
public class FlinkColumn implements Serializable {

    private static final long serialVersionUID = 4820196727157711974L;

    private int position;
    private String name;
    private String type;
    private Boolean key;
    private Boolean nullable;
    private String extras;
    private String watermark;
    private String comment;

    public FlinkColumn() {}

    public FlinkColumn(
            int position,
            String name,
            String type,
            boolean key,
            boolean nullable,
            String extras,
            String watermark,
            String comment) {
        this.position = position;
        this.name = name;
        this.type = type;
        this.key = key;
        this.nullable = nullable;
        this.extras = extras;
        this.watermark = watermark;
        this.comment = comment;
    }

    public static FlinkColumn build(
            int position,
            String name,
            String type,
            boolean key,
            boolean nullable,
            String extras,
            String watermark,
            String comment) {
        return new FlinkColumn(position, name, type, key, nullable, extras, watermark, comment);
    }
}
