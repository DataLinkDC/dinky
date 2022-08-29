package com.dlink.cdc.mysql;

import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.storage.ConverterType;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @className: com.dlink.cdc.mysql.MysqlJsonDebeziumDeserializationSchema
 * @Description:
 * @author: jack zhong
 * @date 8/2/221:43 PM
 */
public class MysqlJsonDebeziumDeserializationSchema implements DebeziumDeserializationSchema<String> {
    private static final long serialVersionUID = 1L;
    private transient JsonConverter jsonConverter;
    private final Boolean includeSchema;
    private Map<String, Object> customConverterConfigs;

    public MysqlJsonDebeziumDeserializationSchema() {
        this(false);
    }

    public MysqlJsonDebeziumDeserializationSchema(Boolean includeSchema) {
        this.includeSchema = includeSchema;
    }

    public MysqlJsonDebeziumDeserializationSchema(Boolean includeSchema, Map<String, Object> customConverterConfigs) {
        this.includeSchema = includeSchema;
        this.customConverterConfigs = customConverterConfigs;
    }

    public void deserialize(SourceRecord record, Collector<String> out) throws Exception {
        if (this.jsonConverter == null) {
            this.initializeJsonConverter();
        }
        byte[] bytes = this.jsonConverter.fromConnectData(record.topic(), record.valueSchema(), record.value());
        out.collect(new String(bytes, StandardCharsets.UTF_8));
    }

    private void initializeJsonConverter() {
        this.jsonConverter = new JsonConverter();
        HashMap<String, Object> configs = new HashMap(2);
        configs.put("converter.type", ConverterType.VALUE.getName());
        configs.put("schemas.enable", this.includeSchema);
        if (this.customConverterConfigs != null) {
            configs.putAll(this.customConverterConfigs);
        }

        this.jsonConverter.configure(configs);
    }

    public TypeInformation<String> getProducedType() {
        return BasicTypeInfo.STRING_TYPE_INFO;
    }
}
