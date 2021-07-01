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
        if (tableResult.getJobClient().isPresent()) {
            String jobId = tableResult.getJobClient().get().getJobID().toHexString();
            ResultRunnable runnable = new ResultRunnable(tableResult, maxRowNum, printRowKind, nullColumn);
            Thread thread = new Thread(runnable, jobId);
            thread.start();
            return SelectResult.buildSuccess(jobId);
        }else{
            return SelectResult.buildFailed();
        }
        /*String jobId = null;
        if (tableResult.getJobClient().isPresent()) {
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
        return new SelectResult(rows, totalCount, rows.size(), column, jobId, true);*/
    }

}
