package org.dinky.data.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.Date;

/**
 * @author liurulin
 * @version 6.1.0
 * @description 描述
 * @date 2024/1/3 18:07
 */
@Data
public class SchedulerParam {

    /**
     *  调度类型
     */
    private String schedulingType;

    /**
     *  生效开始日期
     */
    private Date startEffectiveDate;

    /**
     *  生效结束日期
     */
    private Date endEffectiveDate;

    /**
     * 调度周期
     * 1 天 2 周 3 月
     */
    private String schedulePeriod;

    /**
     * 周-天
     */
    private String[] schedulingDays;

    /**
     * 月-天
     */
    private String[] schedulingWeeks;

    /**
     * 时间
     */
    private Boolean schedulingTime;

    /**
     * 指定执行时间
     */
    private String dispatchDate;
}

