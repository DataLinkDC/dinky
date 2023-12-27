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

package org.dinky.trans.ddl;

import org.junit.Assert;
import org.junit.Test;

/**
 * CustomSetOperationTest
 *
 */
public class CustomSetOperationTest {

    private static final String SET_STATEMENT1 = "set execution.checkpointing.interval = 80000";
    private static final String SET_STATEMENT2 = "SET 'execution.checkpointing.interval' = '80000'";
    private static final String SET_STATEMENT3 = "SET 'execution.checkpointing.interval = 80000'";

    @Test
    public void setOperationTest() {
        String expectKey = "execution.checkpointing.interval";
        String expectValue = "80000";
        CustomSetOperation customSetOperation1 = new CustomSetOperation(SET_STATEMENT1);
        Assert.assertEquals(expectKey, customSetOperation1.getKey());
        Assert.assertEquals(expectValue, customSetOperation1.getValue());
        CustomSetOperation customSetOperation2 = new CustomSetOperation(SET_STATEMENT2);
        Assert.assertEquals(expectKey, customSetOperation2.getKey());
        Assert.assertEquals(expectValue, customSetOperation2.getValue());
        CustomSetOperation customSetOperation3 = new CustomSetOperation(SET_STATEMENT3);
        Assert.assertEquals(expectKey, customSetOperation3.getKey());
        Assert.assertEquals(expectValue, customSetOperation3.getValue());
    }
}
