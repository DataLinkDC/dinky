package com.dlink.scheduler.result;

import java.util.List;

import lombok.Data;

/**
 * page info
 *
 * @param <T> model
 */
@Data
public class PageInfo<T> {

    /**
     * totalList
     */
    private List<T> totalList;
    /**
     * total
     */
    private Integer total = 0;
    /**
     * total Page
     */
    private Integer totalPage;
    /**
     * page size
     */
    private Integer pageSize = 20;
    /**
     * current page
     */
    private Integer currentPage = 0;
    /**
     * pageNo
     */
    private Integer pageNo;

}
