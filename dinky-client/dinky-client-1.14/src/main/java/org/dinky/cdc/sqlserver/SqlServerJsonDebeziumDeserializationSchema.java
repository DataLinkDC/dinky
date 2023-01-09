package org.dinky.cdc.sqlserver;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.json.JsonConverter;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.source.SourceRecord;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.storage.ConverterType;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;

/**
 * @version 1.0
 * @className: org.dinky.cdc.mysql.MysqlJsonDebeziumDeserializationSchema
 * @Description:
 * @author: jack zhong
 */
public class SqlServerJsonDebeziumDeserializationSchema implements DebeziumDeserializationSchema<String> {
    private static final long serialVersionUID = 1L;
    private transient JsonConverter jsonConverter;
    private final Boolean includeSchema;
    private Map<String, Object> customConverterConfigs;

    public SqlServerJsonDebeziumDeserializationSchema() {
        this(false);
    }

    public SqlServerJsonDebeziumDeserializationSchema(Boolean includeSchema) {
        this.includeSchema = includeSchema;
    }

    public SqlServerJsonDebeziumDeserializationSchema(Boolean includeSchema, Map<String, Object> customConverterConfigs) {
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
