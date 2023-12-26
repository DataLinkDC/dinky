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

package org.dinky.constant;

import static org.apache.flink.configuration.ConfigOptions.key;

import org.apache.flink.configuration.ConfigOption;

public class CustomerConfigureOptions {
    public static final ConfigOption<String> REST_TARGET_DIRECTORY = key("rest.target-directory")
            .stringType()
            .noDefaultValue()
            .withDescription("for set savepoint target directory");

    public static final ConfigOption<Boolean> REST_CANCEL_JOB = key("rest.cancel-job")
            .booleanType()
            .defaultValue(false)
            .withDescription("for cancel job when trigger savepoint");

    public static final ConfigOption<String> REST_TRIGGER_ID =
            key("rest.triggerId").stringType().noDefaultValue().withDescription("for trigger savepoint");

    public static final ConfigOption<String> REST_FORMAT_TYPE =
            key("rest.formatType").stringType().defaultValue("DEFAULT").withDescription("for savepoint format type");

    public static final ConfigOption<String> DINKY_HOST =
            key("dinky.dinkyHost").stringType().noDefaultValue().withDescription("dinky local address");

    public static final ConfigOption<Integer> DINKY_PORT =
            key("dinky.dinkyPort").intType().defaultValue(7125).withDescription("dinky local port");
}
