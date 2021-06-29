package com.dlink.result;

import com.dlink.constant.FlinkSQLConstant;
import org.apache.flink.table.api.TableColumn;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

/**
 * SelectBuilder
 *
 * @author wenmo
 * @since 2021/5/25 16:03
 **/
public class SelectResultBuilder implements ResultBuilder {

    public static final String OPERATION_TYPE = FlinkSQLConstant.SELECT;

    private Integer maxRowNum;
    private boolean printRowKind;
    private String nullColumn;

    public SelectResultBuilder(Integer maxRowNum, String nullColumn, boolean printRowKind) {
        this.maxRowNum = maxRowNum;
        this.printRowKind = printRowKind;
        this.nullColumn = nullColumn;
    }

    @Override
    public IResult getResult(TableResult tableResult) {
        String jobId = null;
        if(tableResult.getJobClient().isPresent()) {
            jobId = tableResult.getJobClient().get().getJobID().toHexString();
        }
        List<TableColumn> columns = tableResult.getTableSchema().getTableColumns();
        int totalCount = 0;
        Set<String> column = new LinkedHashSet();
        String[] columnNames = columns.stream().map(TableColumn::getName).map(s -> s.replace(" ", "")).toArray((x$0) -> {
            return (new String[x$0]);
        });
        if (printRowKind) {
            columnNames = Stream.concat(Stream.of("op"), Arrays.stream(columnNames)).toArray((x$0) -> {
                return new String[x$0];
            });
        }
        long numRows = 0L;
        List<Map<String, Object>> rows = new ArrayList<>();
        Iterator<Row> it = tableResult.collect();
        while (it.hasNext()) {
            if (numRows < maxRowNum) {
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
            } else {
                break;
            }
            numRows++;
            totalCount++;
        }
        return new SelectResult(rows, totalCount, rows.size(), column, jobId, true);
    }

    public String[] rowToString(Row row) {
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
        return (String[]) fields.toArray(new String[0]);
    }

}
