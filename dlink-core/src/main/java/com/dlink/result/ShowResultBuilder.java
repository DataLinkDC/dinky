package com.dlink.result;

import com.dlink.constant.FlinkSQLConstant;
import org.apache.flink.table.api.TableColumn;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

/**
 * ShowResultBuilder
 *
 * @author wenmo
 * @since 2021/7/1 23:57
 */
public class ShowResultBuilder implements ResultBuilder {
    public static final String OPERATION_TYPE = FlinkSQLConstant.SHOW;

    private boolean printRowKind;
    private String nullColumn;

    public ShowResultBuilder(String nullColumn, boolean printRowKind) {
        this.printRowKind = printRowKind;
        this.nullColumn = nullColumn;
    }

    @Override
    public IResult getResult(TableResult tableResult) {
        List<TableColumn> columns = tableResult.getTableSchema().getTableColumns();
        Set<String> column = new LinkedHashSet();
        String[] columnNames = columns.stream().map(TableColumn::getName).map(s -> s.replace(" ", "")).toArray((x$0) -> {
            return (new String[x$0]);
        });
        if (printRowKind) {
            columnNames = Stream.concat(Stream.of("op"), Arrays.stream(columnNames)).toArray((x$0) -> {
                return new String[x$0];
            });
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        Iterator<Row> it = tableResult.collect();
        while (it.hasNext()) {
            String[] cols = rowToString(it.next());
            Map<String, Object> row = new HashMap<>();
            for (int i = 0; i < cols.length; i++) {
                if (i > columnNames.length) {
                    column.add("UKN" + i);
                    row.put("UKN" + i, cols[i]);
                } else {
                    column.add(columnNames[i]);
                    row.put(columnNames[i], cols[i]);
                }
            }
            rows.add(row);
        }
        return new DDLResult(rows, rows.size(), column);
    }

    private String[] rowToString(Row row) {
        int len = printRowKind ? row.getArity() + 1 : row.getArity();
        List<String> fields = new ArrayList(len);
        if (printRowKind) {
            fields.add(row.getKind().shortString());
        }
        for (int i = 0; i < row.getArity(); ++i) {
            Object field = row.getField(i);
            if (field == null) {
                fields.add(nullColumn);
            } else {
                fields.add(StringUtils.arrayAwareToString(field));
            }
        }
        return fields.toArray(new String[0]);
    }
}
