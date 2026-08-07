package com.dlink.grpc.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * gRPC 认证拦截器
 * 负责从请求头中提取 Token 并进行权限校验
 * 
 * @author Dinky Team
 * @date 2024
 */
@Slf4j
@Component
public class AuthServerInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> AUTH_TOKEN_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
    
    private static final Metadata.Key<String> TENANT_CODE_KEY =
            Metadata.Key.of("Tenant-Code", Metadata.ASCII_STRING_MARSHALLER);
    
    private static final Metadata.Key<String> USER_ID_KEY =
            Metadata.Key.of("User-Id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        
        // 提取认证信息
        String authToken = headers.get(AUTH_TOKEN_KEY);
        String tenantCode = headers.get(TENANT_CODE_KEY);
        String userId = headers.get(USER_ID_KEY);
        
        // 验证 Token（这里需要集成 Dinky 原有的权限体系）
        if (!validateToken(authToken)) {
            call.close(Status.UNAUTHENTICATED.withDescription("Invalid or missing authentication token"), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }
        
        // 验证租户信息
        if (tenantCode == null || tenantCode.isEmpty()) {
            call.close(Status.INVALID_ARGUMENT.withDescription("Missing tenant code"), new Metadata());
            return new ServerCall.Listener<ReqT>() {};
        }
        
        // 记录日志
        log.info("gRPC request authenticated - Tenant: {}, User: {}", tenantCode, userId);
        
        // 将用户信息添加到上下文（供后续服务使用）
        // 可以使用 Context.current().withValue() 来传递
        
        return next.startCall(call, headers);
    }
    
    /**
     * 验证 Token
     * TODO: 集成 Dinky 原有的 SpringSecurity 权限体系
     * 建议从 Redis 中获取 Token 信息进行校验
     */
    private boolean validateToken(String authToken) {
        if (authToken == null || authToken.isEmpty()) {
            return false;
        }
        
        // 这里需要实现具体的 Token 验证逻辑
        // 可以从 Redis 中查询 Token 是否有效
        // 或者调用 Dinky admin 的权限服务进行验证
        
        // 临时实现：简单验证格式
        return authToken.startsWith("Bearer ");
    }
}
