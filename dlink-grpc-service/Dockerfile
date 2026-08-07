# Dinky gRPC 微服务 Dockerfile

FROM openjdk:11-jre-slim

LABEL maintainer="Dinky Team"
LABEL description="Dinky Flink Stream Processing gRPC Service for Low-Code Platforms"

# 设置工作目录
WORKDIR /app

# 创建日志目录
RUN mkdir -p /var/log/dinky-grpc && \
    mkdir -p /tmp

# 复制构建产物
COPY target/dlink-grpc-service-*.jar app.jar

# 复制配置文件 (可选，用于覆盖默认配置)
# COPY src/main/resources/application.yml application.yml

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
ENV GRPC_SERVER_PORT=9090
ENV SERVER_PORT=8080

# 暴露端口
EXPOSE 8080 9090

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
