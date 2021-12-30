package com.dlink.metadata.result;

import com.dlink.result.AbstractResult;
import com.dlink.result.IResult;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * SelectResult
 *
 * @author wenmo
 * @since 2021/7/19 23:31
 */
public class JdbcSelectResult extends AbstractResult implements IResult {
    private List<String> columns;
    private List<LinkedHashMap<String,Object>> rowData;
    private Integer total;
    private Integer page;
    private Integer limit;

    private static final String STATUS = "status";
    private static final List<String> STATUS_COLUMN = new ArrayList<String>(){{ add("status"); }};

    public JdbcSelectResult() {
    }

    public static JdbcSelectResult buildResult(){
        JdbcSelectResult result = new JdbcSelectResult();
        result.setStartTime(LocalDateTime.now());
        return result;
    }

    public void setStatusList(List<Object> statusList){
        this.setColumns(STATUS_COLUMN);
        List<LinkedHashMap<String,Object>> dataList = new ArrayList<>();
        for(Object item: statusList){
            LinkedHashMap map = new LinkedHashMap<String,Object>();
            map.put(STATUS,item);
            dataList.add(map);
        }
        this.setRowData(dataList);
        this.setTotal(statusList.size());
    }

    @Override
    public String getJobId() {
        return null;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<LinkedHashMap<String, Object>> getRowData() {
        return rowData;
    }

    public void setRowData(List<LinkedHashMap<String, Object>> rowData) {
        this.rowData = rowData;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
