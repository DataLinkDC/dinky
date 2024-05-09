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

import org.dinky.data.constant.CatalogueSortConstant;
import org.dinky.data.enums.SortTypeEnum;
import org.dinky.data.model.Catalogue;
import org.dinky.service.catalogue.strategy.CatalogueTreeSortStrategy;
import org.dinky.utils.Safes;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * CatalogueTreeSortCreateTimeStrategy
 *
 * @since 2024/4/29 14:13
 */
@Slf4j
@Component(CatalogueSortConstant.STRATEGY_CREATE_TIME)
public class CatalogueTreeSortCreateTimeStrategy implements CatalogueTreeSortStrategy {

    /**
     * Sort catalogue tree based on creation time.
     *
     * @param catalogueTree Catalogue Tree.
     * @param sortTypeEnum  Sort type.
     * @return A list of {@link Catalogue} Sorted catalogue tree.
     */
    @Override
    public List<Catalogue> sort(List<Catalogue> catalogueTree, SortTypeEnum sortTypeEnum) {
        log.debug(
                "sort catalogue tree based on creation time. catalogueTree: {}, sortTypeEnum: {}",
                catalogueTree,
                sortTypeEnum);
        return recursionSortCatalogues(catalogueTree, sortTypeEnum);
    }

    public List<Catalogue> recursionSortCatalogues(List<Catalogue> catalogueTree, SortTypeEnum sortTypeEnum) {
        List<Catalogue> catalogueList = Safes.of(catalogueTree).stream()
                .sorted((o1, o2) -> sortTypeEnum.compare(o1.getCreateTime(), o2.getCreateTime()))
                .collect(Collectors.toList());

        for (Catalogue catalogue : catalogueList) {
            List<Catalogue> children = catalogue.getChildren();
            if (CollectionUtil.isEmpty(children)) {
                continue;
            }
            List<Catalogue> sortedChildren = recursionSortCatalogues(children, sortTypeEnum);
            catalogue.setChildren(sortedChildren);
        }
        return catalogueList;
    }
}
