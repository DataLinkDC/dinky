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

package org.dinky.security;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * SecurityAspectTest
 *
 * @author wenmo
 * @since 2023/1/14 15:59
 */
@Ignore
public class SecurityAspectTest {

    @Test
    public void testMask() {
        String sql =
                "stock_trade:='connector' = 'jdbc',"
                        + "useUnicode=yes&characterEncoding=UTF-8&useSSL=false',\\n   'username' = 'trade',"
                        + "\\n 'password' = 'c6634672b535f968b'\\n;\\ntidb_test:='connector' = 'jdbc',"
                        + "\\n'url' = 'jdbc:mysql://localhost:4000/test?useUnicode=yes&characterEncoding=UTF-8&useSSL=false',"
                        + "\\n   'username' = 'root',\\n 'password' = 'wwz@test'\\n;";

        String out = SecurityAspect.mask(sql, SecurityAspect.SENSITIVE, SecurityAspect.MASK);

        Assert.assertEquals(out.contains(SecurityAspect.MASK), true);
    }
}
