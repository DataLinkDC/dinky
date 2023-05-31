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

package org.dinky.context;

import org.dinky.function.constant.PathConstant;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;

public class GitBuildContextHolder {
    private static final List<Integer> RUNNING_LIST = new ArrayList<>();

    public static void addRun(Integer id) {
        RUNNING_LIST.add(id);
        FileUtil.writeUtf8String(
                JSONUtil.toJsonStr(getAll()), PathConstant.TMP_PATH + "/build.list");
    }

    public static void remove(Integer id) {
        RUNNING_LIST.remove(id);
    }

    public static List<Integer> getAll() {
        return new ArrayList<>(RUNNING_LIST);
    }
}
