package com.dlink.app.executor;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;

import java.util.List;

/**
 * Executor
 *
 * @author qiwenkai
 * @since 2021/10/27 15:52
 **/
public class Executor {

    private StreamExecutionEnvironment environment;
    private StreamTableEnvironment stEnvironment;
    private ExecutorSetting executorSetting;
    private SqlManager sqlManager;

    public static Executor build(){
        return new Executor(ExecutorSetting.DEFAULT);
    }

    public static Executor build(ExecutorSetting setting){
        return new Executor(setting);
    }

    private Executor(ExecutorSetting executorSetting) {
        this.executorSetting = executorSetting;
        this.sqlManager = new SqlManager();
        init(executorSetting);
    }

    private void init(ExecutorSetting setting){
        this.environment = StreamExecutionEnvironment.getExecutionEnvironment();
        this.stEnvironment = StreamTableEnvironment.create(this.environment);
    }

    private void executeSql(String statement){
        if(executorSetting.isUseSqlFragment()) {
            statement = sqlManager.parseVariable(statement);
            if(statement.length() > 0 && checkShowFragments(statement)){
                stEnvironment.executeSql(statement);
            }
        }else{
            stEnvironment.executeSql(statement);
        }
    }

    public void submit(List<String> statements){
        for(String statement : statements){
            if(statement==null||"".equals(statement.trim())){
                continue;
            }
            executeSql(statement);
        }
    }

    private boolean checkShowFragments(String sql){
        return sqlManager.checkShowFragments(sql);
    }
}
