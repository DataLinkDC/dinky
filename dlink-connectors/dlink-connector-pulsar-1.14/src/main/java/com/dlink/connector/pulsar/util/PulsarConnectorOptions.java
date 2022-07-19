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



package com.dlink.connector.pulsar.util;

import org.apache.flink.annotation.PublicEvolving;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.DescribedEnum;
import org.apache.flink.configuration.description.InlineElement;
import org.apache.pulsar.client.api.SubscriptionType;

import static org.apache.flink.configuration.description.TextElement.text;

/**
 * @author DarrenDa
 * * @version 1.0
 * * @Desc:
 **/

/** Options for the Pulsar connector. */
@PublicEvolving
public class PulsarConnectorOptions {

    // --------------------------------------------------------------------------------------------
    // Format options
    // --------------------------------------------------------------------------------------------
    public static final ConfigOption<String> SERVICE_URL =
            ConfigOptions.key("connector.service-url")
                    .stringType()
                    .noDefaultValue()
                    .withDescription(
                            "Defines pulsar service url. ");

    public static final ConfigOption<String> ADMIN_URL =
            ConfigOptions.key("connector.admin-url")
                    .stringType()
                    .defaultValue("http://pulsar-dlink-qa.dlink.com:8080")
                    .withDescription(
                            "Defines pulsar admin url. ");

    public static final ConfigOption<String> TOPIC =
            ConfigOptions.key("connector.topic")
                    .stringType()
                    .noDefaultValue()
                    .withDescription(
                            "Defines pulsar topic. ");

    public static final ConfigOption<String> SUBSCRIPTION_NAME =
            ConfigOptions.key("connector.subscription-name")
                    .stringType()
                    .noDefaultValue()
                    .withDescription(
                            "Defines pulsar subscription name. ");

    public static final ConfigOption<SubscriptionType> SUBSCRIPTION_TYPE =
            ConfigOptions.key("connector.subscription-type")
                    .enumType(SubscriptionType.class)
                    .defaultValue(SubscriptionType.Shared)
                    .withDescription(
                            "Defines pulsar subscription type. ");

    public static final ConfigOption<ScanStartupMode> SUBSCRIPTION_INITIAL_POSITION =
            ConfigOptions.key("connector.subscription-initial-position")
                    .enumType(ScanStartupMode.class)
                    .defaultValue(ScanStartupMode.LATEST)
                    .withDescription("Startup mode for Pulsar consumer.");

    public static final ConfigOption<Long> SUBSCRIPTION_INITIAL_POSITION_TIMESTAMP =
            ConfigOptions.key("connector.subscription-initial-position.timestamp")
                    .longType()
                    .noDefaultValue()
                    .withDescription("Start from the specified message time by Message<byte[]>.getPublishTime().");

    public static final ConfigOption<String> UPDATE_MODE =
            ConfigOptions.key("update-mode")
                    .stringType()
                    .noDefaultValue()
                    .withDescription(
                            "Defines pulsar update mode. ");
    public static final ConfigOption<Integer> SOURCE_PARALLELISM =
            ConfigOptions.key("source-parallelism")
                    .intType()
                    .noDefaultValue()
                    .withDescription(
                            "Defines pulsar sink parallelism. ");
    public static final ConfigOption<Integer> SINK_PARALLELISM =
            ConfigOptions.key("sink-parallelism")
                    .intType()
                    .noDefaultValue()
                    .withDescription(
                            "Defines pulsar sink parallelism. ");

    //与老平台 1.14.3之前版本的sql进行兼容，但是并未使用的参数
    public static final ConfigOption<String> VERSION =
            ConfigOptions.key("connector.version")
                    .stringType()
                    .noDefaultValue()
                    .withDescription(
                            "Defines pulsar version. ");

    //与老平台 1.14.3之前版本的sql进行兼容，但是并未使用的参数
    public static final ConfigOption<String> DERIVE_SCHEMA =
            ConfigOptions.key("format.derive-schema")
                    .stringType()
                    .noDefaultValue()
                    .withDescription(
                            "Defines pulsar derive schema. ");

//    public static final ConfigOption<Boolean> PULSAR_ENABLE_AUTO_ACKNOWLEDGE_MESSAGE  =
//            ConfigOptions.key("pulsar.source.enableAutoAcknowledgeMessage")
//                    .booleanType()
//                    .noDefaultValue()
//                    .withDescription(
//                            "Defines pulsar enable auto acknowledge message. ");

    // --------------------------------------------------------------------------------------------
    // Enums
    // --------------------------------------------------------------------------------------------

    /** Startup mode for the Pulsar consumer, see {@link #SUBSCRIPTION_INITIAL_POSITION}. */
    public enum ScanStartupMode implements DescribedEnum {
        EARLIEST("Earliest", text("Start from the earliest available message in the topic..")),
        LATEST("Latest", text("Start from the latest available message in the topic.")),
        TIMESTAMP("Timestamp", text("Start from the specified message time by Message<byte[]>.getPublishTime()."));

        private final String value;
        private final InlineElement description;

        ScanStartupMode(String value, InlineElement description) {
            this.value = value;
            this.description = description;
        }

        @Override
        public String toString() {
            return value;
        }

        @Override
        public InlineElement getDescription() {
            return description;
        }
    }

    private PulsarConnectorOptions() {
    }
}
