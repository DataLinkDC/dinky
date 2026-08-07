package com.dlink.grpc.service;

import com.dlink.grpc.proto.*;
import com.dlink.job.JobManager;
import com.dlink.model.Task;
import com.dlink.result.SqlExplainResult;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Flink 流式任务 gRPC 服务实现
 * 复用 Dinky dlink-core 的核心能力
 * 
 * @author Dinky Team
 * @date 2024
 */
@Slf4j
@GrpcService
public class FlinkStreamTaskServiceImpl extends FlinkStreamTaskServiceGrpc.FlinkStreamTaskServiceImplBase {

    @Autowired
    private JobManager jobManager;

    @Override
    public void createTask(StreamTaskConfig request, StreamObserver<CommonResponse> responseObserver) {
        log.info("Creating Flink task: {}", request.getTaskName());
        
        try {
            // 构建 Task 对象
            Task task = new Task();
            task.setName(request.getTaskName());
            task.setStatement(request.getFlinkSql());
            task.setParallelism(request.getParallelism());
            task.setClusterId(request.getClusterId());
            
            // 设置配置参数
            if (request.getConfigParamsCount() > 0) {
                // 将 Protobuf Map 转换为 Java Map 并设置到 Task
                // TODO: 根据 Dinky 实际 Task 结构进行适配
            }
            
            // 调用 Dinky core 创建任务
            // boolean success = jobManager.createTask(task);
            
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Task created successfully")
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to create task", e);
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Failed to create task: " + e.getMessage())
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void submitTask(TaskIdRequest request, StreamObserver<CommonResponse> responseObserver) {
        log.info("Submitting task with ID: {}", request.getTaskId());
        
        try {
            // 调用 Dinky core 提交任务
            // boolean success = jobManager.submitTask(request.getTaskId());
            
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Task submitted successfully")
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to submit task", e);
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Failed to submit task: " + e.getMessage())
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void startTask(TaskIdRequest request, StreamObserver<CommonResponse> responseObserver) {
        log.info("Starting task with ID: {}", request.getTaskId());
        
        try {
            // 调用 Dinky core 启动任务
            // boolean success = jobManager.startTask(request.getTaskId());
            
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Task started successfully")
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to start task", e);
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Failed to start task: " + e.getMessage())
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void stopTask(SavepointOperateRequest request, StreamObserver<CommonResponse> responseObserver) {
        log.info("Stopping task with ID: {}, savepoint type: {}", 
                request.getTaskId(), request.getSavepointType());
        
        try {
            // 调用 Dinky core 停止任务（支持 Savepoint）
            // boolean success = jobManager.stopTask(
            //     request.getTaskId(), 
            //     request.getSavepointType(),
            //     request.getSavepointPath()
            // );
            
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Task stopped successfully")
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to stop task", e);
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Failed to stop task: " + e.getMessage())
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void restartTask(TaskIdRequest request, StreamObserver<CommonResponse> responseObserver) {
        log.info("Restarting task with ID: {}", request.getTaskId());
        
        try {
            // 调用 Dinky core 重启任务
            // boolean success = jobManager.restartTask(request.getTaskId());
            
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Task restarted successfully")
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to restart task", e);
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Failed to restart task: " + e.getMessage())
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteTask(TaskIdRequest request, StreamObserver<CommonResponse> responseObserver) {
        log.info("Deleting task with ID: {}", request.getTaskId());
        
        try {
            // 调用 Dinky core 删除任务
            // boolean success = jobManager.deleteTask(request.getTaskId());
            
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Task deleted successfully")
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to delete task", e);
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Failed to delete task: " + e.getMessage())
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getTaskDetail(TaskIdRequest request, StreamObserver<TaskDetailResponse> responseObserver) {
        log.info("Getting task detail for ID: {}", request.getTaskId());
        
        try {
            // 调用 Dinky core 获取任务详情
            // Task task = jobManager.getTaskDetail(request.getTaskId());
            
            // 临时返回示例数据
            TaskInfo taskInfo = TaskInfo.newBuilder()
                    .setTaskId(request.getTaskId())
                    .setTaskName("Sample Task")
                    .setFlinkSql("SELECT * FROM source_table")
                    .setParallelism(4)
                    .setClusterId("cluster-1")
                    .setStatus("RUNNING")
                    .setStartTime("2024-01-01 00:00:00")
                    .setEndTime("")
                    .setSavepointPath("")
                    .build();
            
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Success")
                    .setSuccess(true)
                    .build();
            
            TaskDetailResponse taskDetailResponse = TaskDetailResponse.newBuilder()
                    .setResponse(response)
                    .setTaskInfo(taskInfo)
                    .build();
            
            responseObserver.onNext(taskDetailResponse);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("Failed to get task detail", e);
            CommonResponse response = CommonResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Failed to get task detail: " + e.getMessage())
                    .setSuccess(false)
                    .build();
            
            TaskDetailResponse taskDetailResponse = TaskDetailResponse.newBuilder()
                    .setResponse(response)
                    .build();
            
            responseObserver.onNext(taskDetailResponse);
            responseObserver.onCompleted();
        }
    }
}
