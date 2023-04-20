package org.zdpx.myflink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternSelectFunction;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.Duration;

/** */
public class CEPBasicUse {
  public static void main(String[] args) {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setParallelism(1);

    SingleOutputStreamOperator<WaterSensor> waterSensorStream =
        env.readTextFile("input/sensor.txt")
            .map(
                (MapFunction<String, WaterSensor>)
                    value -> {
                      String[] split = value.split(",");
                      return new WaterSensor(
                          split[0], Long.parseLong(split[1]) * 1000, Integer.parseInt(split[2]));
                    })
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                    .withTimestampAssigner((element, recordTimestamp) -> element.getTs())); // 指定TS字段为时间戳

    // 匹配两秒内连续出现"sensor_1"和"sensor_2"的情况.
    Pattern<WaterSensor, WaterSensor> pattern =
        Pattern.<WaterSensor>begin("start")
            .where(
                    new IterativeCondition<WaterSensor>() {
                        @Override
                        public boolean filter(WaterSensor value, Context ctx) throws Exception {
                            return "sensor_1".equals(value.getId());
                        }
                    })
                .next("end")
                .where(new SimpleCondition<WaterSensor>() {
                    @Override
                    public boolean filter(WaterSensor o) throws Exception {
                        return "sensor_2".equals(o.getId());
                    }
                })
                .within(Time.seconds(2));

    // 打印匹配的数据
    PatternStream<WaterSensor> waterStreamPS = CEP.pattern(waterSensorStream, pattern);
    waterStreamPS.select((PatternSelectFunction<WaterSensor, String>) Object::toString).print();

    try {
      env.execute();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
