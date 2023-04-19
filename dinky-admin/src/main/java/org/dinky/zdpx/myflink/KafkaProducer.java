/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dinky.zdpx.myflink;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import java.util.Properties;
import java.util.function.Consumer;

/**
 * Produces TaxiRecords into a Kafka topic.
 */
public class KafkaProducer implements Consumer<String> {

	private final String topic;
	private final org.apache.kafka.clients.producer.KafkaProducer<byte[], byte[]> producer;

	public KafkaProducer(String kafkaTopic, String kafkaBrokers) {
		this.topic = kafkaTopic;
		this.producer = new org.apache.kafka.clients.producer.KafkaProducer<>(createKafkaProperties(kafkaBrokers));
	}

	@Override
	public void accept(String context) {
		// serialize context as JSON
		// create producer context and publish to Kafka
		ProducerRecord<byte[], byte[]> kafkaRecord = new ProducerRecord<>(topic, context.getBytes());
		producer.send(kafkaRecord);
	}

	/**
	 * Create configuration properties for Kafka producer.
	 *
	 * @param brokers The brokers to connect to.
	 * @return A Kafka producer configuration.
	 */
	private static Properties createKafkaProperties(String brokers) {
		Properties kafkaProps = new Properties();
		kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
		kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getCanonicalName());
		kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getCanonicalName());
		return kafkaProps;
	}
}