package com.dlink.configure;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dlink.interceptor.TenantInterceptor;

/**
 * SaTokenConfigure
 *
 * @author wenmo
 * @since 2021/11/28 19:35
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册Sa-Token的路由拦截器
        registry.addInterceptor(new SaRouteInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/login")
                .excludePathPatterns("/openapi/**");

        registry.addInterceptor(new TenantInterceptor())
                .addPathPatterns("/api/alertGroup/**")
                .addPathPatterns("/api/alertHistory/**")
                .addPathPatterns("/api/alertInsta/**")
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
                .addPathPatterns("/api/role/**");
    }
}
