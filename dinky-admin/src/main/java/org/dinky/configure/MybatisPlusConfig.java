/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.configure;

import org.dinky.context.TenantContextHolder;
import org.dinky.data.annotation.ConditionalOnListProperty;
import org.dinky.interceptor.PostgreSQLPrepareInterceptor;
import org.dinky.interceptor.PostgreSQLQueryInterceptor;
import org.dinky.mybatis.handler.DateMetaObjectHandler;
import org.dinky.mybatis.properties.MybatisPlusFillProperties;

import java.util.List;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.google.common.collect.Lists;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;

/** mybatisPlus config class */
@Configuration
@MapperScan({"org.dinky.mapper", "com.zdpx.mapper"})
@EnableConfigurationProperties(MybatisPlusFillProperties.class)
@Slf4j
@RequiredArgsConstructor
public class MybatisPlusConfig {

    private final MybatisPlusFillProperties autoFillProperties;

    private static final List<String> IGNORE_TABLE_NAMES =
            Lists.newArrayList(
                    "dinky_namespace",
                    "dinky_alert_group",
                    "dinky_alert_history",
                    "dinky_alert_instance",
                    "dinky_catalogue",
                    "dinky_cluster",
                    "dinky_cluster_configuration",
                    "dinky_database",
                    "dinky_fragment",
                    "dinky_history",
                    "dinky_jar",
                    "dinky_job_history",
                    "dinky_job_instance",
                    "dinky_role",
                    "dinky_savepoints",
                    "dinky_task",
                    "dinky_task_statement",
                    "dinky_git_project",
                    "dinky_task_version");

    @Bean
    //    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "pgsql , jmx")
    @ConditionalOnListProperty(name = "spring.profiles.active", havingValue = "pgsql")
    public PostgreSQLQueryInterceptor postgreSQLQueryInterceptor() {
        return new PostgreSQLQueryInterceptor();
    }

    /**
     * Add the plugin to the MyBatis plugin interceptor chain.
     *
     * @return {@linkplain PostgreSQLPrepareInterceptor}
     */
    @Bean
    //    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "pgsql , jmx")
    @ConditionalOnListProperty(name = "spring.profiles.active", havingValue = "pgsql")
    public PostgreSQLPrepareInterceptor postgreSQLPrepareInterceptor() {
        return new PostgreSQLPrepareInterceptor();
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.info("mybatis plus interceptor execute");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(
                new TenantLineInnerInterceptor(
                        new TenantLineHandler() {

                            @Override
                            public Expression getTenantId() {
                                Integer tenantId = (Integer) TenantContextHolder.get();
                                if (tenantId == null) {
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

    @Bean
    public PaginationInnerInterceptor paginationInterceptor() {
        return new PaginationInnerInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
            prefix = "dinky.mybatis-plus.fill",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true)
    public MetaObjectHandler metaObjectHandler() {
        return new DateMetaObjectHandler(autoFillProperties);
    }
}
