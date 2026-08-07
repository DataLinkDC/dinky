package com.dlink.grpc.service;

import com.dlink.grpc.proto.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

/**
 * Flink 数据源 gRPC 服务实现
 * 提供数据源注册、验证、连通性测试能力
 * 
 * @author Dinky Team
 * @date 2024
 */
@Slf4j
@GrpcService
public class FlinkDataSourceServiceImpl extends FlinkDataSourceServiceGrpc.FlinkDataSourceServiceImplBase {

    @Override
    public void registerDataSource(DataSourceConfig request, StreamObserver<CommonResponse> responseObserver) {
        log.info("Registering data source: {}", request.getSourceName());
        
        try {
            // TODO: 调用 Dinky metadata 注册数据源
            
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Data source registered successfully")
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to register data source", e);
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Failed to register data source: " + e.getMessage())
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateDataSource(DataSourceConfig request, StreamObserver<CommonResponse> responseObserver) {
        log.info("Updating data source: {}", request.getSourceName());
        
        try {
            // TODO: 调用 Dinky metadata 更新数据源
            
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Data source updated successfully")
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to update data source", e);
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Failed to update data source: " + e.getMessage())
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteDataSource(DataSourceIdRequest request, StreamObserver<CommonResponse> responseObserver) {
        log.info("Deleting data source with ID: {}", request.getSourceId());
        
        try {
            // TODO: 调用 Dinky metadata 删除数据源
            
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Data source deleted successfully")
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to delete data source", e);
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Failed to delete data source: " + e.getMessage())
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void testDataSourceConnectivity(DataSourceTestRequest request, StreamObserver<CommonResponse> responseObserver) {
        log.info("Testing data source connectivity: {} - {}", request.getType(), request.getUrl());
        
        try {
            // TODO: 调用 Dinky metadata 测试数据源连通性
            // boolean success = dataSourceService.testConnection(request);
            
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Data source connectivity test passed")
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to test data source connectivity", e);
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Failed to test data source connectivity: " + e.getMessage())
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void validateDataSourceWithCluster(DataSourceTestRequest request, 
                                              StreamObserver<ValidationResponse> responseObserver) {
        log.info("Validating data source with cluster: {} - {}", request.getType(), request.getUrl());
        
        try {
            // TODO: 调用 Dinky core 验证数据源并推荐合适的集群
            // ValidationResult result = dataSourceService.validateWithCluster(request);
            
            // 临时返回示例数据
            ValidationResponse response = ValidationResponse.newBuilder()
                    .setResponse(CommonResponse.newBuilder()
                            .setCode(200)
                            .setMessage("Validation passed")
                            .setSuccess(true)
                            .build())
                    .setIsValid(true)
                    .setValidationMessage("Data source is valid and compatible with the suggested cluster")
                    .setSuggestedClusterId("cluster-1")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to validate data source with cluster", e);
            ValidationResponse response = ValidationResponse.newBuilder()
                    .setResponse(CommonResponse.newBuilder()
                            .setCode(500)
                            .setMessage("Failed to validate data source: " + e.getMessage())
                            .setSuccess(false)
                            .build())
                    .setIsValid(false)
                    .setValidationMessage("Validation failed: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void listDataSources(DataSourceListRequest request, StreamObserver<DataSourceListResponse> responseObserver) {
        log.info("Listing data sources, page: {}, size: {}, type: {}", 
                request.getPageNum(), request.getPageSize(), request.getSourceType());
        
        try {
            // TODO: 调用 Dinky metadata 获取数据源列表
            
            // 临时返回示例数据
            List<DataSourceInfo> dataSources = new ArrayList<>();
            dataSources.add(DataSourceInfo.newBuilder()
                    .setSourceId(1L)
                    .setSourceName("MySQL Production")
                    .setUrl("jdbc:mysql://mysql-prod:3306/dinky")
                    .setUsername("admin")
                    .setType("MYSQL")
                    .setStatus("NORMAL")
                    .setCreateTime("2024-01-01 00:00:00")
                    .build());
            dataSources.add(DataSourceInfo.newBuilder()
                    .setSourceId(2L)
                    .setSourceName("Kafka Cluster")
                    .setUrl("kafka://kafka-cluster:9092")
                    .setUsername("")
                    .setType("KAFKA")
                    .setStatus("NORMAL")
                    .setCreateTime("2024-01-05 10:00:00")
                    .build());
            dataSources.add(DataSourceInfo.newBuilder()
                    .setSourceId(3L)
                    .setSourceName("Doris Warehouse")
                    .setUrl("jdbc:doris://doris-prod:9030")
                    .setUsername("root")
                    .setType("DORIS")
                    .setStatus("NORMAL")
                    .setCreateTime("2024-01-10 15:30:00")
                    .build());
            
            DataSourceListResponse response = DataSourceListResponse.newBuilder()
                    .setResponse(CommonResponse.newBuilder()
                            .setCode(200)
                            .setMessage("Success")
                            .setSuccess(true)
                            .build())
                    .addAllDataSources(dataSources)
                    .setTotal(dataSources.size())
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to list data sources", e);
            DataSourceListResponse response = DataSourceListResponse.newBuilder()
                    .setResponse(CommonResponse.newBuilder()
                            .setCode(500)
                            .setMessage("Failed to list data sources: " + e.getMessage())
                            .setSuccess(false)
                            .build())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
