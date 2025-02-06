package org.apache.flink.table.test.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * Author: lwjhn
 * Date: 2024/5/30 10:11
 * Description:
 */
public class TestUtil {
    public static StreamExecutionEnvironment getLocalExecutionEnvironment(){
        // StreamTableEnvironment tEnv = StreamTableEnvironment.create(StreamExecutionEnvironment.createLocalEnvironment());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        "decimal.handling.mode", "string"
        env.enableCheckpointing(10000);
        // 设置模式为exactly-once （这是默认值）
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 确保检查点之间有至少500 ms的间隔【checkpoint最小间隔】
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        // 检查点必须在10分钟内完成，或者被丢弃【checkpoint的超时时间】
        env.getCheckpointConfig().setCheckpointTimeout(600000);
        // 同一时间只允许进行一个检查点
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        // cancel后，保留Checkpoint数据，以便根据实际需要恢复到指定的Checkpoint
        env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        // scan.incremental.snapshot.enabled
        env.getCheckpointConfig().setCheckpointStorage("file:///dist/test/checkpoint");
        return env;
    }

    public static StreamTableEnvironment getLocalStreamTableEnvironment(){
        return StreamTableEnvironment.create(getLocalExecutionEnvironment());
    }

    public static String loadFile(String path){
        try (InputStream inputStream =
                     Thread.currentThread().getContextClassLoader().getResourceAsStream(path)){
            assert inputStream != null;
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            /*
            String text = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(TestUtil.class.getResourceAsStream(path)), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
             */
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String[] getStatements(String path){
        return SqlUtil.preparedStatement(loadFile(path));
    }

    public static void executeSql(String path) {
        StreamTableEnvironment environment = getLocalStreamTableEnvironment();
        String[] statements = TestUtil.getStatements(path);
        for(String statement : statements){
            if(StringUtils.isBlank(statement=statement.trim()))
                continue;
            System.out.printf("%nFlink SQL [%d]> %s;%n", System.currentTimeMillis(), statement);
            environment.executeSql(statement).print();
        }
        System.out.printf("[%d] END...%n", System.currentTimeMillis());
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
