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

package org.dinky.service.resource.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.dinky.data.model.Resources;
import org.dinky.data.model.ResourcesModelEnum;
import org.dinky.data.model.SystemConfiguration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.baomidou.mybatisplus.core.conditions.Wrapper;

class ResourceServiceImplTest {

    private final SystemConfiguration configuration = SystemConfiguration.getInstances();
    private final String originalBasePath =
            configuration.getResourcesUploadBasePath().getValue();
    private final ResourcesModelEnum originalModel =
            configuration.getResourcesModel().getValue();

    @TempDir
    Path tempDir;

    @AfterEach
    void restoreConfiguration() {
        configuration.getResourcesUploadBasePath().setValue(originalBasePath);
        configuration.getResourcesModel().setValue(originalModel);
    }

    @Test
    void renameFileMovesPhysicalResource() throws Exception {
        configuration.getResourcesUploadBasePath().setValue(tempDir.toString());
        configuration.getResourcesModel().setValue(ResourcesModelEnum.LOCAL);

        Path oldFile = tempDir.resolve("jars/old.jar");
        Files.createDirectories(oldFile.getParent());
        Files.write(oldFile, new byte[] {1});

        Resources resource = Resources.builder()
                .id(7)
                .pid(1)
                .fileName("old.jar")
                .fullName("jars/old.jar")
                .description("old")
                .isDirectory(false)
                .build();
        ResourceServiceImpl service = spy(new ResourceServiceImpl());
        doReturn(resource).when(service).getById(7);
        doReturn(0L).when(service).count(any(Wrapper.class));
        doReturn(Collections.emptyList()).when(service).list(any(Wrapper.class));
        doReturn(true).when(service).updateById(any(Resources.class));

        service.rename(7, "new.jar", "updated");

        assertThat(oldFile).doesNotExist();
        assertThat(tempDir.resolve("jars/new.jar")).exists();
        assertThat(resource.getFileName()).isEqualTo("new.jar");
        assertThat(resource.getFullName()).isEqualTo("jars/new.jar");
        assertThat(resource.getDescription()).isEqualTo("updated");
    }

    @Test
    void updateDescriptionKeepsPhysicalResourceInPlace() throws Exception {
        configuration.getResourcesUploadBasePath().setValue(tempDir.toString());
        configuration.getResourcesModel().setValue(ResourcesModelEnum.LOCAL);

        Path file = tempDir.resolve("jars/current.jar");
        Files.createDirectories(file.getParent());
        Files.write(file, new byte[] {1});

        Resources resource = Resources.builder()
                .id(8)
                .pid(1)
                .fileName("current.jar")
                .fullName("jars/current.jar")
                .description("old")
                .isDirectory(false)
                .build();
        ResourceServiceImpl service = spy(new ResourceServiceImpl());
        doReturn(resource).when(service).getById(8);
        doReturn(0L).when(service).count(any(Wrapper.class));
        doReturn(true).when(service).updateById(any(Resources.class));

        service.rename(8, "current.jar", "updated");

        assertThat(file).exists();
        assertThat(resource.getFullName()).isEqualTo("jars/current.jar");
        assertThat(resource.getDescription()).isEqualTo("updated");
    }
}
