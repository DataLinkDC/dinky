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

package org.dinky.data.result;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ant Design Pro ProTable Query Result
 *
 * @since 2021/5/18 21:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProTableResult<T> implements Serializable {

    private static final long serialVersionUID = -6377431009117000655L;
    /** 总数 */
    private Long total;
    /** 是否成功：true 成功、false 失败 */
    private boolean success;
    /** 当前页码 */
    private Integer current;
    /** 当前每页记录数 */
    private Integer pageSize;
    /** 当前页结果集 */
    private List<T> data;
}
