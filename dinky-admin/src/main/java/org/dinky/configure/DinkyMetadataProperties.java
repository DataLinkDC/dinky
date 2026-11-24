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

package org.dinky.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Metadata properties for Dinky, used to configure metadata database connection behaviors
 * such as custom PostgreSQL schema support.
 */
@Data
@ConfigurationProperties(prefix = "dinky.metadata")
public class DinkyMetadataProperties {

    /**
     * The schema name for PostgreSQL metadata database.
     * Defaults to {@code public} to maintain backward compatibility.
     * Override with {@code dinky.metadata.schema} or environment variable {@code DINKY_METADATA_SCHEMA}.
     */
    private String schema = "public";
}
