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

import java.util.List;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;

/**
 * mybatisPlus config class
 */
@Configuration
@MapperScan("org.dinky.mapper")
@Slf4j
public class MybatisPlusConfig {

    private static final List<String> IGNORE_TABLE_NAMES = Lists.newArrayList(
            "dinky_namespace", "dinky_alert_group", "dinky_alert_history", "dinky_alert_instance", "dinky_catalogue",
            "dinky_cluster", "dinky_cluster_configuration", "dinky_database", "dinky_fragment", "dinky_history",
            "dinky_jar", "dinky_job_history", "dinky_job_instance", "dinky_role", "dinky_savepoints",
            "dinky_task", "dinky_task_statement", "dinky_task_version");

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        log.info("mybatis plus interceptor execute");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {

            @Override
            public Expression getTenantId() {
                Integer tenantId = (Integer) TenantContextHolder.get();
                if (tenantId == null) {
                    // log.warn("request context tenant id is null");
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
