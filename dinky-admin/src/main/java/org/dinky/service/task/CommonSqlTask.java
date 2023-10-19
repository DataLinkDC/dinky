package org.dinky.service.task;

import cn.hutool.cache.Cache;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.dinky.config.Dialect;
import org.dinky.data.annotation.SupportDialect;
import org.dinky.data.dto.SqlDTO;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.job.JobResult;
import org.dinky.service.DataBaseService;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@SupportDialect({Dialect.SQL, Dialect.MYSQL, Dialect.ORACLE, Dialect.POSTGRESQL, Dialect.HIVE, Dialect.SQLSERVER, Dialect.CLICKHOUSE, Dialect.DORIS, Dialect.PHOENIX, Dialect.STAR_ROCKS, Dialect.PRESTO})
public class CommonSqlTask extends BaseTask {
    private static final Cache<Integer, JobResult> COMMON_SQL_SEARCH_CACHE =
            new TimedCache<>(TimeUnit.MINUTES.toMillis(10));

    public CommonSqlTask(TaskDTO task) {
        super(task);
    }

    @Override
    public List<SqlExplainResult> explain()  {
        DataBaseService dataBaseService = SpringUtil.getBean(DataBaseService.class);
       return dataBaseService.explainCommonSql(task);
    }

    @Override
    public JobResult execute() {
        log.info("Preparing to execute common sql...");
        SqlDTO sqlDTO = SqlDTO.build(task.getStatement(), task.getDatabaseId(), null);
        DataBaseService dataBaseService = SpringUtil.getBean(DataBaseService.class);
        return COMMON_SQL_SEARCH_CACHE.get(task.getId(), () -> dataBaseService.executeCommonSql(sqlDTO));
    }

    @Override
    public boolean stop() {
        return false;
    }
}
