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

package com.dlink.scheduler.model;

import com.dlink.scheduler.enums.DataType;
import com.dlink.scheduler.enums.Direct;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Property implements Serializable {

    private static final long serialVersionUID = -4045513703397452451L;
    @ApiModelProperty(value = "key")
    private String prop;

    /**
     * input/output
     */
    private Direct direct;

    /**
     * data type
     */
    private DataType type;

    @ApiModelProperty(value = "value")
    private String value;

}
