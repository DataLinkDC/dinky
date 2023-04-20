package org.zdpx.myflink;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.zdpx.udf.CountFunction;
import org.zdpx.udf.NTimes;
import org.zdpx.udf.Split;

/** */
public class SqlUdf {
  public static void main(String[] args) {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

    //    注册用户自定义函数
    tableEnv.createTemporarySystemFunction("ntimes", NTimes.class);
    tableEnv.createTemporarySystemFunction("split", Split.class);
    tableEnv.createTemporarySystemFunction("countF", CountFunction.class);

    //    使用使用用户自定义函数
    String createUserBehaviorTableSql =
        "CREATE TABLE user_behavior (\n"
            + "    user_id BIGINT,\n"
            + "    item_id BIGINT,\n"
            + "    category_id BIGINT,\n"
            + "    behavior STRING,\n"
            + "    ts TIMESTAMP(3),\n"
            + "    proctime AS PROCTIME(),\n"
            + "    WATERMARK FOR ts AS ts - INTERVAL '5' SECOND\n"
            + ") WITH (\n"
            + "    'connector' = 'kafka',\n"
            + "    'topic' = 'user_behavior',\n"
            + "    'scan.startup.mode' = 'earliest-offset',\n"
            + "    'properties.bootstrap.servers' = '192.168.17.132:9092',\n"
            + "    'format' = 'json'\n"
            + ")";

    tableEnv.executeSql(createUserBehaviorTableSql);

    String nTimesSql =
        "select "
            + "be, countF(length) as length"
            + " from "
            + "user_behavior, lateral Table(split(behavior)) as T(be, length) "
            + " group by be";

    tableEnv.createTemporaryView("InputTable", tableEnv.sqlQuery(nTimesSql));

    String sql2 = "select be, ntimes(length, 1000) from InputTable";
    tableEnv.executeSql(sql2).print();
  }
}
