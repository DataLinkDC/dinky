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

package org.dinky.data.dto;

import org.dinky.data.bo.catalogue.export.ExportCatalogueBO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import cn.hutool.json.JSONUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class ImportCatalogueDTO {

    private Integer parentCatalogueId;

    private ExportCatalogueBO exportCatalogue;

    public static ImportCatalogueDTO build(MultipartHttpServletRequest request) {
        int parentCatalogueId = Integer.parseInt(request.getParameter("pid"));
        ExportCatalogueBO exportCatalogue = null;
        MultipartFile file = request.getFile("file");
        if (Objects.isNull(file)) {
            return null;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            exportCatalogue = JSONUtil.toBean(content.toString(), ExportCatalogueBO.class);
        } catch (IOException e) {
            log.error("Convert MultipartHttpServletRequest to ExportCatalogueBO failed", e);
        }
        return ImportCatalogueDTO.builder()
                .parentCatalogueId(parentCatalogueId)
                .exportCatalogue(exportCatalogue)
                .build();
    }
}
