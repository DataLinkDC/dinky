package org.dinky.cdc.sql;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.utils.TypeConversions;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;
import org.apache.flink.util.Collector;
import org.dinky.cdc.AbstractSinkBuilder;
import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.utils.JSONUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class AbstractSqlSinkBuilder extends AbstractSinkBuilder implements Serializable {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected AbstractSqlSinkBuilder() {
    }

    protected AbstractSqlSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @SuppressWarnings("rawtypes")
    protected FlatMapFunction<Map, Row> sqlSinkFunction(List<String> columnNameList, List<LogicalType> columnTypeList
            , String schemaTableName) {
        return (value, out) -> {
            try {
                Map after = (Map) value.get("after");
                Map before = (Map) value.get("before");
                switch (value.get("op").toString()) {
                    case "r":
                    case "c":
                        collect(columnNameList, columnTypeList, out, RowKind.INSERT, after);
                        break;
                    case "d":
                        collect(columnNameList, columnTypeList, out, RowKind.DELETE, before);
                        break;
                    case "u":
                        collect(columnNameList, columnTypeList, out, RowKind.UPDATE_BEFORE, before);
                        collect(columnNameList, columnTypeList, out, RowKind.UPDATE_AFTER, after);
                        break;
                    default:
                }
            } catch (Exception e) {
                logger.error(
                        "SchemaTable: {} - Row: {} - Exception {}",
                        schemaTableName,
                        JSONUtil.toJsonString(value),
                        e.toString());
                throw e;
            }
        };
    }

    @SuppressWarnings("rawtypes")
    private void collect(List<String> columnNameList, List<LogicalType> columnTypeList, Collector<Row> out,
                         RowKind rowKind, Map value) {
        Row row = Row.withPositions(rowKind, columnNameList.size());
        for (int i = 0; i < columnNameList.size(); i++) {
            row.setField(i,
                    convertValue(
                            value.get(columnNameList.get(i)),
                            columnTypeList.get(i)));
        }
        out.collect(row);
    }

    @SuppressWarnings("rawtypes")
    protected DataStream<Row> buildRow(
            DataStream<Map> filterOperator,
            List<String> columnNameList,
            List<LogicalType> columnTypeList,
            String schemaTableName) {
        TypeInformation<?>[] typeInformation =
                TypeConversions.fromDataTypeToLegacyInfo(
                        TypeConversions.fromLogicalToDataType(columnTypeList.toArray(new LogicalType[0])));

        return filterOperator.flatMap(
                sqlSinkFunction(columnNameList, columnTypeList, schemaTableName),
                new RowTypeInfo(typeInformation, columnNameList.toArray(new String[0])));
    }
}
