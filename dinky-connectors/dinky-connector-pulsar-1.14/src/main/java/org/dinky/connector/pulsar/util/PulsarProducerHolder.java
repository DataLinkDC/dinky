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

import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.HashingScheme;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;

import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** * @version 1.0 * @Desc: */
public class PulsarProducerHolder {
    private static final Logger LOG = LoggerFactory.getLogger(PulsarProducerHolder.class);
    private static final Map<String, Producer> PULSAR_PRODUCER_MAP = new ConcurrentHashMap<>();

    public static Producer getProducer(String defaultTopicName, Properties properties, PulsarClient client)
            throws Exception {
        return get(defaultTopicName, properties, client);
    }

    private static Producer get(String defaultTopicName, Properties properties, PulsarClient client) throws Exception {
        synchronized (PulsarProducerHolder.class) {
            String pulsarProducerCacheKey = defaultTopicName;
            Producer pulsarProducer = PULSAR_PRODUCER_MAP.get(pulsarProducerCacheKey);
            LOG.info("get pulsarProducer from map result is " + pulsarProducer);
            if (null != pulsarProducer) {
                return pulsarProducer;
            }

            Producer producer = createPulsarProducer(defaultTopicName, properties, client);
            Producer newPulsarProducer = PULSAR_PRODUCER_MAP.putIfAbsent(pulsarProducerCacheKey, producer);
            if (newPulsarProducer == null) {
                return producer;
            }
            return newPulsarProducer;
        }
    }

    private static Producer createPulsarProducer(String defaultTopicName, Properties properties, PulsarClient client) {
        try {
            LOG.info("create producer, and ID is "
                    + UUID.randomUUID()
                    + ", and cache map size is "
                    + PULSAR_PRODUCER_MAP.size());
            LOG.info("now defaultTopicName is "
                    + defaultTopicName
                    + ", and map content is "
                    + PULSAR_PRODUCER_MAP.get(defaultTopicName));

            ProducerBuilder<byte[]> producerBuilder = client.newProducer();
            producerBuilder
                    .blockIfQueueFull(Boolean.TRUE)
                    .compressionType(CompressionType.LZ4)
                    .topic(defaultTopicName)
                    .hashingScheme(HashingScheme.JavaStringHash)
                    .
                    // batchingMaxPublishDelay(100, TimeUnit.MILLISECONDS).
                    loadConf((Map) properties);
            Producer<byte[]> producer = producerBuilder.create();
            return producer;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("创建Producer失败", e);
        }
    }
}
