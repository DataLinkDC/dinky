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

package com.dlink.configure;

import com.dlink.constant.BaseConstant;
import com.dlink.interceptor.LocaleChangeInterceptor;
import com.dlink.interceptor.TenantInterceptor;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;

/**
 * AppConfiguration
 *
 * @author wenmo
 * @since 2021/11/28 19:35
 */
@Configuration
public class AppConfiguration implements WebMvcConfigurer {

    /**
     * Cookie
     *
     * @return local resolver
     */
    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieName(BaseConstant.LOCALE_LANGUAGE_COOKIE);
        // set default locale
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        // set language tag compliant
        localeResolver.setLanguageTagCompliant(false);
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        return new LocaleChangeInterceptor();
    }

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        // 注册Sa-Token的路由拦截器
        registry.addInterceptor(new SaRouteInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login")
                .excludePathPatterns("/api/geTenants")
                .excludePathPatterns("/openapi/**");

        registry.addInterceptor(new TenantInterceptor())
                .addPathPatterns("/api/alertGroup/**")
                .addPathPatterns("/api/alertHistory/**")
                .addPathPatterns("/api/alertInstance/**")
                .addPathPatterns("/api/catalogue/**")
                .addPathPatterns("/api/clusterConfiguration/**")
                .addPathPatterns("/api/cluster/**")
                .addPathPatterns("/api/database/**")
                .addPathPatterns("/api/history/**")
                .addPathPatterns("/api/jobInstance/**")
                .addPathPatterns("/api/namespace/**")
                .addPathPatterns("/api/savepoints/**")
                .addPathPatterns("/api/statement/**")
                .addPathPatterns("/api/task/**")
                .addPathPatterns("/api/role/**")
                .addPathPatterns("/api/fragment/**")
                .addPathPatterns("/api/jar/*");
    }
}
