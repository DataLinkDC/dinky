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

package org.dinky.connector.pulsar.util;

import org.apache.flink.annotation.PublicEvolving;

import java.util.Map;
import java.util.Properties;

/** * @version 1.0 * @Desc: */

/** Utilities for {@link PulsarConnectorOptions}. */
@PublicEvolving
public class PulsarConnectorOptionsUtil {

    // Prefix for Pulsar specific properties.
    public static final String PROPERTIES_PREFIX = "properties.";
    public static final String PROPERTIES_CLIENT_PREFIX = "properties_client.";

    public static Properties getPulsarProperties(Map<String, String> tableOptions, String prefix) {
        final Properties pulsarProperties = new Properties();

        if (hasPulsarClientProperties(tableOptions)) {
            tableOptions.keySet().stream().filter(key -> key.startsWith(prefix)).forEach(key -> {
                final String value = tableOptions.get(key);
                final String subKey = key.substring((prefix).length());
                pulsarProperties.put(subKey, value);
            });
        }
        return pulsarProperties;
    }

    /**
     * Decides if the table options contains Pulsar client properties that start with prefix
     * 'properties'.
     */
    private static boolean hasPulsarClientProperties(Map<String, String> tableOptions) {
        return tableOptions.keySet().stream().anyMatch(k -> k.startsWith(PROPERTIES_PREFIX));
    }

    private PulsarConnectorOptionsUtil() {}
}
