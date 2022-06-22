package com.dlink.cdc.sql;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.executor.CustomTableEnvironment;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import com.dlink.utils.FlinkBaseUtil;
import com.dlink.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.types.logical.*;
import org.apache.flink.table.types.utils.TypeConversions;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SQLSinkBuilder
 *
 * @author wenmo
 * @since 2022/4/25 23:02
 */
public class SQLSinkBuilder extends AbstractSinkBuilder implements SinkBuilder, Serializable {

    private final static String KEY_WORD = "sql";
    private static final long serialVersionUID = -3699685106324048226L;

    public SQLSinkBuilder() {
    }

    private SQLSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public void addSink(StreamExecutionEnvironment env, DataStream<RowData> rowDataDataStream, Table table, List<String> columnNameList, List<LogicalType> columnTypeList) {

    }

    private DataStream<Row> buildRow(
        SingleOutputStreamOperator<Map> filterOperator,
        List<String> columnNameList,
        List<LogicalType> columnTypeList) {
        final String[] columnNames = columnNameList.toArray(new String[columnNameList.size()]);
        final LogicalType[] columnTypes = columnTypeList.toArray(new LogicalType[columnTypeList.size()]);

        TypeInformation<?>[] typeInformations = TypeConversions.fromDataTypeToLegacyInfo(TypeConversions.fromLogicalToDataType(columnTypes));
        RowTypeInfo rowTypeInfo = new RowTypeInfo(typeInformations, columnNames);

        return filterOperator
            .flatMap(new FlatMapFunction<Map, Row>() {
                @Override
                public void flatMap(Map value, Collector<Row> out) throws Exception {
                    switch (value.get("op").toString()) {
                        case "r":
                        case "c":
                            Row irow = Row.ofKind(RowKind.INSERT);
                            Map idata = (Map) value.get("after");
                            for (int i = 0; i < columnNameList.size(); i++) {
                                irow.setField(i, convertValue(idata.get(columnNameList.get(i)), columnTypeList.get(i)));
                            }
                            out.collect(irow);
                            break;
                        case "d":
                            Row drow = Row.ofKind(RowKind.DELETE);
                            Map ddata = (Map) value.get("before");
                            for (int i = 0; i < columnNameList.size(); i++) {
                                drow.setField(i, convertValue(ddata.get(columnNameList.get(i)), columnTypeList.get(i)));
                            }
                            out.collect(drow);
                            break;
                        case "u":
                            Row ubrow = Row.ofKind(RowKind.UPDATE_BEFORE);
                            Map ubdata = (Map) value.get("before");
                            for (int i = 0; i < columnNameList.size(); i++) {
                                ubrow.setField(i, convertValue(ubdata.get(columnNameList.get(i)), columnTypeList.get(i)));
                            }
                            out.collect(ubrow);
                            Row uarow = Row.ofKind(RowKind.UPDATE_AFTER);
                            Map uadata = (Map) value.get("after");
                            for (int i = 0; i < columnNameList.size(); i++) {
                                uarow.setField(i, convertValue(uadata.get(columnNameList.get(i)), columnTypeList.get(i)));
                            }
                            out.collect(uarow);
                            break;
                    }
                }
            }, rowTypeInfo);
    }

    private void addTableSink(
        CustomTableEnvironment customTableEnvironment,
        DataStream<Row> rowDataDataStream,
        Table table,
        List<String> columnNameList) {

        String sinkSchemaName = getSinkSchemaName(table);
        String sinkTableName = getSinkTableName(table);
        String pkList = StringUtils.join(getPKList(table), ".");
        String viewName = "VIEW_" + table.getSchemaTableNameWithUnderline();
        customTableEnvironment.createTemporaryView(viewName, rowDataDataStream, StringUtils.join(columnNameList, ","));
        logger.info("Create " + viewName + " temporaryView successful...");
        String flinkDDL = FlinkBaseUtil.getFlinkDDL(table, sinkTableName, config, sinkSchemaName, sinkTableName, pkList);
        logger.info(flinkDDL);
        customTableEnvironment.executeSql(flinkDDL);
        logger.info("Create " + sinkTableName + " FlinkSQL DDL successful...");
        String cdcSqlInsert = FlinkBaseUtil.getCDCSqlInsert(table, sinkTableName, viewName, config);
        logger.info(cdcSqlInsert);
        List<Operation> operations = customTableEnvironment.getParser().parse(cdcSqlInsert);
        logger.info("Create " + sinkTableName + " FlinkSQL insert into successful...");
        if (operations.size() > 0) {
            Operation operation = operations.get(0);
            if (operation instanceof ModifyOperation) {
                modifyOperations.add((ModifyOperation) operation);
            }
        }
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new SQLSinkBuilder(config);
    }

    @Override
    public DataStreamSource build(
        CDCBuilder cdcBuilder,
        StreamExecutionEnvironment env,
        CustomTableEnvironment customTableEnvironment,
        DataStreamSource<String> dataStreamSource) {
        final List<Schema> schemaList = config.getSchemaList();
        final String schemaFieldName = config.getSchemaFieldName();
        if (Asserts.isNotNullCollection(schemaList)) {
            SingleOutputStreamOperator<Map> mapOperator = deserialize(dataStreamSource);
            logger.info("Build deserialize successful...");
            for (Schema schema : schemaList) {
                for (Table table : schema.getTables()) {
                    try {
                        SingleOutputStreamOperator<Map> filterOperator = shunt(mapOperator, table, schemaFieldName);
                        logger.info("Build " + table.getSchemaTableName() + " shunt successful...");
                        List<String> columnNameList = new ArrayList<>();
                        List<LogicalType> columnTypeList = new ArrayList<>();
                        buildColumn(columnNameList, columnTypeList, table.getColumns());
                        DataStream<Row> rowDataDataStream = buildRow(filterOperator, columnNameList, columnTypeList);
                        logger.info("Build " + table.getSchemaTableName() + " flatMap successful...");
                        logger.info("Start build " + table.getSchemaTableName() + " sink...");
                        addTableSink(customTableEnvironment, rowDataDataStream, table, columnNameList);
                    } catch (Exception e) {
                        logger.error("Build " + table.getSchemaTableName() + " cdc sync failed...");
                        logger.error(LogUtil.getError(e));
                    }
                }
            }
            List<Transformation<?>> trans = customTableEnvironment.getPlanner().translate(modifyOperations);
            for (Transformation<?> item : trans) {
                env.addOperator(item);
            }
            logger.info("A total of " + trans.size() + " table cdc sync were build successfull...");
        }
        return dataStreamSource;
    }

    protected Object convertValue(Object value, LogicalType logicalType) {
        if (value == null) {
            return null;
        }
        if (logicalType instanceof DateType) {
            if(value instanceof Integer){
                return Instant.ofEpochMilli(((Integer) value).longValue()).atZone(ZoneId.systemDefault()).toLocalDate();
            }else {
                return Instant.ofEpochMilli((long) value).atZone(ZoneId.systemDefault()).toLocalDate();
            }
        } else if (logicalType instanceof TimestampType) {
            if(value instanceof Integer){
                return Instant.ofEpochMilli(((Integer) value).longValue()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            }else {
                return Instant.ofEpochMilli((long) value).atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
        } else if (logicalType instanceof DecimalType) {
            return new BigDecimal((String) value);
        } else if (logicalType instanceof BigIntType) {
            if(value instanceof Integer){
                return ((Integer) value).longValue();
            }else {
                return value;
            }
        } else {
            return value;
        }
    }
}
