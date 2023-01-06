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

package com.dlink.utils;

import com.dlink.constant.DirConstant;
import com.dlink.model.FileNode;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * DirUtilTest
 *
 * @author wenmo
 * @since 2022/10/14 22:00
 */
public class DirUtilTest {

    @Test
    public void testListDirByPath() {
        List<FileNode> dirList = DirUtil.listDirByPath(DirConstant.LOG_DIR_PATH);
        Assertions.assertThat(dirList).isNotNull();
    }

    @Test
    public void testReadFile() {
        String result = DirUtil.readFile(DirConstant.LOG_DIR_PATH + "/dlink.log");
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void testReadRootLog() {
        String result = DirUtil.readFile(DirConstant.ROOT_LOG_PATH);
        Assertions.assertThat(result).isNotNull();
    }
}
