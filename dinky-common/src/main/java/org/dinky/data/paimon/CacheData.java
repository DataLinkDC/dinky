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

package org.dinky.data.paimon;

import org.dinky.data.annotations.paimon.Option;
import org.dinky.data.annotations.paimon.Options;
import org.dinky.data.annotations.paimon.PartitionKey;
import org.dinky.data.annotations.paimon.PrimaryKey;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Options({
    @Option(key = "file.format", value = "parquet"),
    @Option(key = "snapshot.time-retained", value = "10 s"),
    @Option(key = "partition.expiration-time", value = "10min"),
    @Option(key = "partition.expiration-check-interval", value = "2min"),
    @Option(key = "partition.timestamp-formatter", value = "yyyy-MM-dd HH:mm"),
    @Option(key = "partition.timestamp-pattern", value = "$cacheTime"),
})
public class CacheData implements Serializable {
    @PartitionKey
    private String cacheTime;

    @PartitionKey
    private String cacheName;

    @PrimaryKey
    private String key;
    /**
     * Serialized json data
     */
    private String data;
}
