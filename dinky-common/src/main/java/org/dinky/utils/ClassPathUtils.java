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

package org.dinky.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

public class ClassPathUtils {
    public static void sort(List<String> list) {
        Collections.sort(list);
        List<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String path = list.get(i);
            if (StrUtil.contains(path, "dinky")) {
                indexList.add(i);
            }
        }
        if (CollUtil.isNotEmpty(indexList)) {
            for (Integer i : indexList) {
                int index = i;
                String path = list.remove(index);
                list.add(0, path);
            }
        }
    }
}
