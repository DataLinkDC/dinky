package com.dlink.cdc.jdbc;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.logical.LogicalType;

import java.util.List;

import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.model.FlinkCDCConfig;

/**
 * MysqlCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:29
 **/
public class JdbcSinkBuilder extends AbstractSinkBuilder implements SinkBuilder {

    private final static String KEY_WORD = "jdbc";
    private final static String TABLE_NAME = "cdc_table";

    public JdbcSinkBuilder() {
    }

    public JdbcSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public void addSink(DataStream<RowData> rowDataDataStream, String schemaTableName, List<String> columnNameList, List<LogicalType> columnTypeList) {

    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new JdbcSinkBuilder(config);
    }

}
