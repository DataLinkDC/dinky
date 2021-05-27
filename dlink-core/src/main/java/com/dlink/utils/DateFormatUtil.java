package com.dlink.utils;

import com.dlink.constant.FlinkHistoryConstant;

import java.util.Date;
import java.util.TimeZone;

public class DateFormatUtil {
    /**
     * 获取一个日期的0:00:00 时间戳    日期必须大于00:00:00否则返回上一天
     *
     * @param date
     * @return
     */
    public static long getZeroTimeStamp(Date date) {
        return getZeroTimeStamp(date.getTime());
    }

    public static long getZeroTimeStamp(Long timestamp) {
        timestamp += TimeZone.getDefault().getRawOffset();
        return timestamp / FlinkHistoryConstant.ONE_DAY * FlinkHistoryConstant.ONE_DAY - TimeZone.getDefault().getRawOffset();
    }

    /**
     * 获取指定时间 当天的最后一秒 23:59:59    日期必须大于00:00:00 否则返回上一天
     * @param date
     * @return
     */
    public static long getLastTimeStampOfOneday(Date date) {
        return getLastTimeStampOfOneday(date.getTime());
    }

    public static long getLastTimeStampOfOneday(Long timestamp) {
        timestamp += TimeZone.getDefault().getRawOffset();
        return ( timestamp / FlinkHistoryConstant.ONE_DAY * FlinkHistoryConstant.ONE_DAY  + FlinkHistoryConstant.ONE_DAY - 100)- TimeZone.getDefault().getRawOffset();
    }
}
