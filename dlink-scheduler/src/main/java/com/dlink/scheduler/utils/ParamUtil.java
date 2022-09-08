package com.dlink.scheduler.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 郑文豪
 */
public class ParamUtil {

    /**
     * 封装分页查询
     *
     * @return {@link Map}
     * @author 郑文豪
     * @date 2022/9/7 16:57
     */
    public static Map<String, Object> getPageParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("pageNo", 1);
        params.put("pageSize", Integer.MAX_VALUE);
        return params;
    }

    /**
     * 封装分页查询
     *
     * @param name 查询条件
     * @return {@link Map}
     * @author 郑文豪
     * @date 2022/9/7 16:58
     */
    public static Map<String, Object> getPageParams(String name) {
        Map<String, Object> params = getPageParams();
        params.put("searchVal", name);
        return params;
    }
}
