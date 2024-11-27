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

package org.dinky.service.catalogue.factory;

import org.dinky.data.constant.CatalogueSortConstant;
import org.dinky.data.exception.BusException;
import org.dinky.service.catalogue.strategy.CatalogueTreeSortStrategy;
import org.dinky.utils.Safes;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * CatalogueTreeSortFactory
 *
 * @since 2024/4/29 14:19
 */
@Slf4j
@Component
public class CatalogueTreeSortFactory {

    @Autowired
    private Map<String, CatalogueTreeSortStrategy> catalogueTreeSortStrategyMap;

    /**
     * Get CatalogueTreeSortStrategy.
     *
     * @param strategyName Strategy Name.
     * @return {@link CatalogueTreeSortStrategy} CatalogueTreeSortStrategy.
     */
    public CatalogueTreeSortStrategy getStrategy(String strategyName) {
        CatalogueTreeSortStrategy catalogueTreeSortStrategy =
                Safes.of(catalogueTreeSortStrategyMap).get(strategyName);
        if (Objects.isNull(catalogueTreeSortStrategy)) {
            catalogueTreeSortStrategy =
                    Safes.of(catalogueTreeSortStrategyMap).get(CatalogueSortConstant.STRATEGY_DEFAULT);
        }
        if (Objects.isNull(catalogueTreeSortStrategy)) {
            throw new BusException(StrUtil.format("Strategy {} is not defined.", strategyName));
        }
        return catalogueTreeSortStrategy;
    }
}
