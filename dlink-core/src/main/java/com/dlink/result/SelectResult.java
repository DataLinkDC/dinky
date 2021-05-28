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
}
