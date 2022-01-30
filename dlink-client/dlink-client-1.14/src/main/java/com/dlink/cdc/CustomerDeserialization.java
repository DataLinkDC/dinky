package com.dlink.cdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CustomerDeserialization
 *
 * @author wenmo
 * @since 2022/1/29 22:16
 */
public class CustomerDeserialization implements DebeziumDeserializationSchema<String> {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {

        //1.创建 JSON 对象用于存储最终数据
        Map result = new HashMap<String,String>();
        //2.获取库名&表名放入 source
        String topic = sourceRecord.topic();
        String[] fields = topic.split("\\.");
        String database = fields[1];
        String tableName = fields[2];
        Map source = new HashMap<String,String>();
        source.put("database",database);
        source.put("table",tableName);

        Struct value = (Struct) sourceRecord.value();
        //3.获取"before"数据
        Struct before = value.getStruct("before");
        Map beforeJson = new HashMap<String,String>();
        if (before != null) {
            Schema beforeSchema = before.schema();
            List<Field> beforeFields = beforeSchema.fields();
            for (Field field : beforeFields) {
                Object beforeValue = before.get(field);
                beforeJson.put(field.name(), beforeValue);
            }
        }

        //4.获取"after"数据
        Struct after = value.getStruct("after");
        Map afterJson = new HashMap<String,String>();
        if (after != null) {
            Schema afterSchema = after.schema();
            List<Field> afterFields = afterSchema.fields();
            for (Field field : afterFields) {
                Object afterValue = after.get(field);
                afterJson.put(field.name(), afterValue);
            }
        }

        //5.获取操作类型  CREATE UPDATE DELETE 进行符合 Debezium-op 的字母
        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
        String type = operation.toString().toLowerCase();
        if ("insert".equals(type)) {
            type = "c";
        }
        if ("update".equals(type)) {
            type = "u";
        }
        if ("delete".equals(type)) {
            type = "d";
        }
        if ("create".equals(type)) {
            type = "c";
        }

        //6.将字段写入 JSON 对象
        result.put("source", source);
        result.put("before", beforeJson);
        result.put("after", afterJson);
        result.put("op", type);

        //7.输出数据
        collector.collect(mapper.writeValueAsString(result));
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return BasicTypeInfo.STRING_TYPE_INFO;
    }
}
