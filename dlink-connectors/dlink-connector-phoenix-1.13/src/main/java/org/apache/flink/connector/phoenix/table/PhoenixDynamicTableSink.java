package org.apache.flink.connector.phoenix.table;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.connector.phoenix.JdbcExecutionOptions;
import org.apache.flink.connector.phoenix.internal.GenericJdbcSinkFunction;
import org.apache.flink.connector.phoenix.internal.options.JdbcDmlOptions;
import org.apache.flink.connector.phoenix.internal.options.PhoenixJdbcOptions;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.data.RowData;
import org.apache.flink.types.RowKind;
import org.apache.flink.util.Preconditions;

import java.util.Objects;

/**
 * PhoenixDynamicTableSink
 *
 * @author gy
 * @since 2022/3/17 11:39
 **/
public class PhoenixDynamicTableSink implements DynamicTableSink  {
    private final PhoenixJdbcOptions jdbcOptions;
    private final JdbcExecutionOptions executionOptions;
    private final JdbcDmlOptions dmlOptions;
    private final TableSchema tableSchema;
    private final String dialectName;

    public PhoenixDynamicTableSink(PhoenixJdbcOptions jdbcOptions, JdbcExecutionOptions executionOptions, JdbcDmlOptions dmlOptions, TableSchema tableSchema) {
        this.jdbcOptions = jdbcOptions;
        this.executionOptions = executionOptions;
        this.dmlOptions = dmlOptions;
        this.tableSchema = tableSchema;
        this.dialectName = dmlOptions.getDialect().dialectName();
    }

    @Override
    public ChangelogMode getChangelogMode(ChangelogMode requestedMode) {
        this.validatePrimaryKey(requestedMode);
        return ChangelogMode.newBuilder().addContainedKind(RowKind.INSERT).addContainedKind(RowKind.DELETE).addContainedKind(RowKind.UPDATE_AFTER).build();
    }


    private void validatePrimaryKey(ChangelogMode requestedMode) {
        Preconditions.checkState(ChangelogMode.insertOnly().equals(requestedMode) || this.dmlOptions.getKeyFields().isPresent(), "please declare primary key for sink table when query contains update/delete record.");
    }

    @Override
    public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {
        TypeInformation<RowData> rowDataTypeInformation = context.createTypeInformation(this.tableSchema.toRowDataType());
        PhoenixJdbcDynamicOutputFormatBuilder builder = new PhoenixJdbcDynamicOutputFormatBuilder();
        builder.setJdbcOptions(this.jdbcOptions);
        builder.setJdbcDmlOptions(this.dmlOptions);
        builder.setJdbcExecutionOptions(this.executionOptions);
        builder.setRowDataTypeInfo(rowDataTypeInformation);
        builder.setFieldDataTypes(this.tableSchema.getFieldDataTypes());
        return SinkFunctionProvider.of(new GenericJdbcSinkFunction(builder.build()), this.jdbcOptions.getParallelism());
    }

    @Override
    public DynamicTableSink copy() {
        return new PhoenixDynamicTableSink(this.jdbcOptions, this.executionOptions, this.dmlOptions, this.tableSchema);
    }

    @Override
    public String asSummaryString() {
        return "Phoenix Table Sink " ;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof PhoenixDynamicTableSink)) {
            return false;
        } else {
            PhoenixDynamicTableSink that = (PhoenixDynamicTableSink)o;
            return Objects.equals(this.jdbcOptions, that.jdbcOptions) && Objects.equals(this.executionOptions, that.executionOptions) && Objects.equals(this.dmlOptions, that.dmlOptions) && Objects.equals(this.tableSchema, that.tableSchema) && Objects.equals(this.dialectName, that.dialectName);
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.jdbcOptions, this.executionOptions, this.dmlOptions, this.tableSchema, this.dialectName});
    }
}
