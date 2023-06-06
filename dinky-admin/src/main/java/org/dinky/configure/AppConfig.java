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

import org.dinky.data.constant.BaseConstant;
import org.dinky.interceptor.LocaleChangeInterceptor;
import org.dinky.interceptor.TenantInterceptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.EndpointLinksResolver;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.EndpointMediaTypes;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import cn.dev33.satoken.interceptor.SaInterceptor;

/**
 * AppConfiguration
 *
 * @since 2021/11/28 19:35
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {
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

    /**
     * 注册拦截器
     *
     * @param registry 注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        // 注册Sa-Token的路由拦截器

        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login")
                .excludePathPatterns("/druid/**")
                .excludePathPatterns("/openapi/**");

        registry.addInterceptor(new TenantInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login")
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
                .addPathPatterns("/api/git/**")
                .addPathPatterns("/api/jar/*");
    }

    /** 增加如下配置可解决Spring Boot 6.x 与Swagger 3.0.0 不兼容问题 */
    @Bean
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
            WebEndpointsSupplier webEndpointsSupplier,
            ServletEndpointsSupplier servletEndpointsSupplier,
            ControllerEndpointsSupplier controllerEndpointsSupplier,
            EndpointMediaTypes endpointMediaTypes,
            CorsEndpointProperties corsProperties,
            WebEndpointProperties webEndpointProperties,
            Environment environment) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping =
                this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
        return new WebMvcEndpointHandlerMapping(
                endpointMapping,
                webEndpoints,
                endpointMediaTypes,
                corsProperties.toCorsConfiguration(),
                new EndpointLinksResolver(allEndpoints, basePath),
                shouldRegisterLinksMapping,
                null);
    }

    private boolean shouldRegisterLinksMapping(
            WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
        return webEndpointProperties.getDiscovery().isEnabled()
                && (StringUtils.hasText(basePath)
                        || ManagementPortType.get(environment)
                                .equals(ManagementPortType.DIFFERENT));
    }
}
