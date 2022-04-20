package com.dlink.cdc.doris;

import org.apache.doris.flink.cfg.DorisExecutionOptions;
import org.apache.doris.flink.cfg.DorisOptions;
import org.apache.doris.flink.cfg.DorisReadOptions;
import org.apache.doris.flink.cfg.DorisSink;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.DecimalData;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.types.logical.BigIntType;
import org.apache.flink.table.types.logical.BooleanType;
import org.apache.flink.table.types.logical.DecimalType;
import org.apache.flink.table.types.logical.DoubleType;
import org.apache.flink.table.types.logical.FloatType;
import org.apache.flink.table.types.logical.IntType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.SmallIntType;
import org.apache.flink.table.types.logical.TinyIntType;
import org.apache.flink.table.types.logical.VarCharType;
import org.apache.flink.types.RowKind;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.executor.CustomTableEnvironment;
import com.dlink.model.Column;
import com.dlink.model.ColumnType;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;

/**
 * DorisSinkBuilder
 *
 * @author wenmo
 * @since 2022/4/20 19:20
 **/
public class DorisSinkBuilder extends AbstractSinkBuilder implements SinkBuilder, Serializable {

    private final static String KEY_WORD = "doris";
    private static final long serialVersionUID = 8330362249137471854L;

    public DorisSinkBuilder() {
    }

    public DorisSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new DorisSinkBuilder(config);
    }

    @Override
    public DataStreamSource build(CDCBuilder cdcBuilder, StreamExecutionEnvironment env, CustomTableEnvironment customTableEnvironment, DataStreamSource<String> dataStreamSource) {
        Map<String, String> sink = config.getSink();
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : sink.entrySet()) {
            if (Asserts.isNotNullString(entry.getKey()) && Asserts.isNotNullString(entry.getValue())) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }
        }
        DorisExecutionOptions.Builder dorisExecutionOptionsBuilder = DorisExecutionOptions.builder();
        if (sink.containsKey("sink.batch.size")) {
            dorisExecutionOptionsBuilder.setBatchSize(Integer.valueOf(sink.get("sink.batch.size")));
        }
        if (sink.containsKey("sink.batch.interval")) {
            dorisExecutionOptionsBuilder.setBatchIntervalMs(Long.valueOf(sink.get("sink.batch.interval")));
        }
        if (sink.containsKey("sink.max-retries")) {
            dorisExecutionOptionsBuilder.setMaxRetries(Integer.valueOf(sink.get("sink.max-retries")));
        }
        if (sink.containsKey("sink.enable-delete")) {
            dorisExecutionOptionsBuilder.setEnableDelete(Boolean.valueOf(sink.get("sink.enable-delete")));
        }
        dorisExecutionOptionsBuilder.setStreamLoadProp(properties);
        final List<Schema> schemaList = config.getSchemaList();
        final String schemaFieldName = config.getSchemaFieldName();
        if (Asserts.isNotNullCollection(schemaList)) {
            SingleOutputStreamOperator<Map> mapOperator = dataStreamSource.map(new MapFunction<String, Map>() {
                @Override
                public Map map(String value) throws Exception {
                    ObjectMapper objectMapper = new ObjectMapper();
                    return objectMapper.readValue(value, Map.class);
                }
            });
            for (Schema schema : schemaList) {
                for (Table table : schema.getTables()) {
                    final String tableName = table.getName();
                    final String schemaName = table.getSchema();
                    final String schemaTableName = table.getSchemaTableName();
                    List<String> columnNameList = new ArrayList<>();
                    List<LogicalType> columnTypeList = new ArrayList<>();
                    buildColumn(columnNameList, columnTypeList, table.getColumns());
                    final String[] columnNames = columnNameList.toArray(new String[columnNameList.size()]);
                    final LogicalType[] columnTypes = columnTypeList.toArray(new LogicalType[columnTypeList.size()]);
                    SingleOutputStreamOperator<Map> filterOperator = mapOperator.filter(new FilterFunction<Map>() {
                        @Override
                        public boolean filter(Map value) throws Exception {
                            LinkedHashMap source = (LinkedHashMap) value.get("source");
                            return tableName.equals(source.get("table").toString())
                                && schemaName.equals(source.get(schemaFieldName).toString());
                        }
                    });
                    DataStream<RowData> rowDataDataStream = filterOperator
                        .flatMap(new FlatMapFunction<Map, RowData>() {
                            @Override
                            public void flatMap(Map value, Collector<RowData> out) throws Exception {
                                switch (value.get("op").toString()) {
                                    case "r":
                                        GenericRowData igenericRowData = new GenericRowData(columnNameList.size());
                                        igenericRowData.setRowKind(RowKind.INSERT);
                                        Map idata = (Map) value.get("after");
                                        for (int i = 0; i < columnNameList.size(); i++) {
                                            igenericRowData.setField(i, convertValue(idata.get(columnNameList.get(i)), columnTypeList.get(i)));
                                        }
                                        out.collect(igenericRowData);
                                        break;
                                    case "d":
                                        GenericRowData dgenericRowData = new GenericRowData(columnNameList.size());
                                        dgenericRowData.setRowKind(RowKind.DELETE);
                                        Map ddata = (Map) value.get("before");
                                        for (int i = 0; i < columnNameList.size(); i++) {
                                            dgenericRowData.setField(i, convertValue(ddata.get(columnNameList.get(i)), columnTypeList.get(i)));
                                        }
                                        out.collect(dgenericRowData);
                                        break;
                                    case "u":
                                        GenericRowData ubgenericRowData = new GenericRowData(columnNameList.size());
                                        ubgenericRowData.setRowKind(RowKind.UPDATE_BEFORE);
                                        Map ubdata = (Map) value.get("before");
                                        for (int i = 0; i < columnNameList.size(); i++) {
                                            ubgenericRowData.setField(i, convertValue(ubdata.get(columnNameList.get(i)), columnTypeList.get(i)));
                                        }
                                        out.collect(ubgenericRowData);
                                        GenericRowData uagenericRowData = new GenericRowData(columnNameList.size());
                                        uagenericRowData.setRowKind(RowKind.UPDATE_AFTER);
                                        Map uadata = (Map) value.get("after");
                                        for (int i = 0; i < columnNameList.size(); i++) {
                                            uagenericRowData.setField(i, convertValue(uadata.get(columnNameList.get(i)), columnTypeList.get(i)));
                                        }
                                        out.collect(uagenericRowData);
                                        break;
                                }
                            }
                        });
                    rowDataDataStream.addSink(
                        DorisSink.sink(
                            columnNames,
                            columnTypes,
                            DorisReadOptions.builder().build(),
                            dorisExecutionOptionsBuilder.build(),
                            DorisOptions.builder()
                                .setFenodes(config.getSink().get("fenodes"))
                                .setTableIdentifier(schemaTableName)
                                .setUsername(config.getSink().get("username"))
                                .setPassword(config.getSink().get("password")).build()
                        ));
                }
            }
        }
        return dataStreamSource;
    }

    private void buildColumn(List<String> columnNameList, List<LogicalType> columnTypeList, List<Column> columns) {
        for (Column column : columns) {
            columnNameList.add(column.getName());
            columnTypeList.add(getLogicalType(column.getJavaType()));
        }
    }

    private LogicalType getLogicalType(ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return new VarCharType();
            case BOOLEAN:
            case JAVA_LANG_BOOLEAN:
                return new BooleanType();
            case BYTE:
            case JAVA_LANG_BYTE:
                return new TinyIntType();
            case SHORT:
            case JAVA_LANG_SHORT:
                return new SmallIntType();
            case LONG:
            case JAVA_LANG_LONG:
                return new BigIntType();
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return new FloatType();
            case DOUBLE:
            case JAVA_LANG_DOUBLE:
                return new DoubleType();
            case DECIMAL:
                return new DecimalType();
            case INT:
            case INTEGER:
                return new IntType();
            default:
                return new VarCharType();
        }
    }

    private Object convertValue(Object value, LogicalType logicalType) {
        if (logicalType instanceof VarCharType) {
            return StringData.fromString((String) value);
        } else if (logicalType instanceof DecimalType) {
            final DecimalType decimalType = ((DecimalType) logicalType);
            final int precision = decimalType.getPrecision();
            final int scala = decimalType.getScale();
            return DecimalData.fromBigDecimal((BigDecimal) value, precision, scala);
        } else {
            return value;
        }
    }
}
