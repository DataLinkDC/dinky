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

package org.dinky.service.catalogue.impl;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.when;

import org.dinky.data.bo.catalogue.export.ExportCatalogueBO;
import org.dinky.data.dto.ImportCatalogueDTO;
import org.dinky.data.model.Catalogue;
import org.dinky.data.vo.ExportCatalogueVO;
import org.dinky.mapper.CatalogueMapper;
import org.dinky.service.TaskService;
import org.dinky.service.catalogue.factory.CatalogueFactory;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*", "org.w3c.*"})
public class CatalogueServiceImplTest {

    @Mock
    private CatalogueMapper catalogueMapper;

    @Mock
    private TaskService taskService;

    @Mock
    private CatalogueFactory catalogueFactory;

    @InjectMocks
    private MockCatalogueServiceImpl catalogueServiceImplTest;

    @Before
    public void init() throws IllegalAccessException, NoSuchFieldException {
        // mock catalogueMapper
        Field baseMapperField = catalogueServiceImplTest
                .getClass()
                .getSuperclass()
                .getSuperclass()
                .getSuperclass()
                .getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(catalogueServiceImplTest, catalogueMapper);
    }

    @Test
    public void exportCatalogueTest() {
        int catalogueId = 1;

        // result
        ExportCatalogueBO subExportCatalogueBo = ExportCatalogueBO.builder()
                .name("234")
                .enabled(true)
                .isLeaf(false)
                .type("ttt")
                .task(null)
                .children(Lists.newArrayList())
                .build();
        ExportCatalogueBO exportCatalogueBo = ExportCatalogueBO.builder()
                .name("123")
                .enabled(true)
                .isLeaf(false)
                .type("ttt")
                .task(null)
                .children(Lists.newArrayList(subExportCatalogueBo))
                .build();
        String expectDataJson = JSONUtil.toJsonPrettyStr(exportCatalogueBo);

        // mock
        Catalogue catalogue = new Catalogue();
        catalogue.setId(catalogueId);
        catalogue.setName("123");
        catalogue.setIsLeaf(false);
        catalogue.setEnabled(true);
        catalogue.setType("ttt");
        Catalogue subCatalogue = new Catalogue();
        subCatalogue.setId(222);
        subCatalogue.setParentId(catalogueId);
        subCatalogue.setName("234");
        subCatalogue.setIsLeaf(false);
        subCatalogue.setEnabled(true);
        subCatalogue.setType("ttt");
        when(catalogueMapper.selectById(eq(catalogueId))).thenReturn(catalogue);
        when(catalogueMapper.selectById(eq(222))).thenReturn(subCatalogue);

        AtomicInteger cnt = new AtomicInteger();
        doAnswer(invocationOnMock -> {
                    if (cnt.getAndIncrement() > 0) {
                        return Lists.newArrayList();
                    }
                    return Lists.newArrayList(subCatalogue);
                })
                .when(catalogueMapper)
                .selectList(any(LambdaQueryWrapper.class));
        when(catalogueFactory.getExportCatalogueBo(eq(catalogue), eq(null))).thenReturn(exportCatalogueBo);
        when(catalogueFactory.getExportCatalogueBo(eq(subCatalogue), eq(null))).thenReturn(subExportCatalogueBo);

        // execute and verify
        ExportCatalogueVO exportCatalogueVo = catalogueServiceImplTest.exportCatalogue(catalogueId);
        assertEquals(expectDataJson, exportCatalogueVo.getDataJson());
    }

    @Test
    public void importCatalogueTest() {
        ExportCatalogueBO subExportCatalogueBo = ExportCatalogueBO.builder()
                .name("234")
                .enabled(true)
                .isLeaf(false)
                .type("ttt")
                .task(null)
                .children(Lists.newArrayList())
                .build();
        ExportCatalogueBO exportCatalogueBo = ExportCatalogueBO.builder()
                .name("123")
                .enabled(true)
                .isLeaf(false)
                .type("ttt")
                .task(null)
                .children(Lists.newArrayList(subExportCatalogueBo))
                .build();
        int parentCatalogueId = 1;
        ImportCatalogueDTO importCatalogueDto = ImportCatalogueDTO.builder()
                .parentCatalogueId(parentCatalogueId)
                .exportCatalogue(exportCatalogueBo)
                .build();

        // mock
        Catalogue parentCatalogue = new Catalogue();
        parentCatalogue.setId(parentCatalogueId);
        parentCatalogue.setName("111");
        parentCatalogue.setIsLeaf(false);
        parentCatalogue.setEnabled(true);
        parentCatalogue.setType("ttt");
        when(catalogueMapper.selectById(eq(parentCatalogueId))).thenReturn(parentCatalogue);
        when(catalogueMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Lists.newArrayList());
        when(catalogueFactory.getCatalogue(any(), anyInt(), eq(null))).thenCallRealMethod();

        // execute and verify
        catalogueServiceImplTest.importCatalogue(importCatalogueDto);
        verify(taskService, times(2)).saveBatch(anyList());
    }
}
