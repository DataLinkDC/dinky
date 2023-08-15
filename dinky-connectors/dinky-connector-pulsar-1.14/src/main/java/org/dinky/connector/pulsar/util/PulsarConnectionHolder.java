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

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.impl.PulsarClientImpl;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** * @version 1.0 * @Desc: */
public class PulsarConnectionHolder {
    private static final Logger LOG = LoggerFactory.getLogger(PulsarConnectionHolder.class);
    private static final Map<String, PulsarClientImpl> PULSAR_CLIENT_MAP = new ConcurrentHashMap<>();

    public static PulsarClientImpl getConsumerClient(String serviceUrl, Properties properties) throws Exception {
        return get(serviceUrl, true, properties);
    }

    public static PulsarClientImpl getProducerClient(String serviceUrl, Properties properties) throws Exception {
        return get(serviceUrl, false, properties);
    }

    private static PulsarClientImpl get(String serviceUrl, boolean consumer, Properties properties) throws Exception {
        synchronized (PulsarConnectionHolder.class) {
            String pulsarClientCacheKey = getPulsarClientCacheKey(serviceUrl, consumer);
            PulsarClientImpl pulsarClient = PULSAR_CLIENT_MAP.get(pulsarClientCacheKey);
            if (null != pulsarClient) {
                return pulsarClient;
            }

            // return PULSAR_CLIENT_MAP.computeIfAbsent(pulsarClientCacheKey, serviceUrlTag ->
            // createPulsarClient(serviceUrl));
            PulsarClientImpl pulsarClientImpl = createPulsarClient(serviceUrl, properties);
            PulsarClientImpl newPulsarClientImpl =
                    PULSAR_CLIENT_MAP.putIfAbsent(pulsarClientCacheKey, pulsarClientImpl);
            if (newPulsarClientImpl == null) {
                return pulsarClientImpl;
            }
            return newPulsarClientImpl;
        }
    }

    private static String getPulsarClientCacheKey(String serviceUrl, boolean consumer) {
        return serviceUrl + consumer;
    }

    private static PulsarClientImpl createPulsarClient(String serviceUrl, Properties properties) {
        try {
            LOG.info("create client, and ID is "
                    + UUID.randomUUID()
                    + ", and cache map size is "
                    + PULSAR_CLIENT_MAP.size());

            return (PulsarClientImpl) PulsarClient.builder()
                    .serviceUrl(serviceUrl)
                    .maxNumberOfRejectedRequestPerConnection(50)
                    .loadConf((Map) properties)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("创建PulsarClient失败", e);
        }
    }
}
