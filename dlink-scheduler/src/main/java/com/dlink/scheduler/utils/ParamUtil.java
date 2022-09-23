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

package com.dlink.scheduler.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 郑文豪
 */
public class ParamUtil {

    /**
     * 封装分页查询
     *
     * @return {@link Map}
     * @author 郑文豪
     * @date 2022/9/7 16:57
     */
    public static Map<String, Object> getPageParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", 1);
        params.put("pageSize", Integer.MAX_VALUE);
        return params;
    }

    /**
     * 封装分页查询
     *
     * @param name 查询条件
     * @return {@link Map}
     * @author 郑文豪
     * @date 2022/9/7 16:58
     */
    public static Map<String, Object> getPageParams(String name) {
        Map<String, Object> params = getPageParams();
        params.put("searchVal", name);
        return params;
    }
}
