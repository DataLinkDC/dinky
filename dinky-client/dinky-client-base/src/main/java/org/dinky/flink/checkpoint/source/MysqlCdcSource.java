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

import java.util.List;
import java.util.stream.Collectors;

import org.apache.flink.cdc.connectors.mysql.source.split.MySqlBinlogSplit;
import org.apache.flink.cdc.connectors.mysql.source.split.MySqlSnapshotSplit;
import org.apache.flink.cdc.connectors.mysql.source.split.MySqlSplit;
import org.apache.flink.cdc.connectors.mysql.source.split.MySqlSplitSerializer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

@SupportSplitSerializer(clazz = MySqlSplitSerializer.class, order = -1)
public class MysqlCdcSource extends BaseCheckpointSource<MySqlSplit> {

    /**
     * MySqlSnapshotSplit{tableId=cdc-test.user, splitId='cdc-test.user:28', splitKeyType=[`id` INT NOT NULL], splitStart=[227165], splitEnd=[235278], highWatermark=null}
     */
    public MysqlCdcSource(List<MySqlSplit> splits) {
        super(splits);
    }

    public List<String> headers() {
        MySqlSplit mySqlSplit = splits.get(0);
        if (mySqlSplit.isBinlogSplit()) {
            return CollUtil.newArrayList(
                    "split-id", "starting-offset", "ending-offset", "table-schemas", "finished-snapshot-split-infos");
        } else if (mySqlSplit.isSnapshotSplit()) {
            return CollUtil.newArrayList(
                    "table-id", "split-id", "split-key-type", "split-start", "split-end", "high-watermark");
        }
        return null;
    }

    public List<?> datas() {
        return splits.stream()
                .map(d -> {
                    JSONObject jsonObject = new JSONObject();
                    if (d.isBinlogSplit()) {
                        MySqlBinlogSplit split = d.asBinlogSplit();
                        jsonObject.set("split-id", split.splitId());
                        jsonObject.set(
                                "starting-offset", split.getStartingOffset().toString());
                        jsonObject.set(
                                "ending-offset-id", split.getEndingOffset().toString());
                        String tableSchemas = split.getTableSchemas().entrySet().stream()
                                .map(e -> {
                                    JSONObject tableSchema = new JSONObject();
                                    tableSchema.set("table-id", e.getKey().identifier());
                                    tableSchema.set("table-schema", e.getValue().toString());
                                    return tableSchema;
                                })
                                .collect(Collectors.toCollection(JSONArray::new))
                                .toString();
                        jsonObject.set("table-schemas", tableSchemas);
                        jsonObject.set(
                                "finished-snapshot-split-infos",
                                JSONUtil.toJsonPrettyStr(split.getFinishedSnapshotSplitInfos()));
                    } else if (d.isSnapshotSplit()) {
                        MySqlSnapshotSplit split = d.asSnapshotSplit();
                        jsonObject.set("table-id", split.getTableId().identifier());
                        jsonObject.set("split-id", split.splitId());
                        jsonObject.set("split-key-type", split.getSplitKeyType().toString());
                        jsonObject.set("split-start", JSONUtil.toJsonStr(split.getSplitStart()));
                        jsonObject.set("split-end", JSONUtil.toJsonStr(split.getSplitEnd()));
                        jsonObject.set("high-watermark", StrUtil.toString(split.getHighWatermark()));
                    }

                    return jsonObject;
                })
                .collect(Collectors.toList());
    }
}
