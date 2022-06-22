package org.apache.flink.connector.phoenix.table;


import org.apache.flink.connector.phoenix.dialect.JdbcDialect;
import org.apache.flink.connector.phoenix.internal.options.JdbcLookupOptions;
import org.apache.flink.connector.phoenix.internal.options.JdbcReadOptions;
import org.apache.flink.connector.phoenix.internal.options.PhoenixJdbcOptions;
import org.apache.flink.connector.phoenix.split.JdbcNumericBetweenParametersProvider;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.source.*;
import org.apache.flink.table.connector.source.abilities.SupportsLimitPushDown;
import org.apache.flink.table.connector.source.abilities.SupportsProjectionPushDown;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.table.utils.TableSchemaUtils;
import org.apache.flink.util.Preconditions;

import java.util.Objects;

/**
 * PhoenixDynamicTableSource
 *
 * @author gy
 * @since 2022/3/17 10:40
 **/
public class PhoenixDynamicTableSource implements ScanTableSource, LookupTableSource, SupportsProjectionPushDown,
        SupportsLimitPushDown {

    private final PhoenixJdbcOptions options;
    private final JdbcReadOptions readOptions;
    private final JdbcLookupOptions lookupOptions;
    private TableSchema physicalSchema;
    private final String dialectName;
    private long limit = -1L;

    public PhoenixDynamicTableSource(PhoenixJdbcOptions options, JdbcReadOptions readOptions, JdbcLookupOptions lookupOptions, TableSchema physicalSchema) {
        this.options = options;
        this.readOptions = readOptions;
        this.lookupOptions = lookupOptions;
        this.physicalSchema = physicalSchema;
        this.dialectName = options.getDialect().dialectName();

    }

    @Override
    public LookupRuntimeProvider getLookupRuntimeProvider(LookupContext context) {
        // JDBC only support non-nested look up keys
        String[] keyNames = new String[context.getKeys().length];
        for (int i = 0; i < keyNames.length; i++) {
            int[] innerKeyArr = context.getKeys()[i];
            Preconditions.checkArgument(
                    innerKeyArr.length == 1, "JDBC only support non-nested look up keys");
            keyNames[i] = physicalSchema.getFieldNames()[innerKeyArr[0]];
        }
        final RowType rowType = (RowType) physicalSchema.toRowDataType().getLogicalType();

        return TableFunctionProvider.of(
                new PhoenixRowDataLookupFunction(
                        options,
                        lookupOptions,
                        physicalSchema.getFieldNames(),
                        physicalSchema.getFieldDataTypes(),
                        keyNames,
                        rowType));
    }

    @Override
    public ScanRuntimeProvider getScanRuntimeProvider(ScanContext runtimeProviderContext) {
        PhoenixJdbcRowDataInputFormat.Builder builder = PhoenixJdbcRowDataInputFormat.builder()
                .setDrivername(this.options.getDriverName())
                .setDBUrl(this.options.getDbURL())
                .setUsername((String)this.options.getUsername().orElse((String) null))
                .setPassword((String)this.options.getPassword().orElse((String) null))
                .setAutoCommit(this.readOptions.getAutoCommit())
                //setting phoenix schema
                .setNamespaceMappingEnabled(this.options.getNamespaceMappingEnabled())
                .setMapSystemTablesToNamespace(this.options.getMapSystemTablesToNamespace())
                ;

        if (this.readOptions.getFetchSize() != 0) {
            builder.setFetchSize(this.readOptions.getFetchSize());
        }

        JdbcDialect dialect = this.options.getDialect();
        String query = dialect.getSelectFromStatement(this.options.getTableName(), this.physicalSchema.getFieldNames(), new String[0]);
        if (this.readOptions.getPartitionColumnName().isPresent()) {
            long lowerBound = (Long)this.readOptions.getPartitionLowerBound().get();
            long upperBound = (Long)this.readOptions.getPartitionUpperBound().get();
            int numPartitions = (Integer)this.readOptions.getNumPartitions().get();
            builder.setParametersProvider((new JdbcNumericBetweenParametersProvider(lowerBound, upperBound)).ofBatchNum(numPartitions));
            query = query + " WHERE " + dialect.quoteIdentifier((String)this.readOptions.getPartitionColumnName().get()) + " BETWEEN ? AND ?";
        }

        if (this.limit >= 0L) {
            query = String.format("%s %s", query, dialect.getLimitClause(this.limit));
        }

        builder.setQuery(query);
        RowType rowType = (RowType)this.physicalSchema.toRowDataType().getLogicalType();
        builder.setRowConverter(dialect.getRowConverter(rowType));
        builder.setRowDataTypeInfo(runtimeProviderContext.createTypeInformation(this.physicalSchema.toRowDataType()));
        return InputFormatProvider.of(builder.build());
    }

    @Override
    public ChangelogMode getChangelogMode() {
        return ChangelogMode.insertOnly();
    }
    @Override
    public boolean supportsNestedProjection() {
        return false;
    }
    @Override
    public void applyProjection(int[][] projectedFields) {
        this.physicalSchema = TableSchemaUtils.projectSchema(this.physicalSchema, projectedFields);
    }

    public DynamicTableSource copy() {
        return new PhoenixDynamicTableSource(this.options, this.readOptions, this.lookupOptions, this.physicalSchema);
    }

    public String asSummaryString() {
        return "JDBC:" + this.dialectName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof PhoenixDynamicTableSource)) {
            return false;
        } else {
            PhoenixDynamicTableSource that = (PhoenixDynamicTableSource)o;
            return Objects.equals(this.options, that.options)  && Objects.equals(this.physicalSchema, that.physicalSchema) && Objects.equals(this.dialectName, that.dialectName) && Objects.equals(this.limit, that.limit);
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.options, this.readOptions, this.lookupOptions, this.physicalSchema, this.dialectName, this.limit});
    }

    public void applyLimit(long limit) {
        this.limit = limit;
    }
}
