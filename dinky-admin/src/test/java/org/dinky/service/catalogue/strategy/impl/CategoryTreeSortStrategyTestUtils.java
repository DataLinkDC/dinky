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

package org.dinky.service.catalogue.strategy.impl;

import org.dinky.data.model.Catalogue;
import org.dinky.utils.Safes;

import java.util.List;

import com.google.common.collect.Lists;

import cn.hutool.core.collection.CollectionUtil;

public class CategoryTreeSortStrategyTestUtils {

    public static List<Integer> breadthTraverse(List<Catalogue> catalogueTree) {
        List<Integer> resultList = Lists.newArrayList();
        for (Catalogue catalogue : Safes.of(catalogueTree)) {
            resultList.add(catalogue.getId());
        }
        for (Catalogue catalogue : Safes.of(catalogueTree)) {
            List<Catalogue> children = catalogue.getChildren();
            if (CollectionUtil.isEmpty(children)) {
                continue;
            }
            resultList.addAll(breadthTraverse(children));
        }
        return resultList;
    }
}
