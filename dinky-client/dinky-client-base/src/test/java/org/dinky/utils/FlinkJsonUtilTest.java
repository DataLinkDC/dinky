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

package org.dinky.utils;

import org.apache.flink.runtime.rest.messages.JobsOverviewHeaders;

import org.junit.Test;

public class FlinkJsonUtilTest {
    @Test
    public void toBeanTest() {
        // If successful, the validation is successful
        FlinkJsonUtil.toBean(
                "{\"jobs\":[{\"jid\":\"495c4e271dc64a4b7e78b8b1eb8d75f5\",\"name\":\"datagen\",\"state\":\"RUNNING\",\"start-time\":1703649915151,\"end-time\":-1,\"duration\":78437474,\"last-modification\":1703649915340,\"tasks\":{\"total\":6,\"created\":0,\"scheduled\":0,\"deploying\":0,\"running\":6,\"finished\":0,\"canceling\":0,\"canceled\":0,\"failed\":0,\"reconciling\":0,\"initializing\":0}},{\"jid\":\"214bbbe27dec4272f18fc3f14e9522b6\",\"name\":\"datagen-1\",\"state\":\"RUNNING\",\"start-time\":1703654026497,\"end-time\":-1,\"duration\":74326128,\"last-modification\":1703654026613,\"tasks\":{\"total\":2,\"created\":0,\"scheduled\":0,\"deploying\":0,\"running\":2,\"finished\":0,\"canceling\":0,\"canceled\":0,\"failed\":0,\"reconciling\":0,\"initializing\":0}}]}",
                JobsOverviewHeaders.getInstance());
    }
}
