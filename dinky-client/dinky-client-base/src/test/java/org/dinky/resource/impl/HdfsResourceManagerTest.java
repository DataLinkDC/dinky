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

package org.dinky.resource.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.dinky.data.enums.Status;
import org.dinky.data.exception.BusException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.junit.jupiter.api.Test;

class HdfsResourceManagerTest {

    @Test
    void renameThrowsWhenHdfsRejectsMove() throws Exception {
        FileSystem fileSystem = mock(FileSystem.class);
        when(fileSystem.rename(any(Path.class), any(Path.class))).thenReturn(false);
        HdfsResourceManager resourceManager = new HdfsResourceManager() {
            @Override
            public String getFilePath(String path) {
                return path;
            }
        };
        resourceManager.setHdfs(fileSystem);

        assertThatThrownBy(() -> resourceManager.rename("jars/old.jar", "jars/new.jar"))
                .isInstanceOf(BusException.class)
                .extracting("code")
                .isEqualTo(Status.RESOURCE_FILE_RENAME_FAILED);
    }
}
