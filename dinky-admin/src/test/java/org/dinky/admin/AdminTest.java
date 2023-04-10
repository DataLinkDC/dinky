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

package org.dinky.admin;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import cn.dev33.satoken.secure.SaSecureUtil;

/**
 * SqlParserTest
 *
 * @since 2021/6/14 17:03
 */
@Ignore
public class AdminTest {

    @Test
    public void adminTest() {
        String admin = SaSecureUtil.md5("admin");
        Assert.assertEquals("21232f297a57a5a743894a0e4a801fc3", admin);
    }
}
