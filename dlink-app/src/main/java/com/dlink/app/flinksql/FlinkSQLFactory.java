package com.dlink.app.flinksql;

import com.dlink.app.db.DBConfig;
import com.dlink.app.db.DBUtil;
import com.dlink.constant.FlinkSQLConstant;
import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.interceptor.FlinkInterceptor;
import com.dlink.parser.SqlType;
import com.dlink.trans.Operations;
import org.apache.flink.table.api.StatementSet;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FlinkSQLFactory
 *
 * @author wenmo
 * @since 2021/10/27
 **/
public class FlinkSQLFactory {

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
            System.err.println(LocalDateTime.now().toString() + " --> 获取 FlinkSQL 异常，ID 为"+ id );
            System.err.println(LocalDateTime.now().toString() + "连接信息为："+ config.toString() );
            System.err.println(LocalDateTime.now().toString() + "异常信息为："+ e.getMessage() );
        }
        return statement;
    }

    public static Map<String,String> getTaskConfig(Integer id, DBConfig config) {
        Map<String,String> task = new HashMap<>();
        try {
            task = DBUtil.getMapByID(getQuerySQL(id),config);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.err.println(LocalDateTime.now().toString() + " --> 获取 FlinkSQL 配置异常，ID 为"+ id );
            System.err.println(LocalDateTime.now().toString() + "连接信息为："+ config.toString() );
            System.err.println(LocalDateTime.now().toString() + "异常信息为："+ e.getMessage() );
        }
        return task;
    }

    public static List<String> getStatements(Integer id, DBConfig config){
        return Arrays.asList(getFlinkSQLStatement(id, config).split(FlinkSQLConstant.SEPARATOR));
    }

    public static void submit(Integer id,DBConfig dbConfig){
        List<String> statements = FlinkSQLFactory.getStatements(Integer.valueOf(id), dbConfig);
        ExecutorSetting executorSetting = ExecutorSetting.build(FlinkSQLFactory.getTaskConfig(Integer.valueOf(id),dbConfig));
        Executor executor = Executor.buildLocalExecutor(executorSetting);
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
        if(executorSetting.isUseStatementSet()) {
            List<String> inserts = new ArrayList<>();
            StatementSet statementSet = executor.createStatementSet();
            for (StatementParam item : trans) {
                if(item.getType().equals(SqlType.INSERT)) {
                    statementSet.addInsertSql(item.getValue());
                    inserts.add(item.getValue());
                }
            }
            if(inserts.size()>0) {
                statementSet.execute();
            }
        }else{
            for (StatementParam item : trans) {
                executor.executeSql(item.getValue());
                break;
            }
        }
        System.out.println(LocalDateTime.now() + "任务提交成功");
    }
}
