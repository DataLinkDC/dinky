package org.dinky.zdpx.myflink;

import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.util.Collector;
import org.dinky.zdpx.udf.NTimes;

/**
 *
 */
public class SimulateGbuZl {

    public static final String TASK_A = "taskA";

    public static void main(String[] args) throws Exception {
        sqlStream();
    }

    private static void sqlStream() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.STREAMING);
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

        //    数据流join广播流
        tableEnv.executeSql("CREATE TABLE DT (t STRING, data STRING) WITH ('connector' = 'gbuzl')");

        //    定义广播流
        tableEnv.executeSql("CREATE TABLE TS (t STRING, data STRING) WITH ('connector' = 'task')");

        DataStream<Row> dataDs = tableEnv.toDataStream(tableEnv.sqlQuery("select * from DT"));
        DataStream<Row> taskDS = tableEnv.toDataStream(tableEnv.sqlQuery("select * from TS"));

        MapStateDescriptor<Void, Row> descriptor = new MapStateDescriptor<>("task", Types.VOID, Types.ROW());
        BroadcastConnectedStream<Row, Row> connectDS = dataDs.connect(taskDS.broadcast(descriptor));

        //    与广播流整合的算子, 业务上是将广播流上的状态信息插入到数据流中
        SingleOutputStreamOperator<Tuple3<String, String, String>> resultDS =
            connectDS.process(new BroadcastProcessFunction<Row, Row, Tuple3<String, String, String>>() {
                @Override
                public void processElement(
                    Row value,
                    BroadcastProcessFunction<Row, Row, Tuple3<String, String, String>>.ReadOnlyContext ctx,
                    Collector<Tuple3<String, String, String>> out)
                    throws Exception {
                    ReadOnlyBroadcastState<Void, Row> broadcastState = ctx.getBroadcastState(descriptor);
                    Row state = broadcastState.get(null);
                    if (state != null) {
                        out.collect(Tuple3.of((String) value.getField("t"),
                            (String) value.getField("data"),
                            (String) state.getField("data")));
                    }
                }

                @Override
                public void processBroadcastElement(
                    Row value,
                    BroadcastProcessFunction<Row, Row, Tuple3<String, String, String>>.Context ctx,
                    Collector<Tuple3<String, String, String>> out)
                    throws Exception {
                    BroadcastState<Void, Row> broadcastState = ctx.getBroadcastState(descriptor);
                    broadcastState.clear();
                    broadcastState.put(null, value);
                }
            });

        // 将JSON形式的任务状态数据转换为表格字段的形式
        Table dataTable = tableEnv.fromDataStream(resultDS).as("t", "data", "task");
        tableEnv.createTemporaryView("TT", dataTable);

        tableEnv.executeSql(
            "create view DataTable as " +
                "select CAST(t AS STRING) as type, " +
                "CAST(data AS STRING) as data, " +
                "JSON_VALUE(task, '$.taskId') as taskId, " +
                "CAST(JSON_VALUE(task, '$.taskStatus') AS INT) as taskStatus,  " +
                "PROCTIME() as proc_time " +
                "FROM TT ");

        //    定义数据事件模式匹配, 匹配任务状态变为0后,连续收到5帧状态为0的数据时触发.
        tableEnv.executeSql(
                "create view MatchTable as "
                    + "select * "
                    + "from DataTable "
                    + "MATCH_RECOGNIZE ( "
                    + "PARTITION BY taskId "
                    + "ORDER BY proc_time "
                    + "MEASURES "
                    + "FIRST(A.taskStatus) AS startTaskStatus, "
                    + "LAST(A.taskStatus) AS endTaskStatus "
                    + "ONE ROW PER MATCH "
                    + "AFTER MATCH SKIP PAST LAST ROW "
                    + "PATTERN (A{5}) "
                    + "DEFINE "
                    + "A AS A.taskStatus = 0"
                    + ")")
            .print();

        // 将JSON形式的任务状态数据转换为表格字段的形式
        tableEnv.executeSql(
            "create view GbuTable as "
                + "select type, "
                + "JSON_VALUE(data, '$.id') as id, "
                + "JSON_VALUE(data, '$.longitude') as longitude, "
                + "JSON_VALUE(data, '$.latitude') as latitude, "
                + "JSON_VALUE(data, '$.dt[1]') as dt, "
                + "JSON_VALUE(data, '$.value') as v, "
                + "taskId, taskStatus "
                + "from DataTable "
                + "where type = 'gbu'");

        //    创建GBU数据库映射表
        tableEnv.executeSql(
            "create table GbuSink ( "
                + "id STRING, "
                + "longitude DOUBLE, "
                + "latitude DOUBLE, "
                + "dt TIMESTAMP(3), "
                + "taskId STRING, "
                + "taskStatus INT, "
                + "v DOUBLE) with ( "
                + "'connector' = 'jdbc',"
                + "'url' = 'jdbc:mysql://192.168.1.88:3306/flink?allowPublicKeyRetrieval=true', "
                + "'username' = 'root', "
                + "'password' = '123456', "
                + "'table-name' = 'gbu') ");

        //    将GBU数据落库
        tableEnv.createTemporarySystemFunction("ntimes", NTimes.class);
        tableEnv.executeSql(
            "insert into GbuSink "
                + "select id, CAST(longitude AS DOUBLE), ntimes(CAST(latitude AS DOUBLE), 2),  "
                + "TO_TIMESTAMP(dt), "
                + "taskId, "
                + "taskStatus, "
                + "CAST(v AS DOUBLE) from GbuTable");

        tableEnv.executeSql(
            "create view ZlTable as "
                + "select type, "
                + "JSON_VALUE(data, '$.id') as id, "
                + "JSON_VALUE(data, '$.longitude') as longitude, "
                + "JSON_VALUE(data, '$.latitude') as latitude, "
                + "JSON_VALUE(data, '$.dt[1]') as dt, "
                + "JSON_VALUE(data, '$.value') as v, "
                + "taskId, taskStatus "
                + "from DataTable "
                + "where type = 'zl'");

        //    将触发的事件数据打印到屏幕, 在本场景中相当于5秒有一个打印输出
        Table matchTable = tableEnv.sqlQuery("select * from MatchTable");
        tableEnv.toChangelogStream(matchTable).print();
        env.execute();
    }

}
