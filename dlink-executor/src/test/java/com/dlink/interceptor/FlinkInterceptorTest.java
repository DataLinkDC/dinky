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

package com.dlink.interceptor;

import com.dlink.executor.Executor;

import org.junit.Assert;
import org.junit.Test;

/**
 * FlinkInterceptorTest
 *
 * @author wenmo
 * @since 2022/4/9 17:48
 **/
public class FlinkInterceptorTest {

    @Test
    public void replaceFragmentTest() {
        String statement = "nullif1:=NULLIF(1, 0) as val;"
            + "nullif2:=NULLIF(0, 0) as val$null;"
            + "select ${nullif1},${nullif2}";
        String pretreatStatement = FlinkInterceptor.pretreatStatement(Executor.build(), statement);
        Assert.assertEquals("select NULLIF(1, 0) as val,NULLIF(0, 0) as val$null", pretreatStatement);
    }
}
