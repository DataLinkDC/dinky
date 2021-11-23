package com.dlink.job;

import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.executor.EnvironmentSetting;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.executor.custom.CustomTableEnvironmentImpl;
import com.dlink.explainer.Explainer;
import com.dlink.gateway.Gateway;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.ActionType;
import com.dlink.gateway.config.AppConfig;
import com.dlink.gateway.config.FlinkConfig;
import com.dlink.gateway.config.SavePointType;
import com.dlink.gateway.result.GatewayResult;
import com.dlink.gateway.result.SavePointResult;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.parser.SqlType;
import com.dlink.result.*;
import com.dlink.session.ExecutorEntity;
import com.dlink.session.SessionConfig;
import com.dlink.session.SessionInfo;
import com.dlink.session.SessionPool;
import com.dlink.trans.Operations;
import com.dlink.utils.SqlUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JobManager
 *
 * @author wenmo
 * @since 2021/5/25 15:27
 **/
public class JobManager extends RunTime {

    private static final Logger logger = LoggerFactory.getLogger(JobManager.class);

    private JobHandler handler;
    private String sessionId;
    private Integer maxRowNum = 100;
    private EnvironmentSetting environmentSetting;
    private ExecutorSetting executorSetting;
    private JobConfig config;
    private Executor executor;
    private boolean useGateway = false;

    public JobManager() {
    }

    public void setUseGateway(boolean useGateway) {
        this.useGateway = useGateway;
    }

    public JobManager(String address, ExecutorSetting executorSetting) {
        if (address != null) {
            this.environmentSetting = EnvironmentSetting.build(address);
            this.executorSetting = executorSetting;
            this.executor = createExecutor();
        }
    }

    public JobManager(String address, String sessionId, Integer maxRowNum, ExecutorSetting executorSetting) {
        this.environmentSetting = EnvironmentSetting.build(address);
        this.sessionId = sessionId;
        this.maxRowNum = maxRowNum;
        this.executorSetting = executorSetting;
        this.executor = createExecutorWithSession();
    }

    public JobManager(JobConfig config) {
        this.config = config;
    }

    public static JobManager build() {
        JobManager manager = new JobManager();
        manager.init();
        return manager;
    }

    public static JobManager build(JobConfig config) {
        initGatewayConfig(config);
        JobManager manager = new JobManager(config);
        manager.init();
        return manager;
    }

    private static void initGatewayConfig(JobConfig config) {
        if (useGateway(config.getType())) {
            Asserts.checkNull(config.getGatewayConfig(), "GatewayConfig 不能为空");
            config.getGatewayConfig().setType(GatewayType.get(config.getType()));
            config.getGatewayConfig().setTaskId(config.getTaskId());
            config.getGatewayConfig().getFlinkConfig().setJobName(config.getJobName());
            config.getGatewayConfig().getFlinkConfig().setSavePoint(config.getSavePointPath());
            config.setUseRemote(false);
        }
    }

    public static boolean useGateway(String type) {
        return (GatewayType.YARN_PER_JOB.equalsValue(type) ||
                GatewayType.YARN_APPLICATION.equalsValue(type));
    }

    private Executor createExecutor() {
        initEnvironmentSetting();
        if (!useGateway && config.isUseRemote() && config.getClusterId() != 0) {
            executor = Executor.buildRemoteExecutor(environmentSetting, config.getExecutorSetting());
            return executor;
        } else {
            executor = Executor.buildLocalExecutor(config.getExecutorSetting());
            return executor;
        }
    }

    private Executor createExecutorWithSession() {
        if (config.isUseSession()) {
            ExecutorEntity executorEntity = SessionPool.get(config.getSession());
            if (Asserts.isNotNull(executorEntity)) {
                executor = executorEntity.getExecutor();
                config.setSessionConfig(executorEntity.getSessionConfig());
                initEnvironmentSetting();
                executor.update(executorSetting);
            } else {
                createExecutor();
                SessionPool.push(new ExecutorEntity(config.getSession(), executor));
            }
        } else {
            createExecutor();
        }
        return executor;
    }

    private void initEnvironmentSetting() {
        if (Asserts.isNotNullString(config.getAddress())) {
            environmentSetting = EnvironmentSetting.build(config.getAddress());
        }
    }

    private void initExecutorSetting() {
        executorSetting = config.getExecutorSetting();
    }

    @Override
    public boolean init() {
        useGateway = useGateway(config.getType());
        handler = JobHandler.build();
        initExecutorSetting();
        createExecutorWithSession();
        return false;
    }

    @Override
    public boolean ready() {
        return handler.init();
    }

    @Override
    public boolean success() {
        return handler.success();
    }

    @Override
    public boolean failed() {
        return handler.failed();
    }

    @Override
    public boolean close() {
        JobContextHolder.clear();
        return false;
    }

    public JobResult executeSql(String statement) {
        Job job = Job.init(GatewayType.get(config.getType()), config, executorSetting, executor, statement, useGateway);
        JobContextHolder.setJob(job);
        if (!useGateway) {
            job.setJobManagerAddress(environmentSetting.getAddress());
        }
        ready();
        String currentSql = "";
        JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement));
        CustomTableEnvironmentImpl stEnvironment = executor.getCustomTableEnvironmentImpl();
        try {
            for (StatementParam item : jobParam.getDdl()) {
                currentSql = item.getValue();
                executor.executeSql(item.getValue());
            }
            if (jobParam.getTrans().size() > 0) {
                if (config.isUseStatementSet() && useGateway) {
                    List<String> inserts = new ArrayList<>();
                    for (StatementParam item : jobParam.getTrans()) {
                        inserts.add(item.getValue());
                    }
                    currentSql = String.join(FlinkSQLConstant.SEPARATOR, inserts);
                    JobGraph jobGraph = executor.getJobGraphFromInserts(inserts);
                    GatewayResult gatewayResult = null;
                    if (GatewayType.YARN_APPLICATION.equalsValue(config.getType())) {
                        gatewayResult = Gateway.build(config.getGatewayConfig()).submitJar();
                    } else {
                        gatewayResult = Gateway.build(config.getGatewayConfig()).submitJobGraph(jobGraph);
                    }
                    job.setResult(InsertResult.success(gatewayResult.getAppId()));
                    job.setJobId(gatewayResult.getAppId());
                    job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
                } else if (config.isUseStatementSet() && !useGateway) {
                    List<String> inserts = new ArrayList<>();
                    StatementSet statementSet = stEnvironment.createStatementSet();
                    for (StatementParam item : jobParam.getTrans()) {
                        if (item.getType().equals(SqlType.INSERT)) {
                            statementSet.addInsertSql(item.getValue());
                            inserts.add(item.getValue());
                        }
                    }
                    if (inserts.size() > 0) {
                        currentSql = String.join(FlinkSQLConstant.SEPARATOR, inserts);
                        TableResult tableResult = statementSet.execute();
                        if (tableResult.getJobClient().isPresent()) {
                            job.setJobId(tableResult.getJobClient().get().getJobID().toHexString());
                        }
                        if (config.isUseResult()) {
                            IResult result = ResultBuilder.build(SqlType.INSERT, maxRowNum, "", true).getResult(tableResult);
                            job.setResult(result);
                        }
                    }
                } else if (!config.isUseStatementSet() && useGateway) {
                    List<String> inserts = new ArrayList<>();
                    for (StatementParam item : jobParam.getTrans()) {
                        inserts.add(item.getValue());
                        break;
                    }
                    currentSql = String.join(FlinkSQLConstant.SEPARATOR, inserts);
                    JobGraph jobGraph = executor.getJobGraphFromInserts(inserts);
                    GatewayResult gatewayResult = null;
                    if (GatewayType.YARN_APPLICATION.equalsValue(config.getType())) {
                        gatewayResult = Gateway.build(config.getGatewayConfig()).submitJar();
                    } else {
                        gatewayResult = Gateway.build(config.getGatewayConfig()).submitJobGraph(jobGraph);
                    }
                    job.setResult(InsertResult.success(gatewayResult.getAppId()));
                    job.setJobId(gatewayResult.getAppId());
                    job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
                } else {
                    for (StatementParam item : jobParam.getTrans()) {
                        currentSql = item.getValue();
                        if (!FlinkInterceptor.build(executor, item.getValue())) {
                            TableResult tableResult = executor.executeSql(item.getValue());
                            if (tableResult.getJobClient().isPresent()) {
                                job.setJobId(tableResult.getJobClient().get().getJobID().toHexString());
                            }
                            if (config.isUseResult()) {
                                IResult result = ResultBuilder.build(item.getType(), maxRowNum, "", true).getResult(tableResult);
                                job.setResult(result);
                            }
                        }
                    }
                }
            }
            job.setEndTime(LocalDateTime.now());
            job.setStatus(Job.JobStatus.SUCCESS);
            success();
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] trace = e.getStackTrace();
            StringBuffer resMsg = new StringBuffer("");
            for (StackTraceElement s : trace) {
                resMsg.append(" \n " + s + "  ");
            }
            LocalDateTime now = LocalDateTime.now();
            job.setEndTime(now);
            job.setStatus(Job.JobStatus.FAILED);
            String error = now.toString() + ":" + "运行语句：\n" + currentSql + " \n时出现异常:" + e.getMessage() + " \n >>>堆栈信息<<<" + resMsg.toString();
            job.setError(error);
            failed();
            close();
        }
        close();
        return job.getJobResult();
    }

    private String formatAddress(String webURL) {
        if (Asserts.isNotNullString(webURL)) {
            return webURL.replaceAll("http://", "");
        } else {
            return "";
        }
    }

    private JobParam pretreatStatements(String[] statements) {
        List<StatementParam> ddl = new ArrayList<>();
        List<StatementParam> trans = new ArrayList<>();
        for (String item : statements) {
            String statement = FlinkInterceptor.pretreatStatement(executor, item);
            if (statement.isEmpty()) {
                continue;
            }
            SqlType operationType = Operations.getOperationType(statement);
            if (operationType.equals(SqlType.INSERT) || operationType.equals(SqlType.SELECT)) {
                trans.add(new StatementParam(statement, operationType));
                if (!config.isUseStatementSet()) {
                    break;
                }
            } else {
                ddl.add(new StatementParam(statement, operationType));
            }
        }
        return new JobParam(ddl, trans);
    }

    public IResult executeDDL(String statement) {
        String[] statements = statement.split(";");
        try {
            for (String item : statements) {
                if (item.trim().isEmpty()) {
                    continue;
                }
                SqlType operationType = Operations.getOperationType(item);
                if (SqlType.INSERT == operationType || SqlType.SELECT == operationType) {
                    continue;
                }
                LocalDateTime startTime = LocalDateTime.now();
                TableResult tableResult = executor.executeSql(item);
                IResult result = ResultBuilder.build(operationType, maxRowNum, "", false).getResult(tableResult);
                result.setStartTime(startTime);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ErrorResult();
    }

    public static SelectResult getJobData(String jobId) {
        return ResultPool.get(jobId);
    }

    public static SessionInfo createSession(String session, SessionConfig sessionConfig, String createUser) {
        if (SessionPool.exist(session)) {
            return SessionPool.getInfo(session);
        }
        Executor sessionExecutor = null;
        if (sessionConfig.isUseRemote()) {
            sessionExecutor = Executor.buildRemoteExecutor(EnvironmentSetting.build(sessionConfig.getAddress()), ExecutorSetting.DEFAULT);
        } else {
            sessionExecutor = Executor.buildLocalExecutor(sessionConfig.getExecutorSetting());
        }
        ExecutorEntity executorEntity = new ExecutorEntity(session, sessionConfig, createUser, LocalDateTime.now(), sessionExecutor);
        SessionPool.push(executorEntity);
        return SessionInfo.build(executorEntity);
    }

    public static List<SessionInfo> listSession(String createUser) {
        return SessionPool.filter(createUser);
    }

    public List<SqlExplainResult> explainSql(String statement) {
        Explainer explainer = Explainer.build(executor);
        return explainer.explainSqlResult(statement);
    }

    public ObjectNode getStreamGraph(String statement) {
        Explainer explainer = Explainer.build(executor);
        return explainer.getStreamGraph(statement);
    }

    public boolean cancel(String jobId) {
        if (useGateway && !config.isUseRestAPI()) {
            config.getGatewayConfig().setFlinkConfig(FlinkConfig.build(jobId, ActionType.CANCEL.getValue(),
                    null, null));
            Gateway.build(config.getGatewayConfig()).savepointJob();
            return true;
        } else {
            try{
                return FlinkAPI.build(config.getAddress()).stop(jobId);
            }catch (Exception e){
                logger.info("停止作业时集群不存在");
            }
            return false;
        }
    }

    public SavePointResult savepoint(String jobId,String savePointType) {
        if (useGateway && !config.isUseRestAPI()) {
            config.getGatewayConfig().setFlinkConfig(FlinkConfig.build(jobId, ActionType.SAVEPOINT.getValue(),
                    savePointType, null));
            return Gateway.build(config.getGatewayConfig()).savepointJob();
        } else {
            return null;
        }
    }

}
