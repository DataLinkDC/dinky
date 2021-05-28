package com.dlink.common.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Ant Design Pro ProTable Query Result
 *
 * @author wenmo
 * @since 2021/5/18 21:54
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProTableResult<T> implements Serializable {
    private static final long serialVersionUID = -6377431009117000655L;
    /**
     * 总数
     */
    private Long total;
    /**
     * 是否成功：true 成功、false 失败
     */
    private boolean success;
    /**
     * 当前页码
     */
    private Integer current;
    /**
     * 当前每页记录数
     */
    private Integer pageSize;
    /**
     * 当前页结果集
     */
    private List<T> data;
}
