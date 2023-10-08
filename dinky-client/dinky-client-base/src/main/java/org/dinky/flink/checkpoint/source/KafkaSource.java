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

package org.dinky.flink.checkpoint.source;

import org.dinky.flink.checkpoint.SupportSplitSerializer;

import org.apache.flink.connector.kafka.source.split.KafkaPartitionSplit;
import org.apache.flink.connector.kafka.source.split.KafkaPartitionSplitSerializer;

import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;

@SupportSplitSerializer(clazz = KafkaPartitionSplitSerializer.class)
public class KafkaSource extends BaseCheckpointSource<KafkaPartitionSplit> {

    public KafkaSource(List<KafkaPartitionSplit> splits) {
        super(splits);
    }

    public List<String> headers() {
        return CollUtil.newArrayList("partition", "starting-offset", "stopping-offset");
    }

    public List<?> datas() {
        return splits.stream()
                .map(d -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.set("partition", d.getTopicPartition().toString());
                    jsonObject.set("starting-offset", d.getStartingOffset());
                    jsonObject.set(
                            "stopping-offset", d.getStoppingOffset().orElse(KafkaPartitionSplit.NO_STOPPING_OFFSET));
                    return jsonObject;
                })
                .collect(Collectors.toList());
    }
}
