package org.dinky.zdpx.source;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.StringData;
import org.apache.flink.table.data.TimestampData;

import java.time.LocalDateTime;

/**
 *
 */ //  数据源算子, 模拟产生GBU和ZL目标数据
public class GbuZlDataSource implements SourceFunction<RowData> {
    private boolean isRunning = true;

    @Override
    public void run(SourceContext<RowData> ctx) throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
        boolean tombStone = false;
        Thread.sleep(2000);

        while (isRunning) {
            GBU gbu = GBU.generate();
//            String gbuJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(gbu);

            ctx.collect(GenericRowData.of(StringData.fromString("gbu"),
                StringData.fromString("taskA"),
                StringData.fromString(gbu.getId()),
                gbu.getLongitude(),
                gbu.getLatitude(),
                TimestampData.fromLocalDateTime((gbu.getDt())),
                gbu.getValue()));

//            String zlJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ZL.generate());
            ZL zl = ZL.generate();
            if (!tombStone) {
                ctx.collect(GenericRowData.of(StringData.fromString("zl"),
                    StringData.fromString("taskA"),
                    StringData.fromString(zl.getId()),
                    zl.getLongitude(),
                    zl.getLatitude(),
                    TimestampData.fromLocalDateTime((zl.getDt())),
                    zl.getValue()));
                tombStone = true;
            }
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }

    /**
     * GBU 数据格式
     */
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GBU {
        private static Faker faker = new Faker();

        private String id;
        private double longitude;
        private double latitude;

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        private LocalDateTime dt;

        private double value;

        public static GBU generate() {
            return GBU.builder()
                .id(faker.idNumber().validSvSeSsn())
                .longitude(faker.number().numberBetween(0, 180))
                .latitude(faker.number().numberBetween(0, 90))
                .dt(LocalDateTime.now())
                .value(faker.number().randomDouble(2, 0, 1000))
                .build();
        }
    }

    /**
     * ZL数据格式
     */
    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZL {
        private static Faker faker = new Faker();

        private String id;
        private double longitude;
        private double latitude;

        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        private LocalDateTime dt;

        private int number;
        private double value;

        public static ZL generate() {
            return ZL.builder()
                .id(faker.idNumber().ssnValid())
                .longitude(faker.number().numberBetween(0, 180))
                .latitude(faker.number().numberBetween(0, 90))
                .number(faker.number().numberBetween(0, 100))
                .value(faker.number().randomDouble(2, 0, 1000))
                .dt(LocalDateTime.now())
                .build();
        }

    }
}

