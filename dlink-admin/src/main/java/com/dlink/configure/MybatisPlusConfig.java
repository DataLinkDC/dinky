package com.dlink.configure;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.NullValue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.dlink.context.RequestContext;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

/**
 * mybatisPlus config class
 */
@Configuration
@MapperScan("com.dlink.mapper")
@Slf4j
public class MybatisPlusConfig {
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
                return !"dlink_role".equalsIgnoreCase(tableName);
            }
        }));

        return interceptor;
    }

}
