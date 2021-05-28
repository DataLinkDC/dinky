package com.dlink.constant;

public interface FlinkHistoryConstant {
    /**
     * history端口
     */
    String PORT = "8082";

    /**
     * 逗号,
     */
    String COMMA = ",";
    /**
     * 任务复数 jobs
     */
    String JOBS = "jobs";
    /**
     * 任务单数 job
     */
    String JOB = "job";
    /**
     * 总览 overview
     */
    String OVERVIEW = "overview";
    /**
     * 错误 error
     */
    String ERROR = "error";
    /**
     * 起始时间 start-time
     */
    String START_TIME = "start-time";
    /**
     * 任务名称 name
     */
    String NAME = "name";
    /**
     * 任务状态 state
     */
    String STATE = "state";
    /**
     * 异常 获取任务数据失败
     */
    String EXCEPTION_DATA_NOT_FOUND = "获取任务数据失败";
    /**
     * 30天时间戳的大小
     */
    Long THIRTY_DAY = 30L * 24 * 60 * 60 * 1000;
    /**
     * 一天时间戳
     */
    Integer ONE_DAY = 24 * 60 * 60 * 1000;
    /**
     * 运行active
     */
    String ACTIVE = "active";
    /**
     * 查询记录的条数
     */
    String COUNT = "count";
    /**
     * 当前页码 page
     */
    String PAGE = "page";
    /**
     * 每一页的大小 SIZE
     */
    String SIZE = "size";
    /**
     * 当前页的条数 pageCount
     */
    String PAGE_COUNT = "pageCount";
    /**
     * 返回数据集 resList
     */
    String RES_LIST = "resList";


}
