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

package org.dinky.metadata.config;

import org.dinky.data.model.CustomConfig;
import org.dinky.data.model.S3Configuration;

import org.apache.paimon.options.Options;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaimonConfig implements IConnectConfig {

    private List<CustomConfig> paimonConfig;
    private String warehouse;
    private S3 s3;
    private String fileSystemType;
    private String catalogType;

    public Options getOptions() {
        Options options = new Options();
        options.set("warehouse", warehouse);
        if (Objects.requireNonNull(FileSystemType.fromType(fileSystemType)) == FileSystemType.S3) {
            if (s3 != null) {
                options.set(S3Configuration.ENDPOINT, s3.getEndpoint());
                options.set(S3Configuration.ACCESS_KEY, s3.getAccessKey());
                options.set(S3Configuration.SECRET_KEY, s3.getSecretKey());
                options.set(S3Configuration.PATH_STYLE_ACCESS, String.valueOf(s3.isPathStyle()));
            } else {
                throw new IllegalArgumentException("S3 config is required for S3 file system");
            }
        }
        if (paimonConfig != null) {
            for (CustomConfig customConfig : paimonConfig) {
                options.set(customConfig.getName(), customConfig.getValue());
            }
        }
        return options;
    }

    @Data
    public static class S3 {
        private String endpoint;
        private String accessKey;
        private String secretKey;
        private boolean pathStyle;
    }
}
