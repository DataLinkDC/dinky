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

package org.dinky.cdc.doris;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;

public class DorisSinkOptions {

    public static final ConfigOption<String> FENODES = ConfigOptions.key("fenodes")
            .stringType()
            .noDefaultValue()
            .withDescription("Doris FE http address, support multiple addresses, separated by commas.");
    public static final ConfigOption<String> TABLE_IDENTIFIER = ConfigOptions.key("table.identifier")
            .stringType()
            .noDefaultValue()
            .withDescription("Doris table identifier, eg, db1.tbl1.");
    public static final ConfigOption<String> USERNAME =
            ConfigOptions.key("username").stringType().noDefaultValue().withDescription("Doris username.");
    public static final ConfigOption<String> PASSWORD =
            ConfigOptions.key("password").stringType().noDefaultValue().withDescription("Doris password.");

    public static final ConfigOption<Boolean> DORIS_DESERIALIZE_ARROW_ASYNC = ConfigOptions.key(
                    "doris.deserialize.arrow.async")
            .booleanType()
            .defaultValue(false)
            .withDescription(
                    "Whether to support asynchronous conversion of Arrow format to RowBatch required for flink-doris-connector iteration.");
    public static final ConfigOption<Integer> DORIS_DESERIALIZE_QUEUE_SIZE = ConfigOptions.key(
                    "doris.deserialize.queue.size")
            .intType()
            .defaultValue(64)
            .withDescription(
                    "Asynchronous conversion of the internal processing queue in Arrow format takes effect when doris.deserialize.arrow.async is true.");
    public static final ConfigOption<Long> DORIS_EXEC_MEM_LIMIT = ConfigOptions.key("doris.exec.mem.limit")
            .longType()
            .defaultValue(2147483648L)
            .withDescription("Memory limit for a single query. The default is 2GB, in bytes.");
    public static final ConfigOption<String> DORIS_FILTER_QUERY = ConfigOptions.key("doris.filter.query")
            .stringType()
            .noDefaultValue()
            .withDescription(
                    "Filter expression of the query, which is transparently transmitted to Doris. Doris uses this expression to complete source-side data filtering.");
    public static final ConfigOption<String> DORIS_READ_FIELD = ConfigOptions.key("doris.read.field")
            .stringType()
            .noDefaultValue()
            .withDescription("List of column names in the Doris table, separated by commas.");
    public static final ConfigOption<Integer> DORIS_BATCH_SIZE = ConfigOptions.key("doris.batch.size")
            .intType()
            .defaultValue(1024)
            .withDescription(
                    "The maximum number of rows to read data from BE at one time. Increasing this value can reduce the number of connections between Flink and Doris."
                            + " Thereby reducing the extra time overhead caused by network delay.");
    public static final ConfigOption<Integer> DORIS_REQUEST_CONNECT_TIMEOUT_MS = ConfigOptions.key(
                    "doris.request.connect.timeout.ms")
            .intType()
            .defaultValue(30000)
            .withDescription("Connection timeout for sending requests to Doris.");
    public static final ConfigOption<Integer> DORIS_REQUEST_QUERY_TIMEOUT_S = ConfigOptions.key(
                    "doris.request.query.timeout.s")
            .intType()
            .defaultValue(3600)
            .withDescription("Query the timeout time of doris, the default is 1 hour, -1 means no timeout limit.");
    public static final ConfigOption<Integer> DORIS_REQUEST_READ_TIMEOUT_MS = ConfigOptions.key(
                    "doris.request.read.timeout.ms")
            .intType()
            .defaultValue(30000)
            .withDescription("Read timeout for sending request to Doris.");
    public static final ConfigOption<Integer> DORIS_REQUEST_RETRIES = ConfigOptions.key("doris.request.retries")
            .intType()
            .defaultValue(3)
            .withDescription("Number of retries to send requests to Doris.");
    public static final ConfigOption<Integer> DORIS_REQUEST_TABLET_SIZE = ConfigOptions.key("doris.request.tablet.size")
            .intType()
            .defaultValue(Integer.MAX_VALUE)
            .withDescription(
                    "The number of Doris Tablets corresponding to an Partition. The smaller this value is set, the more partitions will be generated. "
                            + "This will increase the parallelism on the flink side, but at the same time will cause greater pressure on Doris.");

    public static final ConfigOption<Integer> SINK_BUFFER_COUNT = ConfigOptions.key("sink.buffer-count")
            .intType()
            .defaultValue(3)
            .withDescription(
                    "The number of write data cache buffers, it is not recommended to modify, the default configuration is sufficient.");
    public static final ConfigOption<Integer> SINK_BUFFER_SIZE = ConfigOptions.key("sink.buffer-size")
            .intType()
            .defaultValue(1048576)
            .withDescription(
                    "Write data cache buffer size, in bytes. It is not recommended to modify, the default configuration is sufficient.");
    public static final ConfigOption<Boolean> SINK_ENABLE_DELETE = ConfigOptions.key("sink.enable-delete")
            .booleanType()
            .defaultValue(true)
            .withDescription(
                    "Whether to enable deletion. This option requires Doris table to enable batch delete function (0.15+ version is enabled by default), and only supports Uniq model.");
    public static final ConfigOption<String> SINK_LABEL_PREFIX = ConfigOptions.key("sink.label-prefix")
            .stringType()
            .noDefaultValue()
            .withDescription(
                    "The label prefix used by stream load imports. In the 2pc scenario, global uniqueness is required to ensure the EOS semantics of Flink.");
    public static final ConfigOption<Integer> SINK_MAX_RETRIES = ConfigOptions.key("sink.max-retries")
            .intType()
            .defaultValue(1)
            .withDescription("In the 2pc scenario, the number of retries after the commit phase fails.");

    public static final ConfigOption<Boolean> SINK_USE_NEW_SCHEMA_CHANGE = ConfigOptions.key("sink.use-new-schema-change")
            .booleanType()
            .defaultValue(false)
            .withDescription(
                    "supports table column name, column type, default, comment synchronization, supports multi-column changes, "
                            +"and supports column name rename. Need to be enabled by configuring use-new-schema-change.");

}
