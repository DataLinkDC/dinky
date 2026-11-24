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

package org.dinky.metric.base;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricNames {

    // Task 基础状态
    public static final String DINKY_FLINK_TASK_IS_RUNNING = "DINKY_FLINK_TASK_IS_RUNNING";

    // 背压
    public static final String DINKY_FLINK_TASK_BACKPRESSURE_LEVEL = "DINKY_FLINK_TASK_BACKPRESSURE_LEVEL";
    public static final String DINKY_FLINK_TASK_BACKPRESSURE_RATE_MIN = "DINKY_FLINK_TASK_BACKPRESSURE_RATE_MIN";
    public static final String DINKY_FLINK_TASK_BACKPRESSURE_RATE_MAX = "DINKY_FLINK_TASK_BACKPRESSURE_RATE_MAX";

    // ---------------- Vertice 常用监控指标 ----------------
    // 吞吐率
    public static final String DINKY_FLINK_TASK_NUM_RECORDS_IN = "DINKY_FLINK_TASK_NUM_RECORDS_IN";
    public static final String DINKY_FLINK_TASK_NUM_RECORDS_OUT = "DINKY_FLINK_TASK_NUM_RECORDS_OUT";
    public static final String DINKY_FLINK_TASK_NUM_RECORDS_IN_PER_SEC = "DINKY_FLINK_TASK_NUM_RECORDS_IN_PER_SEC";
    public static final String DINKY_FLINK_TASK_NUM_RECORDS_OUT_PER_SEC = "DINKY_FLINK_TASK_NUM_RECORDS_OUT_PER_SEC";

    // 数据字节量
    public static final String DINKY_FLINK_TASK_NUM_BYTES_IN = "DINKY_FLINK_TASK_NUM_BYTES_IN";
    public static final String DINKY_FLINK_TASK_NUM_BYTES_OUT = "DINKY_FLINK_TASK_NUM_BYTES_OUT";
    public static final String DINKY_FLINK_TASK_NUM_BYTES_IN_PER_SEC = "DINKY_FLINK_TASK_NUM_BYTES_IN_PER_SEC";
    public static final String DINKY_FLINK_TASK_NUM_BYTES_OUT_PER_SEC = "DINKY_FLINK_TASK_NUM_BYTES_OUT_PER_SEC";

    // 延迟
    public static final String DINKY_FLINK_TASK_LATENCY = "DINKY_FLINK_TASK_LATENCY"; // 通常是 histogram
    public static final String DINKY_FLINK_TASK_WATERMARK = "DINKY_FLINK_TASK_WATERMARK";

    // Checkpoint
    public static final String DINKY_FLINK_TASK_CHECKPOINT_ALIGNMENT_TIME =
            "DINKY_FLINK_TASK_CHECKPOINT_ALIGNMENT_TIME";
    public static final String DINKY_FLINK_TASK_CHECKPOINT_START_DELAY = "DINKY_FLINK_TASK_CHECKPOINT_START_DELAY";

    // CPU 时间
    public static final String DINKY_FLINK_TASK_CPU_TIME = "DINKY_FLINK_TASK_CPU_TIME";

    // Checkpoint
    public static final String DINKY_FLINK_TASK_LAST_CHECKPOINT_SIZE = "DINKY_FLINK_TASK_LAST_CHECKPOINT_SIZE";
    public static final String DINKY_FLINK_TASK_LAST_CHECKPOINT_DURATION = "DINKY_FLINK_TASK_LAST_CHECKPOINT_DURATION";
    public static final String DINKY_FLINK_TASK_NUM_BYTES_IN_CHECKPOINTED =
            "DINKY_FLINK_TASK_NUM_BYTES_IN_CHECKPOINTED";
    public static final String DINKY_FLINK_TASK_NUM_BYTES_OUT_CHECKPOINTED =
            "DINKY_FLINK_TASK_NUM_BYTES_OUT_CHECKPOINTED";

    // Kafka / Source Offset
    public static final String DINKY_FLINK_TASK_CURRENT_OFFSET = "DINKY_FLINK_TASK_CURRENT_OFFSET";
    public static final String DINKY_FLINK_TASK_COMMITTED_OFFSET = "DINKY_FLINK_TASK_COMMITTED_OFFSET";
    public static final String DINKY_FLINK_TASK_RECORDS_LAG_MAX = "DINKY_FLINK_TASK_RECORDS_LAG_MAX";
    public static final String DINKY_FLINK_TASK_BYTES_CONSUMED_RATE = "DINKY_FLINK_TASK_BYTES_CONSUMED_RATE";
    public static final String DINKY_FLINK_TASK_RECORDS_CONSUMED_RATE = "DINKY_FLINK_TASK_RECORDS_CONSUMED_RATE";

    // Buffer
    public static final String DINKY_FLINK_TASK_BUFFERS_IN_POOL_USAGE = "DINKY_FLINK_TASK_BUFFERS_IN_POOL_USAGE";
    public static final String DINKY_FLINK_TASK_BUFFERS_OUT_POOL_USAGE = "DINKY_FLINK_TASK_BUFFERS_OUT_POOL_USAGE";
    public static final String DINKY_FLINK_TASK_BUFFERS_IN_QUEUE_LENGTH = "DINKY_FLINK_TASK_BUFFERS_IN_QUEUE_LENGTH";
    public static final String DINKY_FLINK_TASK_BUFFERS_OUT_QUEUE_LENGTH = "DINKY_FLINK_TASK_BUFFERS_OUT_QUEUE_LENGTH";

    // 错误计数
    public static final String DINKY_FLINK_TASK_NUM_RECORDS_FAILED = "DINKY_FLINK_TASK_NUM_RECORDS_FAILED";
    public static final String DINKY_FLINK_TASK_NUM_RECORDS_IN_ERRORS = "DINKY_FLINK_TASK_NUM_RECORDS_IN_ERRORS";
    public static final String DINKY_FLINK_TASK_NUM_RECORDS_OUT_ERRORS = "DINKY_FLINK_TASK_NUM_RECORDS_OUT_ERRORS";

    // CPU & 启动时间 & 重启次数
    public static final String DINKY_FLINK_TASK_CPU_LOAD = "DINKY_FLINK_TASK_CPU_LOAD";
    public static final String DINKY_FLINK_TASK_START_TIME = "DINKY_FLINK_TASK_START_TIME";
    public static final String DINKY_FLINK_TASK_NUM_RESTARTS = "DINKY_FLINK_TASK_NUM_RESTARTS";

    public static final Map<String, String> FLINK_VERTICE_MERTICS_MAP = new HashMap<>();

    public static final List<String> HUDI_METRICS_LIST = Arrays.asList(
            DINKY_FLINK_TASK_NUM_RECORDS_IN,
            DINKY_FLINK_TASK_NUM_RECORDS_OUT,
            DINKY_FLINK_TASK_NUM_RECORDS_IN_PER_SEC,
            DINKY_FLINK_TASK_NUM_RECORDS_OUT_PER_SEC);

    static {
        // 吞吐率
        FLINK_VERTICE_MERTICS_MAP.put("numRecordsIn", DINKY_FLINK_TASK_NUM_RECORDS_IN);
        FLINK_VERTICE_MERTICS_MAP.put("numRecordsOut", DINKY_FLINK_TASK_NUM_RECORDS_OUT);
        FLINK_VERTICE_MERTICS_MAP.put("numRecordsInPerSecond", DINKY_FLINK_TASK_NUM_RECORDS_IN_PER_SEC);
        FLINK_VERTICE_MERTICS_MAP.put("numRecordsOutPerSecond", DINKY_FLINK_TASK_NUM_RECORDS_OUT_PER_SEC);

        // 数据字节量
        FLINK_VERTICE_MERTICS_MAP.put("numBytesIn", DINKY_FLINK_TASK_NUM_BYTES_IN);
        FLINK_VERTICE_MERTICS_MAP.put("numBytesOut", DINKY_FLINK_TASK_NUM_BYTES_OUT);
        FLINK_VERTICE_MERTICS_MAP.put("numBytesInPerSecond", DINKY_FLINK_TASK_NUM_BYTES_IN_PER_SEC);
        FLINK_VERTICE_MERTICS_MAP.put("numBytesOutPerSecond", DINKY_FLINK_TASK_NUM_BYTES_OUT_PER_SEC);

        // 延迟 & watermark
        FLINK_VERTICE_MERTICS_MAP.put("latency", DINKY_FLINK_TASK_LATENCY);
        FLINK_VERTICE_MERTICS_MAP.put("currentOutputWatermark", DINKY_FLINK_TASK_WATERMARK);

        // checkpoint
        FLINK_VERTICE_MERTICS_MAP.put("checkpointAlignmentTime", DINKY_FLINK_TASK_CHECKPOINT_ALIGNMENT_TIME);
        FLINK_VERTICE_MERTICS_MAP.put("checkpointStartDelay", DINKY_FLINK_TASK_CHECKPOINT_START_DELAY);
        FLINK_VERTICE_MERTICS_MAP.put("lastCheckpointDuration", DINKY_FLINK_TASK_LAST_CHECKPOINT_DURATION);

        // 内存
        // FLINK_VERTICE_MERTICS_MAP.put("memUsed", DINKY_FLINK_TASK_MEM_USED);
        FLINK_VERTICE_MERTICS_MAP.put("buffers.inPoolUsage", DINKY_FLINK_TASK_BUFFERS_IN_POOL_USAGE);
        FLINK_VERTICE_MERTICS_MAP.put("buffers.outPoolUsage", DINKY_FLINK_TASK_BUFFERS_OUT_POOL_USAGE);

        // CPU
        FLINK_VERTICE_MERTICS_MAP.put("CpuTime", DINKY_FLINK_TASK_CPU_TIME);

        // Checkpoint
        FLINK_VERTICE_MERTICS_MAP.put("lastCheckpointSize", DINKY_FLINK_TASK_LAST_CHECKPOINT_SIZE);
        FLINK_VERTICE_MERTICS_MAP.put("lastCheckpointDuration", DINKY_FLINK_TASK_LAST_CHECKPOINT_DURATION);
        FLINK_VERTICE_MERTICS_MAP.put("numBytesInCheckpointed", DINKY_FLINK_TASK_NUM_BYTES_IN_CHECKPOINTED);
        FLINK_VERTICE_MERTICS_MAP.put("numBytesOutCheckpointed", DINKY_FLINK_TASK_NUM_BYTES_OUT_CHECKPOINTED);

        // Kafka / Source Offset
        FLINK_VERTICE_MERTICS_MAP.put("currentOffset", DINKY_FLINK_TASK_CURRENT_OFFSET);
        FLINK_VERTICE_MERTICS_MAP.put("committedOffset", DINKY_FLINK_TASK_COMMITTED_OFFSET);
        FLINK_VERTICE_MERTICS_MAP.put("records-lag-max", DINKY_FLINK_TASK_RECORDS_LAG_MAX);
        FLINK_VERTICE_MERTICS_MAP.put("bytes-consumed-rate", DINKY_FLINK_TASK_BYTES_CONSUMED_RATE);
        FLINK_VERTICE_MERTICS_MAP.put("records-consumed-rate", DINKY_FLINK_TASK_RECORDS_CONSUMED_RATE);

        // Buffer
        FLINK_VERTICE_MERTICS_MAP.put("buffers.inPoolUsage", DINKY_FLINK_TASK_BUFFERS_IN_POOL_USAGE);
        FLINK_VERTICE_MERTICS_MAP.put("buffers.outPoolUsage", DINKY_FLINK_TASK_BUFFERS_OUT_POOL_USAGE);
        FLINK_VERTICE_MERTICS_MAP.put("buffers.inQueueLength", DINKY_FLINK_TASK_BUFFERS_IN_QUEUE_LENGTH);
        FLINK_VERTICE_MERTICS_MAP.put("buffers.outQueueLength", DINKY_FLINK_TASK_BUFFERS_OUT_QUEUE_LENGTH);

        // 错误计数
        FLINK_VERTICE_MERTICS_MAP.put("numRecordsFailed", DINKY_FLINK_TASK_NUM_RECORDS_FAILED);
        FLINK_VERTICE_MERTICS_MAP.put("numRecordsInErrors", DINKY_FLINK_TASK_NUM_RECORDS_IN_ERRORS);
        FLINK_VERTICE_MERTICS_MAP.put("numRecordsOutErrors", DINKY_FLINK_TASK_NUM_RECORDS_OUT_ERRORS);

        // CPU & 启动时间 & 重启次数
        FLINK_VERTICE_MERTICS_MAP.put("cpuLoad", DINKY_FLINK_TASK_CPU_LOAD);
        FLINK_VERTICE_MERTICS_MAP.put("taskStartTime", DINKY_FLINK_TASK_START_TIME);
        FLINK_VERTICE_MERTICS_MAP.put("numRestarts", DINKY_FLINK_TASK_NUM_RESTARTS);
    }
}
