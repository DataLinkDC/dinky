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

package org.dinky.executor;

import static org.dinky.executor.ExecutorSetting.CHECKPOINT_CONST;
import static org.dinky.executor.ExecutorSetting.PARALLELISM_CONST;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/** */
class ExecutorSettingTest {

    @Test
    void build() {
        Map<String, String> maps = new HashMap<>();
        maps.put(CHECKPOINT_CONST, "123");
        maps.put(PARALLELISM_CONST, "456");

        ExecutorSetting es = ExecutorSetting.build(maps);
        assertEquals(123, es.getCheckpoint());
        assertEquals(456, es.getParallelism());

        ExecutorSetting esNull = ExecutorSetting.build(Collections.emptyMap());
        assertNull(esNull.getCheckpoint());
        assertNull(esNull.getParallelism());
    }
}
