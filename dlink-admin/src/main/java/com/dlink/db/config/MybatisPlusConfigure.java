package com.dlink.db.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.dlink.db.handler.DateMetaObjectHandler;
import com.dlink.db.properties.MybatisPlusFillProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * MybatisPlusConfigure
 *
 * @author wenmo
 * @since 2021/5/25
 **/
@EnableConfigurationProperties(MybatisPlusFillProperties.class)
public class MybatisPlusConfigure {

    @Autowired
    private MybatisPlusFillProperties autoFillProperties;

    @Bean
    public PaginationInnerInterceptor paginationInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        return paginationInterceptor;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "dlink.mybatis-plus.fill", name = "enabled", havingValue = "true", matchIfMissing = true)
    public MetaObjectHandler metaObjectHandler() {
        return new DateMetaObjectHandler(autoFillProperties);
    }
}
