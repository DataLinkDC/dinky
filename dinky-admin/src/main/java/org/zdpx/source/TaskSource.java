package org.zdpx.source;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.data.TimestampData;
import org.zdpx.myflink.SimulateGbuZl;

import java.time.LocalDateTime;

/**
 *
 */ //  数据源算子, 模拟产生任务状态数据
public class TaskSource extends RichSourceFunction<RowData> {
    private static final Faker faker = new Faker();
//    static ObjectMapper mapper = new ObjectMapper();
//
//    static {
//        mapper.registerModule(new JavaTimeModule());
//    }

    private boolean flag = true;

    @Override
    public void run(SourceContext<RowData> ctx) throws Exception {

        while (flag) {
            TaskContext taskContext =
                TaskContext.builder()
                    .taskId(SimulateGbuZl.TASK_A)
                    .taskStatus(faker.number().numberBetween(0, 1))
                    .dt(LocalDateTime.now())
                    .build();
//            String taskJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(taskContext);
            ctx.collect(GenericRowData.of(StringData.fromString("task"),
                StringData.fromString(taskContext.getTaskId()),
                taskContext.getTaskStatus(),
                TimestampData.fromLocalDateTime((taskContext.getDt()))));
            Thread.sleep(10000);
        }
    }

    @Override
    public void cancel() {
        flag = false;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskContext {
        private String taskId;
        private int taskStatus;
        private LocalDateTime dt;
    }
}
