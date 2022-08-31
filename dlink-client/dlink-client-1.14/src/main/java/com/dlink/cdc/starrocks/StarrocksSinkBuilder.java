package com.dlink.cdc.starrocks;

import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.model.Column;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Table;
import com.dlink.utils.ObjectConvertUtil;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.TimestampData;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.DateType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.TimestampType;
import org.apache.flink.table.types.utils.TypeConversions;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.starrocks.connector.flink.row.sink.StarRocksTableRowTransformer;
import com.starrocks.connector.flink.table.sink.StarRocksDynamicSinkFunction;
import com.starrocks.connector.flink.table.sink.StarRocksSinkOptions;

/**
 * StarrocksSinkBuilder
 *
 **/
public class StarrocksSinkBuilder extends AbstractSinkBuilder implements Serializable {

    private static final String KEY_WORD = "datastream-starrocks";
    private static final long serialVersionUID = 8330362249137431824L;
    private final ZoneId sinkZoneIdUTC = ZoneId.of("UTC");

    public StarrocksSinkBuilder() {
    }

    public StarrocksSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new StarrocksSinkBuilder(config);
    }

    @Override
    public void addSink(
        StreamExecutionEnvironment env,
        DataStream<RowData> rowDataDataStream,
        Table table,
        List<String> columnNameList,
        List<LogicalType> columnTypeList) {
        try {
            List<Column> columns = table.getColumns();
            List<String>  primaryKeys = new LinkedList<>();
            String[] columnNames = new String[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                if (column.isKeyFlag()) {
                    primaryKeys.add(column.getName());
                }
                columnNames[i] = column.getName();
            }
            String[] primaryKeyArrays = primaryKeys.stream().toArray(String[]::new);
            DataType[] dataTypes = new DataType[columnTypeList.size()];
            for (int i = 0; i < columnTypeList.size(); i++) {
                LogicalType logicalType = columnTypeList.get(i);
                String columnName = columnNameList.get(i);
                if (primaryKeys.contains(columnName)) {
                    logicalType = logicalType.copy(false);
                }
                dataTypes[i] = TypeConversions.fromLogicalToDataType(logicalType);
            }
            TableSchema tableSchema = TableSchema.builder().primaryKey(primaryKeyArrays).fields(columnNames, dataTypes).build();
            Map<String, String> sink = config.getSink();
            StarRocksSinkOptions.Builder builder = StarRocksSinkOptions.builder()
                    .withProperty("jdbc-url", sink.get("jdbc-url"))
                    .withProperty("load-url", sink.get("load-url"))
                    .withProperty("username", sink.get("username"))
                    .withProperty("password", sink.get("password"))
                    .withProperty("table-name", getSinkTableName(table))
                    .withProperty("database-name", getSinkSchemaName(table))
                    .withProperty("sink.properties.format", "json")
                    .withProperty("sink.properties.strip_outer_array", "true")
                    // 设置并行度，多并行度情况下需要考虑如何保证数据有序性
                    .withProperty("sink.parallelism", "1");
            sink.forEach((key,value) -> {
                if (key.startsWith("sink.")) {
                    builder.withProperty(key,value);
                }
            });
            StarRocksDynamicSinkFunction<RowData> starrocksSinkFunction = new StarRocksDynamicSinkFunction<RowData>(
                    builder.build(),
                    tableSchema,
                    new StarRocksTableRowTransformer(TypeInformation.of(RowData.class))
            );
            rowDataDataStream.addSink(starrocksSinkFunction);
            logger.info("handler connector name:{} sink successful.....",getHandle());
        } catch (Exception ex) {
            logger.error("handler connector name:{} sink ex:",getHandle(),ex);
        }
    }

    @Override
    protected Object convertValue(Object value, LogicalType logicalType) {
        Object object = ObjectConvertUtil.convertValue(value, logicalType, sinkZoneIdUTC);
        if (object == null) {
            return null;
        }
        if (logicalType instanceof TimestampType && object instanceof LocalDateTime) {
            return TimestampData.fromLocalDateTime((LocalDateTime) object);
        } else if (logicalType instanceof DateType) {
            if (value instanceof Integer) {
                return Instant.ofEpochSecond((int) value).atZone(sinkZoneIdUTC).toEpochSecond();
            }
            return Instant.ofEpochMilli((long) value).atZone(sinkZoneIdUTC).toEpochSecond();
        }
        return object;
    }
}
