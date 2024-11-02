package org.dinky.connector.mock.sink;

import org.apache.flink.api.common.accumulators.SerializedListAccumulator;
import org.apache.flink.api.common.typeutils.base.MapSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.logical.RowType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockSinkFunction extends RichSinkFunction<RowData> {
    private final RowType rowType;
    private final String tableIdentifier;
    // when columns is in VARCHAR or STRING type, rowData will be generated to BinaryStringData, which is not serialized, as a result, SerializedListAccumulator is used here
    private final SerializedListAccumulator<Map<String, String>> rowDataList;

    public MockSinkFunction(
            String tableName,
            RowType rowType) {
        this.rowType = rowType;
        this.tableIdentifier = tableName;
        this.rowDataList = new SerializedListAccumulator<>();
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        getRuntimeContext().addAccumulator(tableIdentifier, rowDataList);
        super.open(parameters);
    }

    @Override
    public void invoke(RowData rowData, Context context) throws Exception {
        List<String> fieldNames = rowType.getFieldNames();
        Map<String, String> rowDataMap = new HashMap<>();
        for (int i = 0; i < fieldNames.size(); i++) {
            RowData.FieldGetter fieldGetter = RowData.createFieldGetter(rowType.getTypeAt(i), i);
            rowDataMap.put(fieldNames.get(i), String.valueOf(fieldGetter.getFieldOrNull(rowData)));
        }
        rowDataList.add(rowDataMap, new MapSerializer<>(new StringSerializer(), new StringSerializer()));
    }
}