package com.dlink.grpc.config;

import net.devh.boot.grpc.server.security.interceptors.DefaultAccessInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * gRPC 服务配置类
 * 
 * @author Dinky Team
 * @date 2024
 */
@Configuration
public class GrpcServerConfig {

    /**
     * 配置全局 gRPC 拦截器
     * 用于权限校验、日志记录、链路追踪等
     */
    @Bean
    public DefaultAccessInterceptor defaultAccessInterceptor() {
        return new DefaultAccessInterceptor();
    }
}
