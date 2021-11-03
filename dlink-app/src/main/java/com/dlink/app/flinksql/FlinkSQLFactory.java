package com.dlink.app.flinksql;

import com.dlink.app.constant.AppConstant;
import com.dlink.app.db.DBConfig;
import com.dlink.app.db.DBUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

    public static List<String> getStatements(Integer id, DBConfig config){
        return Arrays.asList(getFlinkSQLStatement(id, config).split(AppConstant.FLINKSQL_SEPARATOR));
    }
}
