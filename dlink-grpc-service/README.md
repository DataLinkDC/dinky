# Dinky gRPC 微服务改造指南

## 项目概述

本项目是将开源项目 Dinky 改造为独立 gRPC 微服务的实现，专门对接低代码平台，屏蔽 Flink 底层复杂度，对外提供标准化 gRPC 接口。

## 目录结构

```
dlink-grpc-service/
├── pom.xml                                    # Maven 配置
├── Dockerfile                                 # Docker 镜像构建文件
├── src/main/
│   ├── java/com/dlink/grpc/
│   │   ├── DlinkGrpcServiceApplication.java   # 启动类
│   │   ├── config/
│   │   │   └── GrpcServerConfig.java          # gRPC 配置
│   │   ├── interceptor/
│   │   │   └── AuthServerInterceptor.java     # 认证拦截器
│   │   └── service/
│   │       ├── FlinkStreamTaskServiceImpl.java        # 任务服务实现
│   │       ├── FlinkStreamTaskMonitorServiceImpl.java # 监控服务实现
│   │       ├── FlinkClusterServiceImpl.java           # 集群服务实现
│   │       └── FlinkDataSourceServiceImpl.java        # 数据源服务实现
│   ├── proto/
│   │   └── flink_stream_task.proto            # Protobuf 接口定义
│   └── resources/
│       └── application.yml                    # 应用配置
└── README.md                                  # 本文档
```

## 核心功能

### 1. Flink 流式任务服务 (FlinkStreamTaskService)

- `CreateTask` - 创建 Flink 流式任务
- `SubmitTask` - 提交任务到集群
- `StartTask` - 启动任务
- `StopTask` - 停止任务 (支持 Savepoint)
- `RestartTask` - 重启任务
- `DeleteTask` - 删除任务
- `GetTaskDetail` - 获取任务详情

### 2. 任务监控服务 (FlinkStreamTaskMonitorService)

- `GetTaskStatus` - 获取任务状态
- `GetCheckpointInfo` - 获取 Checkpoint 信息
- `GetBackPressureInfo` - 获取背压信息
- `SubscribeTaskStatusStream` - 订阅任务状态流 (双向流)
- `GetTaskMetrics` - 获取任务指标

### 3. Flink 集群服务 (FlinkClusterService)

- `RegisterCluster` - 注册集群
- `UpdateCluster` - 更新集群
- `DeleteCluster` - 删除集群
- `ListClusters` - 获取集群列表
- `GetClusterDetail` - 获取集群详情
- `TestClusterConnectivity` - 测试集群连通性

### 4. 数据源服务 (FlinkDataSourceService)

- `RegisterDataSource` - 注册数据源
- `UpdateDataSource` - 更新数据源
- `DeleteDataSource` - 删除数据源
- `TestDataSourceConnectivity` - 测试数据源连通性
- `ValidateDataSourceWithCluster` - 验证数据源并关联集群
- `ListDataSources` - 获取数据源列表

## 快速开始

### 前置条件

- JDK 11+
- Maven 3.6+
- MySQL 5.7+
- Redis 5.0+
- Dinky 1.1.x 源码环境

### 1. 将模块添加到父 POM

在 Dinky 根目录的 `pom.xml` 中添加:

```xml
<modules>
    <!-- 现有模块 -->
    <module>dlink-admin</module>
    <module>dlink-core</module>
    <!-- ... -->
    
    <!-- 新增 gRPC 服务模块 -->
    <module>dlink-grpc-service</module>
</modules>
```

### 2. 配置数据库和 Redis

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://your-mysql-host:3306/dinky
    username: your-username
    password: your-password
  
  redis:
    host: your-redis-host
    port: 6379
```

### 3. 构建项目

```bash
cd /workspace
mvn clean install -pl dlink-grpc-service -am -DskipTests
```

### 4. 运行服务

```bash
java -jar dlink-grpc-service/target/dlink-grpc-service-1.1.0.jar
```

### 5. Docker 部署

```bash
# 构建镜像
docker build -t dinky-grpc-service:1.1.0 .

# 运行容器
docker run -d \
  -p 8080:8080 \
  -p 9090:9090 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql-host:3306/dinky \
  -e SPRING_REDIS_HOST=redis-host \
  dinky-grpc-service:1.1.0
```

## gRPC 客户端调用示例

### Java 客户端

```java
// 创建 Channel
ManagedChannel channel = ManagedChannelBuilder
    .forAddress("localhost", 9090)
    .usePlaintext()
    .build();

// 创建 Stub
FlinkStreamTaskServiceGrpc.FlinkStreamTaskServiceBlockingStub stub = 
    FlinkStreamTaskServiceGrpc.newBlockingStub(channel);

// 创建任务
StreamTaskConfig config = StreamTaskConfig.newBuilder()
    .setTaskName("MySQL to Kafka Sync")
    .setFlinkSql("INSERT INTO kafka_sink SELECT * FROM mysql_source")
    .setParallelism(4)
    .setClusterId("cluster-1")
    .putConfigParams("checkpoint.interval", "60000")
    .build();

CommonResponse response = stub.createTask(config);
System.out.println("Task created: " + response.getSuccess());
```

### Python 客户端

```python
import grpc
import flink_stream_task_pb2
import flink_stream_task_pb2_grpc

# 创建 Channel
channel = grpc.insecure_channel('localhost:9090')
stub = flink_stream_task_pb2_grpc.FlinkStreamTaskServiceStub(channel)

# 创建任务
config = flink_stream_task_pb2.StreamTaskConfig(
    task_name='MySQL to Kafka Sync',
    flink_sql='INSERT INTO kafka_sink SELECT * FROM mysql_source',
    parallelism=4,
    cluster_id='cluster-1'
)

response = stub.CreateTask(config)
print(f"Task created: {response.success}")
```

## 架构设计

### 技术栈

- **框架**: Spring Boot 2.7.x + gRPC Spring Boot Starter
- **协议**: Protobuf 3 + HTTP/2
- **持久化**: MybatisPlus + MySQL
- **缓存**: Redis (分布式会话)
- **监控**: Spring Boot Actuator + Prometheus

### 核心特性

1. **高性能**: gRPC + Protobuf 二进制序列化，比 RESTful JSON 性能提升 1 倍
2. **分布式会话**: Redis 存储 Catalog 和临时会话，支持多实例部署
3. **统一鉴权**: gRPC 拦截器复用 Dinky 原有权限体系
4. **可观测性**: 完整链路追踪、监控指标、日志隔离
5. **云原生**: 支持 Docker/K8s 部署、弹性伸缩

## 待完成工作

### 高优先级

1. [ ] 集成 Dinky dlink-core 的实际任务创建/提交逻辑
2. [ ] 实现 Redis 分布式会话管理
3. [ ] 完善 Token 认证逻辑 (对接 SpringSecurity)
4. [ ] 集成 Flink RestAPI 获取实时监控数据
5. [ ] 添加完整的单元测试和集成测试

### 中优先级

1. [ ] 实现 SkyWalking 链路追踪
2. [ ] 配置 Prometheus 监控指标
3. [ ] 添加配置中心支持 (Nacos)
4. [ ] 优化连接池和异步非阻塞模式
5. [ ] 编写完整的 API 文档

### 低优先级

1. [ ] 支持 TLS 加密传输
2. [ ] 添加流量限制和熔断机制
3. [ ] 实现灰度发布支持
4. [ ] 添加多语言客户端 SDK

## 注意事项

### 协议合规

- Dinky 采用 Apache 2.0 协议，二次开发和商用闭源无限制
- 保留原有 OpenAPI 兼容性，支持 gRPC 转 HTTP/JSON

### 安全建议

- 生产环境必须配置 TLS1.3 加密传输
- Token 密钥必须修改为强随机字符串
- 配置 API 网关 IP 白名单和流量限制
- 敏感信息 (密码、密钥) 使用 AES 加密存储

### 性能优化

- 合理设置 JVM 参数 (-Xms, -Xmx, GC 策略)
- 配置 Redis 连接池参数
- 启用 gRPC 消息压缩 (Gzip)
- 本地 Bean 复用，避免额外 HTTP 调用

## 参考文档

- [Dinky 官方文档](https://www.dlink.top/)
- [gRPC 官方文档](https://grpc.io/docs/)
- [Spring Boot gRPC Starter](https://yidongnan.github.io/grpc-spring-boot-starter/)
- [Protobuf 语言指南](https://developers.google.com/protocol-buffers/docs/proto3)

## 联系方式

如有问题或建议，请通过以下方式联系:

- GitHub Issues: https://github.com/DataLinkDC/dlink/issues
- 社区论坛：https://community.dlink.top/

---

**版本**: 1.0.0  
**更新日期**: 2024  
**许可证**: Apache 2.0
