package com.dlink.result;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DDLResult
 *
 * @author wenmo
 * @since 2021/6/29 22:06
 */
@Setter
@Getter
public class DDLResult extends AbstractResult implements IResult {

    private List<Map<String,Object>> rowData;
    private Integer total;
    private Set<String> columns;

    public DDLResult(boolean success) {
        this.success = success;
        this.endTime = LocalDateTime.now();
    }

    public DDLResult(List<Map<String, Object>> rowData, Integer total, Set<String> columns) {
        this.rowData = rowData;
        this.total = total;
        this.columns = columns;
        this.success = true;
        this.endTime = LocalDateTime.now();
    }

    @Override
    public String getJobId() {
        return null;
    }
}
