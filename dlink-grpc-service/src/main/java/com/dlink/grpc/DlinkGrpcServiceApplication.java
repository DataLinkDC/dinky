package com.dlink.grpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Dinky gRPC 微服务启动类
 * 
 * 该服务将 Dinky 核心能力封装为 gRPC 接口，供低代码平台调用
 * 
 * @author Dinky Team
 * @date 2024
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.dlink.grpc", "com.dlink"})
public class DlinkGrpcServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DlinkGrpcServiceApplication.class, args);
        System.out.println("========================================");
        System.out.println("Dinky gRPC Service Started Successfully!");
        System.out.println("gRPC Server is listening on port: 9090");
        System.out.println("========================================");
    }
}
