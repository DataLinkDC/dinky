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

package org.dinky.utils;

import org.dinky.data.vo.MetricsVO;
import org.dinky.function.constant.PathConstant;

import org.apache.paimon.catalog.Catalog;
import org.apache.paimon.catalog.CatalogContext;
import org.apache.paimon.catalog.CatalogFactory;
import org.apache.paimon.catalog.Identifier;
import org.apache.paimon.data.BinaryRow;
import org.apache.paimon.data.BinaryRowWriter;
import org.apache.paimon.data.BinaryString;
import org.apache.paimon.data.InternalRow;
import org.apache.paimon.data.Timestamp;
import org.apache.paimon.fs.Path;
import org.apache.paimon.predicate.Predicate;
import org.apache.paimon.predicate.PredicateBuilder;
import org.apache.paimon.reader.RecordReader;
import org.apache.paimon.schema.Schema;
import org.apache.paimon.table.Table;
import org.apache.paimon.table.sink.BatchTableCommit;
import org.apache.paimon.table.sink.BatchTableWrite;
import org.apache.paimon.table.sink.BatchWriteBuilder;
import org.apache.paimon.table.sink.CommitMessage;
import org.apache.paimon.table.source.ReadBuilder;
import org.apache.paimon.table.source.Split;
import org.apache.paimon.table.source.TableRead;
import org.apache.paimon.types.DataTypes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaimonUtil {
    private static final String DINKY_DB = "dinky_db";
    private static final Map<Identifier, Schema> SCHEMA_MAP = new HashMap<>();
    private static final CatalogContext CONTEXT =
            CatalogContext.create(
                    new Path(URLUtil.toURI(URLUtil.url(PathConstant.TMP_PATH + "paimon"))));
    private static final Catalog CATALOG = CatalogFactory.createCatalog(CONTEXT);
    public static final Identifier METRICS_IDENTIFIER =
            Identifier.create(DINKY_DB, "dinky_metrics");

    static {
        try {
            CATALOG.createDatabase(DINKY_DB, true);
        } catch (Catalog.DatabaseAlreadyExistException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> options = new HashMap<>();
        options.put("partition.expiration-time", "7d");
        options.put("partition.expiration-check-interval", "1d");
        options.put("partition.timestamp-formatter", "yyyy-MM-dd");
        options.put("partition.timestamp-pattern", "$date");
        options.put("file.format", "parquet");
        options.put("snapshot.time-retained", "10 s");

        Schema.Builder schemaBuilder = Schema.newBuilder();
        schemaBuilder.primaryKey("heart_time", "model", "date");
        schemaBuilder.partitionKeys("model", "date");
        schemaBuilder.column("heart_time", DataTypes.TIMESTAMP_MILLIS());
        schemaBuilder.column("model", DataTypes.STRING());
        schemaBuilder.column("content", DataTypes.STRING());
        schemaBuilder.column("date", DataTypes.STRING());
        schemaBuilder.options(options);
        Schema schema = schemaBuilder.build();
        SCHEMA_MAP.put(METRICS_IDENTIFIER, schema);
    }

    public static synchronized void writeMetrics(List<MetricsVO> metricsList) {
        if (CollUtil.isEmpty(metricsList)) {
            return;
        }
        Table metricsTable = createOrGetMetricsTable();
        BatchWriteBuilder writeBuilder = metricsTable.newBatchWriteBuilder();

        // 2. Write records in distributed tasks

        try (BatchTableWrite write = writeBuilder.newWrite()) {
            for (MetricsVO metrics : metricsList) {
                LocalDateTime now = metrics.getHeartTime();

                BinaryRow row = new BinaryRow(30);
                BinaryRowWriter writer = new BinaryRowWriter(row);
                writer.writeTimestamp(0, Timestamp.fromLocalDateTime(now), 0);
                writer.writeString(1, BinaryString.fromString(metrics.getModel()));
                writer.writeString(2, BinaryString.fromString(metrics.getContent()));
                writer.writeString(
                        3,
                        BinaryString.fromString(
                                now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                write.write(row);
            }
            List<CommitMessage> messages = write.prepareCommit();

            // 3. Collect all CommitMessages to a global node and commit
            try (BatchTableCommit commit = writeBuilder.newCommit()) {
                commit.commit(messages);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> batchReadTable(Identifier identifier, Class<T> clazz) {
        return batchReadTable(identifier, clazz, null);
    }

    public static <T> List<T> batchReadTable(
            Identifier identifier,
            Class<T> clazz,
            Function<PredicateBuilder, List<Predicate>> filter) {
        TimeInterval timer = DateUtil.timer();
        List<T> dataList = new ArrayList<>();

        PredicateBuilder builder = new PredicateBuilder(SCHEMA_MAP.get(identifier).rowType());

        ReadBuilder readBuilder;
        try {
            if (!CATALOG.tableExists(identifier)) {
                return dataList;
            }
            readBuilder = CATALOG.getTable(identifier).newReadBuilder();
            if (filter != null) {
                List<Predicate> predicates = filter.apply(builder);
                readBuilder.withFilter(predicates);
            }
        } catch (Catalog.TableNotExistException e) {
            throw new RuntimeException(e);
        }

        // 2. Plan splits in 'Coordinator' (or named 'Driver')
        List<Split> splits = readBuilder.newScan().plan().splits();

        // 3. Distribute these splits to different tasks

        // 4. Read a split in task
        TableRead read = readBuilder.newRead();
        try (RecordReader<InternalRow> reader = read.createReader(splits)) {

            Schema schema = SCHEMA_MAP.get(METRICS_IDENTIFIER);
            reader.forEachRemaining(
                    x -> {
                        T t = ReflectUtil.newInstance(clazz);
                        schema.fields()
                                .forEach(
                                        f -> {
                                            Object value =
                                                    InternalRow.createFieldGetter(f.type(), f.id())
                                                            .getFieldOrNull(x);
                                            String fieldName = StrUtil.toCamelCase(f.name());
                                            try {
                                                ReflectUtil.setFieldValue(t, fieldName, value);
                                            } catch (Exception ignored) {
                                            }
                                        });
                        dataList.add(t);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.debug(
                "paimon read; table: {} ,size: {} ,timer: {}ms",
                identifier.getFullName(),
                dataList.size(),
                timer.intervalMs());
        return dataList;
    }

    public static Table createOrGetMetricsTable() {
        try {
            if (CATALOG.tableExists(METRICS_IDENTIFIER)) {
                return CATALOG.getTable(METRICS_IDENTIFIER);
            }

            CATALOG.createTable(METRICS_IDENTIFIER, SCHEMA_MAP.get(METRICS_IDENTIFIER), false);
            return CATALOG.getTable(METRICS_IDENTIFIER);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
