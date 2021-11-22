package com.dlink.app.flinksql;

import com.dlink.app.db.DBConfig;
import com.dlink.app.db.DBUtil;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.parser.SqlType;
import com.dlink.trans.Operations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * FlinkSQLFactory
 *
 * @author wenmo
 * @since 2021/10/27
 **/
public class Submiter {

    private static final Logger logger = LoggerFactory.getLogger(Submiter.class);

    private static String getQuerySQL(Integer id) throws SQLException {
        if (id == null) {
            throw new SQLException("请指定任务ID");
        }
        return "select statement from dlink_task_statement where id = " + id;
    }

    private static String getTaskInfo(Integer id) throws SQLException {
        if (id == null) {
            throw new SQLException("请指定任务ID");
        }
        return "select id, name, alias, type,check_point as checkPoint," +
                "save_point_path as savePointPath, parallelism,fragment,statement_set as statementSet,config" +
                " from dlink_task where id = " + id;
    }

    private static String getFlinkSQLStatement(Integer id, DBConfig config) {
        String statement = "";
        try {
            statement = DBUtil.getOneByID(getQuerySQL(id),config);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            logger.error(LocalDateTime.now().toString() + " --> 获取 FlinkSQL 异常，ID 为"+ id );
            logger.error(LocalDateTime.now().toString() + "连接信息为："+ config.toString() );
            logger.error(LocalDateTime.now().toString() + "异常信息为："+ e.getMessage() );
        }
        return statement;
    }

    public static Map<String,String> getTaskConfig(Integer id, DBConfig config) {
        Map<String,String> task = new HashMap<>();
        try {
            task = DBUtil.getMapByID(getTaskInfo(id),config);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            logger.error(LocalDateTime.now().toString() + " --> 获取 FlinkSQL 配置异常，ID 为"+ id );
            logger.error(LocalDateTime.now().toString() + "连接信息为："+ config.toString() );
            logger.error(LocalDateTime.now().toString() + "异常信息为："+ e.getMessage() );
        }
        return task;
    }

    public static List<String> getStatements(Integer id, DBConfig config){
        return Arrays.asList(getFlinkSQLStatement(id, config).split(FlinkSQLConstant.SEPARATOR));
    }

    public static void submit(Integer id,DBConfig dbConfig){
        logger.info(LocalDateTime.now() + "开始提交作业 -- "+id);
        List<String> statements = Submiter.getStatements(id, dbConfig);
        ExecutorSetting executorSetting = ExecutorSetting.build(Submiter.getTaskConfig(id,dbConfig));
        logger.info("作业配置如下： "+executorSetting.toString());
        Executor executor = Executor.buildAppStreamExecutor(executorSetting);
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
                if (!executorSetting.isUseStatementSet()) {
                    break;
                }
            } else {
                ddl.add(new StatementParam(statement, operationType));
            }
        }
        for (StatementParam item : ddl) {
            logger.info("正在执行 FlinkSQL： "+item.getValue());
            executor.submitSql(item.getValue());
            logger.info("执行成功");
        }
        if(executorSetting.isUseStatementSet()) {
            List<String> inserts = new ArrayList<>();
            for (StatementParam item : trans) {
                if(item.getType().equals(SqlType.INSERT)) {
                    inserts.add(item.getValue());
                }
            }
            logger.info("正在执行 FlinkSQL 语句集： "+String.join(FlinkSQLConstant.SEPARATOR,inserts));
            executor.submitStatementSet(inserts);
            logger.info("执行成功");
        }else{
            for (StatementParam item : trans) {
                logger.info("正在执行 FlinkSQL： "+item.getValue());
                executor.submitSql(item.getValue());
                logger.info("执行成功");
                break;
            }
        }
        logger.info(LocalDateTime.now() + "任务提交成功");
        System.out.println(LocalDateTime.now() + "任务提交成功");
    }
}
