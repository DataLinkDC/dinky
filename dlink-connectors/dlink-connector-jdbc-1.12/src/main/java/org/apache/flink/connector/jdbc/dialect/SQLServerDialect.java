package org.apache.flink.connector.jdbc.dialect;

import org.apache.flink.connector.jdbc.internal.converter.JdbcRowConverter;
import org.apache.flink.connector.jdbc.internal.converter.SQLServerRowConverter;
import org.apache.flink.table.types.logical.LogicalTypeRoot;
import org.apache.flink.table.types.logical.RowType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SQLServerDialect
 *
 * @author wenmo
 * @since 2021/12/9
 **/
public class SQLServerDialect extends AbstractDialect {
    private static final long serialVersionUID = 1L;

    private static final int MAX_TIMESTAMP_PRECISION = 6;
    private static final int MIN_TIMESTAMP_PRECISION = 1;

    private static final int MAX_DECIMAL_PRECISION = 65;
    private static final int MIN_DECIMAL_PRECISION = 1;

    // jdbc:sqlserver://127.0.0.1:1433;DatabaseName=test
    @Override
    public boolean canHandle(String url) {
        return url.startsWith("jdbc:sqlserver:");
    }

    @Override
    public JdbcRowConverter getRowConverter(RowType rowType) {
        return new SQLServerRowConverter(rowType);
    }

    @Override
    public Optional<String> defaultDriverName() {
        return Optional.of("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    @Override
    public String quoteIdentifier(String identifier) {
        return identifier;
    }

    /*IF EXISTS(SELECT * FROM source WHERE tid = 3)
    BEGIN
    UPDATE source SET tname = 'd' WHERE tid = 3
    END
    ELSE
    BEGIN
    INSERT INTO source (tid, tname) VALUES(3, 'd')
    END*/
    @Override
    public Optional<String> getUpsertStatement(
            String tableName, String[] fieldNames, String[] uniqueKeyFields) {
        /*get update field*/
        ArrayList<String> updateFieldNamesList = new ArrayList<String>(fieldNames.length);
        Collections.addAll(updateFieldNamesList, fieldNames);
        ArrayList<String> uniqueKeyFieldsList = new ArrayList<String>(uniqueKeyFields.length);
        Collections.addAll(uniqueKeyFieldsList, uniqueKeyFields);
        updateFieldNamesList.removeAll(uniqueKeyFieldsList);

        String updateClause =
                Arrays.stream(updateFieldNamesList.toArray(new String[0]))
                        .map(f -> quoteIdentifier(f) + " = :" + quoteIdentifier(f))
                        .collect(Collectors.joining(", "));
        String onClause =
                Arrays.stream(uniqueKeyFields)
                        .map(f -> quoteIdentifier(f) + " = :" + quoteIdentifier(f))
                        .collect(Collectors.joining(" AND "));
        String sql =
                "IF EXISTS ( SELECT * FROM " + tableName + " WHERE " + onClause + " ) "
                + " BEGIN "
                + " UPDATE " + tableName + " SET " + updateClause + " WHERE " + onClause
                + " END "
                + " ELSE "
                + " BEGIN "
                +  getInsertStatement(tableName,fieldNames)
                + " END";
        return Optional.of(sql);
    }

    private String getInsertStatement(String tableName, String[] fieldNames) {
        String columns =
                Arrays.stream(fieldNames)
                        .map(this::quoteIdentifier)
                        .collect(Collectors.joining(", "));
        String placeholders =
                Arrays.stream(fieldNames).map(f -> ":" + f).collect(Collectors.joining(", "));
        return "INSERT INTO " + tableName + "(" + columns + ") VALUES (" + placeholders + ")";
    }

    @Override
    public String dialectName() {
        return "SQLServer";
    }

    @Override
    public int maxDecimalPrecision() {
        return MAX_DECIMAL_PRECISION;
    }

    @Override
    public int minDecimalPrecision() {
        return MIN_DECIMAL_PRECISION;
    }

    @Override
    public int maxTimestampPrecision() {
        return MAX_TIMESTAMP_PRECISION;
    }

    @Override
    public int minTimestampPrecision() {
        return MIN_TIMESTAMP_PRECISION;
    }

    @Override
    public List<LogicalTypeRoot> unsupportedTypes() {

        return Arrays.asList(
                LogicalTypeRoot.BINARY,
                LogicalTypeRoot.TIMESTAMP_WITH_LOCAL_TIME_ZONE,
                LogicalTypeRoot.TIMESTAMP_WITH_TIME_ZONE,
                LogicalTypeRoot.INTERVAL_YEAR_MONTH,
                LogicalTypeRoot.INTERVAL_DAY_TIME,
                LogicalTypeRoot.ARRAY,
                LogicalTypeRoot.MULTISET,
                LogicalTypeRoot.MAP,
                LogicalTypeRoot.ROW,
                LogicalTypeRoot.DISTINCT_TYPE,
                LogicalTypeRoot.STRUCTURED_TYPE,
                LogicalTypeRoot.NULL,
                LogicalTypeRoot.RAW,
                LogicalTypeRoot.SYMBOL,
                LogicalTypeRoot.UNRESOLVED);
    }
}
