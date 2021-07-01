package com.dlink.result;

import org.apache.flink.table.api.TableColumn;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.util.StringUtils;

import java.util.*;
import java.util.stream.Stream;

/**
 * ResultRunnable
 *
 * @author wenmo
 * @since 2021/7/1 22:50
 */
public class ResultRunnable implements Runnable {

    private TableResult tableResult;
    private Integer maxRowNum;
    private boolean printRowKind;
    private String nullColumn;

    public ResultRunnable(TableResult tableResult, Integer maxRowNum, boolean printRowKind, String nullColumn) {
        this.tableResult = tableResult;
        this.maxRowNum = maxRowNum;
        this.printRowKind = printRowKind;
        this.nullColumn = nullColumn;
    }

    @Override
    public void run() {
        if(tableResult.getJobClient().isPresent()) {
            String jobId = tableResult.getJobClient().get().getJobID().toHexString();
            if (!ResultPool.containsKey(jobId)) {
                ResultPool.put(new SelectResult(jobId, new ArrayList<Map<String, Object>>(), new LinkedHashSet<String>()));
            }
            try {
                catchData(ResultPool.get(jobId));
            }catch (Exception e){

            }
        }
    }

    private void catchData(SelectResult selectResult){
        List<TableColumn> columns = tableResult.getTableSchema().getTableColumns();
        String[] columnNames = columns.stream().map(TableColumn::getName).map(s -> s.replace(" ", "")).toArray((x$0) -> {
            return (new String[x$0]);
        });
        if (printRowKind) {
            columnNames = Stream.concat(Stream.of("op"), Arrays.stream(columnNames)).toArray((x$0) -> {
                return new String[x$0];
            });
        }
        Set<String> column = new LinkedHashSet(Arrays.asList(columnNames));
        selectResult.setColumns(column);
        long numRows = 0L;
        List<Map<String, Object>> rows = selectResult.getRowData();
        Iterator<Row> it = tableResult.collect();
        while (it.hasNext()) {
            if (numRows < maxRowNum) {
                String[] cols = rowToString(it.next());
                Map<String, Object> row = new HashMap<>();
                for (int i = 0; i < cols.length; i++) {
                    if (i > columnNames.length) {
                        /*column.add("UKN" + i);
                        row.put("UKN" + i, cols[i]);*/
                    } else {
//                        column.add(columnNames[i]);
                        row.put(columnNames[i], cols[i]);
                    }
                }
                rows.add(row);
                numRows++;
            } else {
                break;
            }
        }
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
