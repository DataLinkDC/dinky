package com.dlink.interceptor;

import com.dlink.executor.Executor;
import com.dlink.executor.LocalStreamExecutor;

import org.junit.Test;

/**
 * @author ZackYoung
 * @since 0.6.8
 */
public class CdcSourceTests {
    @Test
    public void flink11() throws Exception {

        String statement = "EXECUTE CDCSOURCE jobname WITH (\n" +
            "  'connector' = 'mysql-cdc',\n" +
            "  'hostname' = '127.0.0.1',\n" +
            "  'port' = '3306',\n" +
            "  'username' = 'root',\n" +
            "  'password' = '123456',\n" +
            "  'checkpoint' = '3000',\n" +
            "  'scan.startup.mode' = 'initial',\n" +
            "  'parallelism' = '1',\n" +
            "  'table-name' = 'dlink\\.dlink_history',\n" +
            "  'sink.connector'='print'\n" +
            ")";

        Executor build = LocalStreamExecutor.build();
        build.executeSql(statement);
        build.execute("");
    }
}
