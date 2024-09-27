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

import org.dinky.data.exception.BusException;
import org.dinky.data.model.CustomConfig;
import org.dinky.utils.TextUtil;

import org.apache.paimon.options.Options;

import java.util.List;

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
    private Hadoop hadoop;
    private String fileSystemType;
    private String catalogType;

    public Options getOptions() {
        Options options = new Options();

        if (CatalogType.getByName(catalogType) == CatalogType.Hive) {
            options.set(PaimonHadoopConfig.METASTORE, "hive");
            if (hadoop != null) {
                if (!TextUtil.isEmpty(hadoop.getHadoopConfDir())) {
                    options.set(PaimonHadoopConfig.hadoopConfDir, hadoop.getHadoopConfDir());
                }
                if (!TextUtil.isEmpty(hadoop.getHiveConfDir())) {
                    options.set(PaimonHadoopConfig.hiveConfDir, hadoop.getHiveConfDir());
                }
                if (!TextUtil.isEmpty(hadoop.getUri())) {
                    options.set(PaimonHadoopConfig.URI, hadoop.getUri());
                }
            } else {
                throw new BusException("Hadoop config is required for Hive catalog");
            }
        } else {
            options.set("warehouse", warehouse);
        }

        if (FileSystemType.fromType(fileSystemType) == FileSystemType.S3) {
            if (s3 != null) {
                options.set(PaimonS3Configuration.ENDPOINT, s3.getEndpoint());
                options.set(PaimonS3Configuration.ACCESS_KEY, s3.getAccessKey());
                options.set(PaimonS3Configuration.SECRET_KEY, s3.getSecretKey());
                options.set(PaimonS3Configuration.PATH_STYLE_ACCESS, String.valueOf(s3.isPathStyle()));
            } else {
                throw new BusException("S3 config is required for S3 file system");
            }
        } else if (FileSystemType.fromType(fileSystemType) == FileSystemType.HDFS) {
            if (hadoop != null) {
                if (!TextUtil.isEmpty(hadoop.getHadoopConfDir())) {
                    options.set(PaimonHadoopConfig.hadoopConfDir, hadoop.getHadoopConfDir());
                }
            } else {
                throw new BusException("Hadoop config is required for hadoop ");
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

    @Data
    public static class Hadoop {
        private String hiveConfDir;
        private String hadoopConfDir;
        private String uri;
    }
}
