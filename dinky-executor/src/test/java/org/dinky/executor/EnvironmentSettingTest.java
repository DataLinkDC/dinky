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


import org.junit.Assert;

import junit.framework.TestCase;

public class EnvironmentSettingTest extends TestCase {

    public void testEnvAddress() {
        assertRet("HTTP://flink.b.x", "flink.b.x:80");
        assertRet("HTTPs://flink.b.x", "flink.b.x:443");
        assertRet("http://flink.b.x:333", "flink.b.x:333");
        assertRet("HTTPs://flink.b.x:333", "flink.b.x:333");
        assertRet("flink.b.x", "flink.b.x:80");
        assertRet("http://127.0.0.1", "127.0.0.1:80");
        assertRet("127.0.0.1", "127.0.0.1:80");
    }

    private static void assertRet(String src, String expect) {
        String[] hostAndPort = EnvironmentSetting.getHostAndPort(src);
        Assert.assertEquals(hostAndPort.length, 2);
        Assert.assertEquals(hostAndPort[0] + ":" + hostAndPort[1], expect);
    }

}
