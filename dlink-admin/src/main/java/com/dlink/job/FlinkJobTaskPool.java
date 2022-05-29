package com.dlink.job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dlink.model.JobInfoDetail;
import com.dlink.pool.AbstractPool;

/**
 * FlinkJobTaskPool
 *
 * @author wenmo
 * @since 2022/5/28 16:39
 */
public class FlinkJobTaskPool extends AbstractPool<JobInfoDetail> {

    private static volatile Map<String, JobInfoDetail> flinkJobTaskEntityMap = new ConcurrentHashMap<>();

    private static FlinkJobTaskPool instance = new FlinkJobTaskPool();

    public static FlinkJobTaskPool getInstance(){
        return instance;
    }

    @Override
    public Map<String, JobInfoDetail> getMap() {
        return flinkJobTaskEntityMap;
    }

    public void refresh(JobInfoDetail entity) {
        entity.refresh();
    }
}
