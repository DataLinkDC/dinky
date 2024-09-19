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

package org.dinky.data.result;

import static org.junit.Assert.assertEquals;

import org.dinky.utils.JsonUtils;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

/**
 * SelectResultTest.
 *
 * @since 2024/5/31 10:00:00
 */
@Slf4j
public class SelectResultTest {

    @Test
    public void toTruncateJsonTest() {
        SelectResult selectResult = prepareData();
        String truncateJson = selectResult.toTruncateJson(10L);
        log.info("truncateJson: {}", truncateJson);
        assertEquals("{}", truncateJson);
    }

    @Test
    public void toTruncateJsonTest2() {
        SelectResult selectResult = prepareData();
        String truncateJson = selectResult.toTruncateJson(200L);
        log.info("truncateJson: {}", truncateJson);
        selectResult.setRowData(Lists.newArrayList());
        assertEquals(JsonUtils.toJsonString(selectResult), truncateJson);
    }

    @Test
    public void toTruncateJsonTest3() {
        SelectResult selectResult = prepareData();
        String truncateJson = selectResult.toTruncateJson(250L);
        log.info("truncateJson: {}", truncateJson);
        selectResult.setRowData(Lists.newArrayList(selectResult.getRowData().get(0)));
        assertEquals(JsonUtils.toJsonString(selectResult), truncateJson);
    }

    private SelectResult prepareData() {
        String jobId = "111";
        SelectResult selectResult = SelectResult.buildSuccess(jobId);
        selectResult.setColumns(Sets.newLinkedHashSet(Lists.newArrayList("name", "age", "class", "location")));
        selectResult.setRowData(Lists.newArrayList(
                ImmutableMap.of("name", "zhangsan1", "age", 18, "class", "class1", "location", "xxxxxx beijing 222"),
                ImmutableMap.of("name", "zhangsan2", "age", 18, "class", "class1", "location", "xxxxxx beijing 222"),
                ImmutableMap.of("name", "zhangsan3", "age", 18, "class", "class1", "location", "xxxxxx beijing 222"),
                ImmutableMap.of("name", "zhangsan4", "age", 18, "class", "class1", "location", "xxxxxx beijing 222"),
                ImmutableMap.of("name", "zhangsan5", "age", 18, "class", "class1", "location", "xxxxxx beijing 222"),
                ImmutableMap.of("name", "zhangsan6", "age", 18, "class", "class1", "location", "xxxxxx beijing 222"),
                ImmutableMap.of("name", "zhangsan7", "age", 18, "class", "class1", "location", "xxxxxx beijing 222"),
                ImmutableMap.of("name", "zhangsan8", "age", 18, "class", "class1", "location", "xxxxxx beijing 222")));
        return selectResult;
    }
}
