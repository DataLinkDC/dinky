package com.dlink.metadata.result;

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
public class SelectResult {
    private List<String> columns;
    private List<HashMap<String,Object>> datas;
    private Integer total;
    private Integer page;
    private Integer limit;
}
