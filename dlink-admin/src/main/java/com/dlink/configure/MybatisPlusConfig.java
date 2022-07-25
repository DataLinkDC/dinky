package com.dlink.configure;

import lombok.extern.slf4j.Slf4j;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.dlink.context.RequestContext;
import com.google.common.collect.Lists;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;

import java.util.List;

/**
 * mybatisPlus config class
 */
@Configuration
@MapperScan("com.dlink.mapper")
@Slf4j
public class MybatisPlusConfig {

    private static final List<String> IGNORE_TABLE_NAMES =
        Lists.newArrayList("dlink_catalogue",  "dlink_cluster", "dlink_cluster_configuration", "dlink_database",
                                     "dlink_history","dlink_savepoints","dlink_task","dlink_task_statement",
                                     "dlink_task_version","dlink_job_instance","dlink_alert_instance","dlink_alert_group",
                                     "dlink_alert_history","dlink_job_history","dlink_role","dlink_namespace",
                                     "dlink_alert_group","dlink_alert_history","dlink_alert_instance");

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.info("mybatis plus interceptor execute");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                Integer tenantId = (Integer) RequestContext.get();
                if (tenantId == null) {
                    log.warn("request context tenant id is null");
                    return new NullValue();
                }
                return new LongValue(tenantId);
            }

            @Override
            public boolean ignoreTable(String tableName) {
                return !IGNORE_TABLE_NAMES.contains(tableName);
            }
        }));

        return interceptor;
    }

}