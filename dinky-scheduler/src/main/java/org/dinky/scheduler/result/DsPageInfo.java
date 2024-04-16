package org.dinky.scheduler.result;

import lombok.Data;

import java.util.List;

@Data
public class DsPageInfo<T> {
    /** total */
    private DsPageData<T> data;
    private Integer total = 0;
    /** total Page */
    private Integer totalPage;
    /** page size */
    private Integer pageSize = 20;
    /** current page */
    private Integer currentPage = 0;
    /** pageNo */
    private Integer pageNo;

}