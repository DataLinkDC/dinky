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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.dinky.service.catalogue.strategy.CatalogueTreeSortStrategy;
import org.dinky.service.catalogue.strategy.impl.CatalogueTreeSortCreateTimeStrategy;
import org.dinky.service.catalogue.strategy.impl.CatalogueTreeSortDefaultStrategy;
import org.dinky.service.catalogue.strategy.impl.CatalogueTreeSortFirstLetterStrategy;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.ImmutableMap;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*", "org.w3c.*"})
public class CatalogueTreeSortFactoryTest {

    @Mock
    private Map<String, CatalogueTreeSortStrategy> catalogueTreeSortStrategyMap;

    @InjectMocks
    private CatalogueTreeSortFactory catalogueTreeSortFactoryTest;

    /** mock strategy */
    CatalogueTreeSortDefaultStrategy defaultStrategy = new CatalogueTreeSortDefaultStrategy();

    CatalogueTreeSortCreateTimeStrategy catalogueTreeSortCreateTimeStrategy = new CatalogueTreeSortCreateTimeStrategy();
    CatalogueTreeSortFirstLetterStrategy catalogueTreeSortFirstLetterStrategy =
            new CatalogueTreeSortFirstLetterStrategy();
    Map<String, CatalogueTreeSortStrategy> mockCatalogueTreeSortStrategyMap = ImmutableMap.of(
            "default", defaultStrategy,
            "create_time", catalogueTreeSortCreateTimeStrategy,
            "first_letter", catalogueTreeSortFirstLetterStrategy);

    @Test
    public void getStrategyTest() {
        when(catalogueTreeSortStrategyMap.get(anyString())).thenAnswer(invocationOnMock -> {
            String strategy = invocationOnMock.getArgument(0);
            return mockCatalogueTreeSortStrategyMap.get(strategy);
        });

        CatalogueTreeSortStrategy strategy = catalogueTreeSortFactoryTest.getStrategy("create_time");
        assertEquals(catalogueTreeSortCreateTimeStrategy, strategy);
    }

    @Test
    public void getStrategyTest2() {
        when(catalogueTreeSortStrategyMap.get(anyString())).thenAnswer(invocationOnMock -> {
            String strategy = invocationOnMock.getArgument(0);
            return mockCatalogueTreeSortStrategyMap.get(strategy);
        });

        CatalogueTreeSortStrategy strategy = catalogueTreeSortFactoryTest.getStrategy("first_letter");
        assertEquals(catalogueTreeSortFirstLetterStrategy, strategy);
    }

    @Test
    public void getStrategyIllegalTest() {
        when(catalogueTreeSortStrategyMap.get(anyString())).thenAnswer(invocationOnMock -> {
            String strategy = invocationOnMock.getArgument(0);
            return mockCatalogueTreeSortStrategyMap.get(strategy);
        });

        CatalogueTreeSortStrategy strategy = catalogueTreeSortFactoryTest.getStrategy("xxx");
        assertEquals(defaultStrategy, strategy);
    }

    @Test
    public void getStrategyIllegalTest2() {
        when(catalogueTreeSortStrategyMap.get(anyString())).thenAnswer(invocationOnMock -> {
            String strategy = invocationOnMock.getArgument(0);
            return mockCatalogueTreeSortStrategyMap.get(strategy);
        });

        CatalogueTreeSortStrategy strategy = catalogueTreeSortFactoryTest.getStrategy(null);
        assertEquals(defaultStrategy, strategy);
    }
}
