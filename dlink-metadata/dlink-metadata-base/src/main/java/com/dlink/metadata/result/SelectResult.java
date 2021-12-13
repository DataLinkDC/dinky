package com.dlink.metadata.result;

import com.dlink.result.AbstractResult;
import com.dlink.result.IResult;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

/**
 * SelectResult
 *
 * @author wenmo
 * @since 2021/7/19 23:31
 */
@Setter
@Getter
public class SelectResult extends AbstractResult implements IResult {
    private List<String> columns;
    private List<HashMap<String,Object>> rowData;
    private Integer total;
    private Integer page;
    private Integer limit;

    @Override
    public String getJobId() {
        return null;
    }
}
