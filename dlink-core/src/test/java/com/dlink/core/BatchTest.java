package com.dlink.core;

import com.dlink.executor.CustomTableEnvironmentImpl;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ExecutionOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.junit.Test;

/**
 * BatchTest
 *
 * @author wenmo
 * @since 2022/2/7 23:15
 */
public class BatchTest {
    @Test
    public void batchTest() {
        String source = "CREATE TABLE Orders (\n" +
                "    order_number BIGINT,\n" +
                "    price        DECIMAL(32,2),\n" +
                "    buyer        ROW<first_name STRING, last_name STRING>,\n" +
                "    order_time   TIMESTAMP(3)\n" +
                ") WITH (\n" +
                "  'connector' = 'datagen',\n" +
                "  'number-of-rows' = '100'\n" +
                ")";
        String select = "select order_number,price,order_time from Orders";
//        LocalEnvironment environment = ExecutionEnvironment.createLocalEnvironment();
        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
//                .inStreamingMode() // 声明为流任务
                .inBatchMode() // 声明为批任务
                .build();

        TableEnvironment tEnv = TableEnvironment.create(settings);
        tEnv.executeSql(source);
        TableResult tableResult = tEnv.executeSql(select);
        tableResult.print();
    }
}
