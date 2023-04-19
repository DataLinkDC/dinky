package org.dinky.zdpx.myflink;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/** */
public class SqlCepExample {

  public static void main(String[] args) {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
    String createTableSql =
        "create table temp_record( "
            + "rack_id int,"
            + " ts timestamp(3),"
            + " temp int,"
            + " WATERMARK FOR ts AS ts - INTERVAL '1' SECOND) "
            + "with ('connector'='filesystem', 'path'='input/temp_record.csv', 'format'='csv')";
    tableEnv.executeSql(createTableSql);

    String cepSql = "select * from temp_record " +
            "match_recognize(" +
            "partition by rack_id " +
            "order by ts " +
            "measures " +
            "A.ts as start_ts, " +
            "last(B.ts) as end_ts, " +
            "A.temp as start_temp, " +
            "last(B.temp) as end_temp, " +
            "avg(B.temp) as avg_temp " +
            "one row per match " +
            "after match skip to next row " +
            "pattern (A B+ C) within interval '90' second " +
            "define  " +
            "A as A.temp < 50, " +
            "B as B.temp >=50, " +
            "C as C.temp < 50 " +
            ")";
    tableEnv.executeSql(cepSql).print();
  }
}
