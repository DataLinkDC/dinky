package com.dlink.result;

import com.dlink.constant.FlinkConstant;
import com.dlink.utils.FlinkUtil;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;

import java.util.*;

/**
 * ResultRunnable
 *
 * @author wenmo
 * @since 2021/7/1 22:50
 */
public class ResultRunnable implements Runnable {

    private TableResult tableResult;
    private Integer maxRowNum;
    private boolean isChangeLog;
    private boolean isAutoCancel;
    private String nullColumn = "";

    public ResultRunnable(TableResult tableResult, Integer maxRowNum, boolean isChangeLog, boolean isAutoCancel) {
        this.tableResult = tableResult;
        this.maxRowNum = maxRowNum;
        this.isChangeLog = isChangeLog;
        this.isAutoCancel = isAutoCancel;
    }

    @Override
    public void run() {
        if (tableResult.getJobClient().isPresent()) {
            String jobId = tableResult.getJobClient().get().getJobID().toHexString();
            if (!ResultPool.containsKey(jobId)) {
                ResultPool.put(new SelectResult(jobId, new ArrayList<>(), new LinkedHashSet<>()));
            }
            try {
                if (isChangeLog) {
                    catchChangLog(ResultPool.get(jobId));
                } else {
                    catchData(ResultPool.get(jobId));
                }
            } catch (Exception e) {

            }
        }
    }

    private void catchChangLog(SelectResult selectResult) {
        List<String> columns = FlinkUtil.catchColumn(tableResult);
        columns.add(0, FlinkConstant.OP);
        Set<String> column = new LinkedHashSet(columns);
        selectResult.setColumns(column);
        List<Map<String, Object>> rows = selectResult.getRowData();
        Iterator<Row> it = tableResult.collect();
        while (it.hasNext()) {
            if (rows.size() >= maxRowNum) {
                if (isAutoCancel && tableResult.getJobClient().isPresent()) {
                    tableResult.getJobClient().get().cancel();
                }
                break;
            }
            Map<String, Object> map = new LinkedHashMap<>();
            Row row = it.next();
            map.put(columns.get(0), row.getKind().shortString());
            for (int i = 0; i < row.getArity(); ++i) {
                Object field = row.getField(i);
                if (field == null) {
                    map.put(columns.get(i + 1), nullColumn);
                } else {
                    map.put(columns.get(i + 1), field);
                }
            }
            rows.add(map);
        }
    }

    private void catchData(SelectResult selectResult) {
        List<String> columns = FlinkUtil.catchColumn(tableResult);
        Set<String> column = new LinkedHashSet(columns);
        selectResult.setColumns(column);
        List<Map<String, Object>> rows = selectResult.getRowData();
        Iterator<Row> it = tableResult.collect();
        while (it.hasNext()) {
            if (rows.size() >= maxRowNum) {
                break;
            }
            Map<String, Object> map = new LinkedHashMap<>();
            Row row = it.next();
            for (int i = 0; i < row.getArity(); ++i) {
                Object field = row.getField(i);
                if (field == null) {
                    map.put(columns.get(i), nullColumn);
                } else {
                    map.put(columns.get(i), field);
                }
            }
            if (RowKind.UPDATE_BEFORE == row.getKind() || RowKind.DELETE == row.getKind()) {
                rows.remove(map);
            } else {
                rows.add(map);
            }
        }
    }
}
