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

import org.dinky.data.model.Catalogue;
import org.dinky.service.HistoryService;
import org.dinky.service.JobHistoryService;
import org.dinky.service.JobInstanceService;
import org.dinky.service.MonitorService;
import org.dinky.service.TaskService;
import org.dinky.service.catalogue.factory.CatalogueFactory;
import org.dinky.service.catalogue.factory.CatalogueTreeSortFactory;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.Lists;

public class MockCatalogueServiceImpl extends CatalogueServiceImpl {

    public MockCatalogueServiceImpl(
            TaskService taskService,
            JobInstanceService jobInstanceService,
            HistoryService historyService,
            JobHistoryService jobHistoryService,
            MonitorService monitorService,
            CatalogueTreeSortFactory catalogueTreeSortFactory,
            CatalogueFactory catalogueFactory) {
        super(
                taskService,
                jobInstanceService,
                historyService,
                jobHistoryService,
                monitorService,
                catalogueTreeSortFactory,
                catalogueFactory);
    }

    /**
     * Save batch
     *
     * @param entityList ignore
     * @param batchSize  ignore
     * @return ignore
     */
    @Override
    public boolean saveBatch(Collection<Catalogue> entityList, int batchSize) {
        AtomicInteger id = new AtomicInteger(11111);
        Optional.ofNullable(entityList).orElse(Lists.newArrayList()).forEach(e -> e.setId(id.getAndIncrement()));
        return true;
    }

    @Override
    protected Integer getCurrentUserId() {
        return 222;
    }
}
