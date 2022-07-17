package com.dlink.connector.pulsar;

import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableResult;
import org.junit.Test;

/**
 * @author DarrenDa
 * @version 1.0
 * @Desc: Test case
 */
public class PulsarSqlCase {

    @Test
    public void testCase() {

        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inStreamingMode()
                .build();
        TableEnvironment tableEnvironment = TableEnvironment.create(settings);

        tableEnvironment.executeSql("create table source_gen_data(\n" +
                " f_sequence INT,\n" +
                " f_random INT,\n" +
                " f_random_str STRING,\n" +
                " ts AS localtimestamp,\n" +
                " WATERMARK FOR ts AS ts\n" +
                ") WITH (\n" +
                " 'connector' = 'datagen',\n" +
                "\n" +
                " -- optional options --\n" +
                "\n" +
                " 'rows-per-second'='5',\n" +
                "\n" +
                " 'fields.f_sequence.kind'='sequence',\n" +
                " 'fields.f_sequence.start'='1',\n" +
                " 'fields.f_sequence.end'='1000',\n" +
                "\n" +
                " 'fields.f_random.min'='1',\n" +
                " 'fields.f_random.max'='1000',\n" +
                "\n" +
                " 'fields.f_random_str.length'='10'\n" +
                ")")
        ;

        tableEnvironment.executeSql("create table sink_table(\n" +
                " f_sequence INT,\n" +
                " f_random INT,\n" +
                " f_random_str STRING,\n" +
                " ts string\n" +
                ") with (\n" +
                "  'connector' = 'print'\n" +
                ")");

        TableResult tableResult = tableEnvironment.executeSql("insert into sink_table\n" +
                "select\n" +
                " f_sequence ,\n" +
                " f_random ,\n" +
                " f_random_str ,\n" +
                " cast(ts as string)\n" +
                "from source_gen_data");

        tableResult.print();

//        Table tb = tableEnvironment.sqlQuery("select * from source_gen_data");
//        tableResult.execute().print();
    }


    @Test
    public void pulsarTest() throws Exception {
        EnvironmentSettings settings = EnvironmentSettings.newInstance()
                .inStreamingMode()
                .build();
        TableEnvironment tableEnvironment = TableEnvironment.create(settings);

        tableEnvironment.executeSql("" +
                "CREATE TABLE source_pulsar(\n" +
                "    requestId VARCHAR,\n" +
                "    `timestamp` BIGINT,\n" +
                "    `date` VARCHAR,\n" +
                "    appId VARCHAR,\n" +
                "    appName VARCHAR,\n" +
                "    forwardTimeMs VARCHAR,\n" +
                "    processingTimeMs INT,\n" +
                "    errCode VARCHAR,\n" +
                "    userIp VARCHAR,\n" +
                "    createTime bigint,\n" +
                "    b_create_time as TO_TIMESTAMP(FROM_UNIXTIME(createTime/1000,'yyyy-MM-dd HH:mm:ss'),'yyyy-MM-dd HH:mm:ss')\n" +
                ") WITH (\n" +
                "  'connector' = 'pulsar',\n" +
                "  'connector.version' = 'universal',\n" +
                "  'connector.topic' = 'persistent://dlink/dev/context.pulsar',\n" +
                "  'connector.service-url' = 'pulsar://pulsar-dlink-n.stream.com:6650',\n" +
                "  'connector.subscription-name' = 'tmp_print_detail',\n" +
                "  'connector.subscription-type' = 'Shared',\n" +
                "  'connector.subscription-initial-position' = 'Latest',\n" +
                "  'update-mode' = 'append',\n" +
                "  'format' = 'json',\n" +
                "  'format.derive-schema' = 'true'\n" +
                ")")
        ;

        tableEnvironment.executeSql("" +
                "create table sink_pulsar_result(\n" +
                "    requestId VARCHAR,\n" +
                "    `timestamp` BIGINT,\n" +
                "    `date` VARCHAR,\n" +
                "    appId VARCHAR,\n" +
                "    appName VARCHAR,\n" +
                "    forwardTimeMs VARCHAR,\n" +
                "    processingTimeMs INT,\n" +
                "    errCode VARCHAR,\n" +
                "    userIp VARCHAR\n" +
                ") with (\n" +
                "  'connector' = 'print'\n" +
                ")");

        TableResult tableResult = tableEnvironment.executeSql("" +
                "insert into sink_pulsar_result\n" +
                "select \n" +
                "      requestId ,\n" +
                "      `timestamp`,\n" +
                "      `date`,\n" +
                "      appId,\n" +
                "      appName,\n" +
                "      forwardTimeMs,\n" +
                "      processingTimeMs,\n" +
                "      errCode,\n" +
                "      userIp\n" +
                "from source_pulsar");

        tableResult.print();


    }
}
