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

import static org.junit.jupiter.api.Assertions.*;

import org.dinky.data.enums.SortTypeEnum;
import org.dinky.data.model.Catalogue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*", "org.w3c.*"})
public class CatalogueTreeSortFirstLetterStrategyTest {

    @InjectMocks
    private CatalogueTreeSortFirstLetterStrategy catalogueTreeSortFirstLetterStrategyTest;

    @Test
    public void sortAscTest() {
        SortTypeEnum sortTypeEnum = SortTypeEnum.ASC;
        List<Catalogue> catalogueTree = Lists.newArrayList();
        Catalogue catalogue = new Catalogue();
        catalogue.setId(1);
        catalogue.setName("测试目录");
        catalogue.setCreateTime(LocalDateTime.of(2024, 4, 28, 19, 22, 0));

        Catalogue catalogue11 = new Catalogue();
        catalogue11.setId(2);
        catalogue11.setName("bbb");
        catalogue11.setCreateTime(LocalDateTime.of(2024, 4, 28, 20, 22, 0));
        Catalogue catalogue12 = new Catalogue();
        catalogue12.setId(3);
        catalogue12.setName("aaa");
        catalogue12.setCreateTime(LocalDateTime.of(2024, 4, 28, 21, 22, 0));
        catalogue.setChildren(Lists.newArrayList(catalogue12, catalogue11));

        Catalogue catalogue2 = new Catalogue();
        catalogue2.setId(4);
        catalogue2.setName("test");
        catalogue2.setCreateTime(LocalDateTime.of(2024, 4, 29, 19, 22, 0));

        Catalogue catalogue21 = new Catalogue();
        catalogue21.setId(7);
        catalogue21.setName("测试目录2");
        catalogue21.setCreateTime(LocalDateTime.of(2024, 4, 29, 21, 22, 0));
        Catalogue catalogue22 = new Catalogue();
        catalogue22.setId(6);
        catalogue22.setName("生产任务");
        catalogue22.setCreateTime(LocalDateTime.of(2024, 4, 29, 20, 22, 0));
        catalogue2.setChildren(Lists.newArrayList(catalogue21, catalogue22));

        catalogueTree.add(catalogue2);
        catalogueTree.add(catalogue);

        /*
        input:
        -- 4 (2024-04-29 19:22:00) (test)
            -- 7 (2024-04-29 21:22:00) (测试目录2)
            -- 6 (2024-04-29 20:22:00) (生产任务)
        -- 1 (2024-04-28 19:22:00) (测试目录)
            -- 3 (2024-04-28 21:22:00) (aaa)
            -- 2 (2024-04-28 20:22:00) (bbb)

        output:
        -- 4 (2024-04-29 19:22:00) (test)
            -- 7 (2024-04-29 21:22:00) (测试目录2)
            -- 6 (2024-04-29 20:22:00) (生产任务)
        -- 1 (2024-04-28 19:22:00) (测试目录)
            -- 3 (2024-04-28 21:22:00) (aaa)
            -- 2 (2024-04-28 20:22:00) (bbb)
         */
        List<Catalogue> resultList = catalogueTreeSortFirstLetterStrategyTest.sort(catalogueTree, sortTypeEnum);
        List<Integer> resultIdList = CategoryTreeSortStrategyTestUtils.breadthTraverse(resultList);
        assertEquals(Lists.newArrayList(4, 1, 7, 6, 3, 2), resultIdList);
    }

    @Test
    public void sortDescTest() {
        SortTypeEnum sortTypeEnum = SortTypeEnum.DESC;
        List<Catalogue> catalogueTree = Lists.newArrayList();
        Catalogue catalogue = new Catalogue();
        catalogue.setId(1);
        catalogue.setName("测试目录");
        catalogue.setCreateTime(LocalDateTime.of(2024, 4, 28, 19, 22, 0));

        Catalogue catalogue11 = new Catalogue();
        catalogue11.setId(2);
        catalogue11.setName("bbb");
        catalogue11.setCreateTime(LocalDateTime.of(2024, 4, 28, 20, 22, 0));
        Catalogue catalogue12 = new Catalogue();
        catalogue12.setId(3);
        catalogue12.setName("aaa");
        catalogue12.setCreateTime(LocalDateTime.of(2024, 4, 28, 21, 22, 0));
        catalogue.setChildren(Lists.newArrayList(catalogue12, catalogue11));

        Catalogue catalogue2 = new Catalogue();
        catalogue2.setId(4);
        catalogue2.setName("test");
        catalogue2.setCreateTime(LocalDateTime.of(2024, 4, 29, 19, 22, 0));

        Catalogue catalogue21 = new Catalogue();
        catalogue21.setId(7);
        catalogue21.setName("测试目录2");
        catalogue21.setCreateTime(LocalDateTime.of(2024, 4, 29, 21, 22, 0));
        Catalogue catalogue22 = new Catalogue();
        catalogue22.setId(6);
        catalogue22.setName("生产任务");
        catalogue22.setCreateTime(LocalDateTime.of(2024, 4, 29, 20, 22, 0));
        catalogue2.setChildren(Lists.newArrayList(catalogue21, catalogue22));

        catalogueTree.add(catalogue2);
        catalogueTree.add(catalogue);

        /*
        input:
        -- 4 (2024-04-29 19:22:00) (test)
            -- 7 (2024-04-29 21:22:00) (测试目录2)
            -- 6 (2024-04-29 20:22:00) (生产任务)
        -- 1 (2024-04-28 19:22:00) (测试目录)
            -- 3 (2024-04-28 21:22:00) (aaa)
            -- 2 (2024-04-28 20:22:00) (bbb)

        output:
        -- 1 (2024-04-28 19:22:00) (测试目录)
            -- 2 (2024-04-28 20:22:00) (bbb)
            -- 3 (2024-04-28 21:22:00) (aaa)
        -- 4 (2024-04-29 19:22:00) (test)
            -- 6 (2024-04-29 20:22:00) (生产任务)
            -- 7 (2024-04-29 21:22:00) (测试目录2)
         */
        List<Catalogue> resultList = catalogueTreeSortFirstLetterStrategyTest.sort(catalogueTree, sortTypeEnum);
        List<Integer> resultIdList = CategoryTreeSortStrategyTestUtils.breadthTraverse(resultList);
        assertEquals(Lists.newArrayList(1, 4, 2, 3, 6, 7), resultIdList);
    }

    @Test
    public void sortEmptyTest() {
        List<Catalogue> catalogueTree = Lists.newArrayList();
        SortTypeEnum sortTypeEnum = SortTypeEnum.ASC;

        List<Catalogue> resultList = catalogueTreeSortFirstLetterStrategyTest.sort(catalogueTree, sortTypeEnum);
        assertEquals(Lists.newArrayList(), resultList);
    }

    @Test
    public void sortEmptyTest2() {
        List<Catalogue> catalogueTree = null;
        SortTypeEnum sortTypeEnum = SortTypeEnum.ASC;

        List<Catalogue> resultList = catalogueTreeSortFirstLetterStrategyTest.sort(catalogueTree, sortTypeEnum);
        assertEquals(Lists.newArrayList(), resultList);
    }
}
