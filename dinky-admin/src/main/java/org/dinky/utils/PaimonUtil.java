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

import static org.dinky.data.constant.PaimonTableConstant.DINKY_DB;

import org.dinky.data.annotations.paimon.Option;
import org.dinky.data.annotations.paimon.Options;
import org.dinky.data.annotations.paimon.PartitionKey;
import org.dinky.data.annotations.paimon.PrimaryKey;
import org.dinky.function.constant.PathConstant;
import org.dinky.shaded.paimon.catalog.Catalog;
import org.dinky.shaded.paimon.catalog.CatalogContext;
import org.dinky.shaded.paimon.catalog.CatalogFactory;
import org.dinky.shaded.paimon.catalog.Identifier;
import org.dinky.shaded.paimon.data.BinaryRow;
import org.dinky.shaded.paimon.data.BinaryRowWriter;
import org.dinky.shaded.paimon.data.BinaryString;
import org.dinky.shaded.paimon.data.BinaryWriter;
import org.dinky.shaded.paimon.data.InternalRow;
import org.dinky.shaded.paimon.data.Timestamp;
import org.dinky.shaded.paimon.fs.Path;
import org.dinky.shaded.paimon.predicate.Predicate;
import org.dinky.shaded.paimon.predicate.PredicateBuilder;
import org.dinky.shaded.paimon.reader.RecordReader;
import org.dinky.shaded.paimon.schema.Schema;
import org.dinky.shaded.paimon.table.Table;
import org.dinky.shaded.paimon.table.sink.BatchTableCommit;
import org.dinky.shaded.paimon.table.sink.BatchTableWrite;
import org.dinky.shaded.paimon.table.sink.BatchWriteBuilder;
import org.dinky.shaded.paimon.table.sink.CommitMessage;
import org.dinky.shaded.paimon.table.source.ReadBuilder;
import org.dinky.shaded.paimon.table.source.Split;
import org.dinky.shaded.paimon.table.source.TableRead;
import org.dinky.shaded.paimon.types.DataField;
import org.dinky.shaded.paimon.types.DataType;
import org.dinky.shaded.paimon.types.DataTypeRoot;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class PaimonUtil {

    private static PaimonUtil instance;

    private final Cache<Class<?>, Schema> schemaCache;
    private final CatalogContext context;
    private final Catalog catalog;

    public PaimonUtil() {
        schemaCache = CacheUtil.newLRUCache(100);
        context = CatalogContext.create(new Path(URLUtil.toURI(URLUtil.url(PathConstant.TMP_PATH + "paimon"))));
        catalog = CatalogFactory.createCatalog(context);
        try {
            catalog.createDatabase(DINKY_DB, true);
        } catch (Catalog.DatabaseAlreadyExistException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized PaimonUtil getInstance() {
        if (instance == null) {
            instance = new PaimonUtil();
        }
        return instance;
    }

    public static void dropTable(String table) {
        Identifier identifier = Identifier.create(DINKY_DB, table);
        if (getInstance().getCatalog().tableExists(identifier)) {
            try {
                getInstance().getCatalog().dropTable(identifier, true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> void write(String table, List<T> dataList, Class<?> clazz) {
        if (CollUtil.isEmpty(dataList)) {
            return;
        }
        Table paimonTable = createOrGetTable(table, clazz);
        BatchWriteBuilder writeBuilder = paimonTable.newBatchWriteBuilder();

        // 2. Write records in distributed tasks
        try (BatchTableWrite write = writeBuilder.newWrite()) {
            Schema schema = getSchemaByClass(clazz);
            List<DataField> fields = schema.fields();
            for (T t : dataList) {
                BinaryRow row = new BinaryRow(fields.size());
                BinaryRowWriter writer = new BinaryRowWriter(row);
                for (int i = 0; i < fields.size(); i++) {
                    DataField dataField = fields.get(i);
                    DataType type = dataField.type();
                    String fieldName = StrUtil.toCamelCase(dataField.name());
                    Object fieldValue = ReflectUtil.getFieldValue(t, fieldName);
                    try {
                        // TODO BinaryWriter.write已被废弃，后续可以考虑改成这种方式
                        // BinaryWriter.createValueSetter(type).setValue(writer, i, fieldValue);
                        if (type.getTypeRoot() == DataTypeRoot.VARCHAR) {
                            BinaryWriter.write(
                                    writer, i, BinaryString.fromString(JSONUtil.toJsonStr(fieldValue)), type, null);
                        } else if (type.getTypeRoot() == DataTypeRoot.TIMESTAMP_WITHOUT_TIME_ZONE) {
                            Timestamp timestamp = Timestamp.fromLocalDateTime((LocalDateTime) fieldValue);
                            BinaryWriter.write(writer, i, timestamp, type, null);
                        } else {
                            BinaryWriter.write(writer, i, fieldValue, type, null);
                        }
                    } catch (Throwable e) {
                        String err = StrFormatter.format(
                                "write table: [{}], data filed [{}], value: [{}] error",
                                paimonTable.name(),
                                fieldName,
                                fieldValue);
                        throw new RuntimeException(err, e);
                    }
                }
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

    public static <T> List<T> batchReadTable(String table, Class<T> clazz) {
        return batchReadTable(table, clazz, null);
    }

    public static <T> List<T> batchReadTable(
            String table, Class<T> clazz, Function<PredicateBuilder, List<Predicate>> filter) {
        Identifier identifier = getIdentifier(table);
        TimeInterval timer = DateUtil.timer();
        List<T> dataList = new ArrayList<>();

        PredicateBuilder builder = new PredicateBuilder(getSchemaByClass(clazz).rowType());

        ReadBuilder readBuilder;
        try {
            if (!getInstance().getCatalog().tableExists(identifier)) {
                return dataList;
            }
            readBuilder = getInstance().getCatalog().getTable(identifier).newReadBuilder();
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

            Schema schema = getSchemaByClass(clazz);
            reader.forEachRemaining(x -> {
                T t = ReflectUtil.newInstance(clazz);
                schema.fields().forEach(f -> {
                    Object value =
                            InternalRow.createFieldGetter(f.type(), f.id()).getFieldOrNull(x);
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

    public static Table createOrGetTable(String tableName, Class<?> clazz) {
        try {
            Identifier identifier = Identifier.create(DINKY_DB, tableName);
            if (getInstance().getCatalog().tableExists(identifier)) {
                return getInstance().getCatalog().getTable(identifier);
            }
            getInstance().getCatalog().createTable(identifier, getSchemaByClass(clazz), false);
            return getInstance().getCatalog().getTable(identifier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Schema getSchemaByClass(Class<?> clazz) {
        return getInstance().getSchemaCache().get(clazz, () -> {
            List<String> primaryKeys = new ArrayList<>();
            List<String> partitionKeys = new ArrayList<>();
            Schema.Builder builder = Schema.newBuilder();
            Field[] fields = ReflectUtil.getFields(clazz, field -> !ModifierUtil.isStatic(field));
            for (Field field : fields) {
                String fieldName = StrUtil.toUnderlineCase(field.getName());
                if (field.getAnnotations().length > 0) {
                    if (AnnotationUtil.hasAnnotation(field, PartitionKey.class)) {
                        partitionKeys.add(fieldName);
                    }
                    if (AnnotationUtil.hasAnnotation(field, PrimaryKey.class)) {
                        primaryKeys.add(fieldName);
                    }
                }

                Class<?> type = field.getType();
                DataType dataType = PaimonTypeUtil.classToDataType(type);
                builder.column(fieldName, dataType);
            }
            // get options
            Options options = AnnotationUtil.getAnnotation(clazz, Options.class);
            if (options != null) {
                for (Option option : options.value()) {
                    builder.option(option.key(), option.value());
                }
            } else {
                // default options
                Map<String, String> defaultOptions =
                        MapUtil.builder("file.format", "parquet").build();
                builder.options(defaultOptions);
            }
            // builder schema;
            return builder.partitionKeys(partitionKeys).primaryKey(primaryKeys).build();
        });
    }

    public static Identifier getIdentifier(String tableName) {
        return Identifier.create(DINKY_DB, tableName);
    }
}
