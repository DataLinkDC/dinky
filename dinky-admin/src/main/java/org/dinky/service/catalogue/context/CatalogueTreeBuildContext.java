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

package org.dinky.service.catalogue.context;

import org.dinky.data.model.Catalogue;
import org.dinky.data.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollectionUtil;

public class CatalogueTreeBuildContext {
    private final Map<Integer, List<Catalogue>> childMap;
    private final Map<Integer, Task> taskMap;

    public CatalogueTreeBuildContext(Map<Integer, List<Catalogue>> childMap, Map<Integer, Task> taskMap) {
        this.childMap = childMap;
        this.taskMap = taskMap;
    }

    public List<Catalogue> getChildren(Integer parentId) {
        List<Catalogue> children = childMap.get(parentId);
        if (CollectionUtil.isEmpty(children)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(children);
    }

    public boolean hasChild(Integer parentId) {
        List<Catalogue> children = childMap.get(parentId);
        return CollectionUtil.isNotEmpty(children);
    }

    public Task getTask(Integer taskId) {
        if (taskId == null) {
            return null;
        }
        return taskMap.get(taskId);
    }
}
