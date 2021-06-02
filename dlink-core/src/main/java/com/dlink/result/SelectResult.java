package com.dlink.result;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SelectResult
 *
 * @author wenmo
 * @since 2021/5/25 16:01
 **/
public class SelectResult implements IResult{

    private List<Map<String,Object>> rowData;
    private Integer total;
    private Integer currentCount;
    private Set<String> columns;

    public SelectResult(List<Map<String, Object>> rowData, Integer total, Integer currentCount, Set<String> columns) {
        this.rowData = rowData;
        this.total = total;
        this.currentCount = currentCount;
        this.columns = columns;
    }

    public List<Map<String, Object>> getRowData() {
        return rowData;
    }

    public void setRowData(List<Map<String, Object>> rowData) {
        this.rowData = rowData;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(Integer currentCount) {
        this.currentCount = currentCount;
    }

    public Set<String> getColumns() {
        return columns;
    }

    public void setColumns(Set<String> columns) {
        this.columns = columns;
    }
}
