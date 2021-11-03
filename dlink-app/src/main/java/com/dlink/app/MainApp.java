package com.dlink.app;

import com.dlink.app.assertion.Asserts;
import com.dlink.app.db.DBConfig;
import com.dlink.app.executor.Executor;
import com.dlink.app.flinksql.FlinkSQLFactory;
import org.apache.flink.api.java.utils.ParameterTool;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * MainApp
 *
 * @author wenmo
 * @since 2021/10/27
 **/
public class MainApp {

    public static void main(String[] args) throws IOException {
        System.out.println(LocalDateTime.now() + "任务开始");
        ParameterTool parameters = ParameterTool.fromArgs(args);
        String id = parameters.get("id", null);
        if (Asserts.isNotNullString(id)) {
            Executor.build().submit(FlinkSQLFactory.getStatements(Integer.valueOf(id), DBConfig.build(parameters)));
        }
    }
}
