/*
package org.apache.flink.connector.phoenix.internal.options;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.phoenix.internal.AbstractJdbcOutputFormat;
import org.apache.flink.connector.phoenix.internal.connection.JdbcConnectionProvider;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

*/
/**
 * PhoenixSinkFunction
 *
 * @author gy
 * @since 2022/3/22 16:27
 **//*

public class PhoenixSinkFunction <T> extends RichSinkFunction<T>
        implements CheckpointedFunction {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJdbcOutputFormat.class);

    private final JdbcConnectionProvider jdbcConnectionProvider;
    private final JdbcOptions options;
    private static Connection connection = null;
    private static String tableName = "test.ecgbeats12";
    private static PreparedStatement psUp = null;
    private static int batchcount = 0;
    private static int totalcount = 0;
    private static Date startTime;

    public PhoenixSinkFunction(JdbcOptions jdbcOptions,JdbcConnectionProvider jdbcConnectionProvider) {
        this.options = jdbcOptions;
        this.jdbcConnectionProvider = jdbcConnectionProvider;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        Connection connection = jdbcConnectionProvider.getOrEstablishConnection();
        //super.open(parameters);
        */
/*RuntimeContext ctx = getRuntimeContext();
       outputFormat.setRuntimeContext(ctx);
        outputFormat.open(ctx.getIndexOfThisSubtask(), ctx.getNumberOfParallelSubtasks());*//*

*/
/*        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
        Properties properties = new Properties();
        properties.put("phoenix.schema.isNamespaceMappingEnabled", "true");
        properties.put("phoenix.schema.mapSystemTablesToNamespac", "true");
        connection = DriverManager.getConnection("jdbc:phoenix:hd01,hd02,hd03:2181",properties);*//*

        connection.setAutoCommit(false);
        //使用PrepareStatement进行数据的插入，需要指定好对应的Primary Key
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("upsert into " + tableName + "(ecg_id , bindex , btype , bt_flag , af_flag , bmatch , rr, nrr , detpeak , dettresh ) values(?,?,?,?,?,?,?,?,?,?)");
        String sqlUp = sqlBuilder.toString();
        psUp = connection.prepareStatement(sqlUp);


        this.options.getDialect().


    }

    @Override
    public void invoke(T value, Context context) throws IOException {
        psUp.executeUpdate();

        //psUp.addBatch();

        batchcount++;
        totalcount++;
        if (batchcount == 1000) {
            System.out.println("add batch : "+batchcount);
            //Phoenix使用commit（）而不是executeBatch（）来控制批量更新。
            //psUp.executeBatch();
            connection.commit();
            //psUp.clearBatch();

            batchcount = 0;
            System.out.println("totalcount : "+totalcount);
        }
    }

    @Override
    public void initializeState(FunctionInitializationContext context) {}

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        outputFormat.flush();
    }

    @Override
    public void close() {
        //psUp.executeBatch();
        connection.commit();
        //psUp.clearBatch();

        Date endTime = new Date();

        long l = endTime.getTime() - startTime.getTime();

        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);


        System.out.println("========结束写入时间： "+ endTime);
        System.out.println("========运行时间： " + day + "天" + hour + "小时" + min + "分" + s + "秒");



        if (psUp != null ) {
            try {
                psUp.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}*/
