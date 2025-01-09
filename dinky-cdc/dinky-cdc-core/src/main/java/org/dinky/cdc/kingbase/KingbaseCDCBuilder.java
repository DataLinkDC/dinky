package org.dinky.cdc.kingbase;

import com.ververica.cdc.connectors.kingbase.KingbaseSource;
import com.ververica.cdc.debezium.DebeziumSourceFunction;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.commons.lang3.StringUtils;
import org.dinky.assertion.Asserts;
import org.dinky.cdc.CDCBuilder;
import org.dinky.cdc.postgres.PostgresCDCBuilder;
import org.dinky.data.model.FlinkCDCConfig;

import java.util.List;
import java.util.Properties;

/**
 * Author: lwjhn
 * Date: 2023/8/24 14:34
 * Description:
 */
public class KingbaseCDCBuilder extends PostgresCDCBuilder {
    public static final String KEY_WORD = "kingbase-cdc";

    public KingbaseCDCBuilder() {
    }

    public KingbaseCDCBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public String getMetadataType() {
        return "kingbase";
    }

    @Override
    public String getSourceName() {
        return "Kingbase CDC Source";
    }

    @Override
    public String getDriverPrefix() {
        return "jdbc:kingbase8";
    }

    @Override
    public CDCBuilder create(FlinkCDCConfig config) {
        return new KingbaseCDCBuilder(config);
    }


    public DebeziumSourceFunction<String> createSourceFunction(Properties debeziumProperties, String decodingPluginName, String slotName) {
        KingbaseSource.Builder<String> sourceBuilder = KingbaseSource.<String>builder()
                .hostname(config.getHostname())
                .port(config.getPort())
                .username(config.getUsername())
                .password(config.getPassword())
                .database(config.getDatabase());

        List<String> schemaTableNameList = config.getSchemaTableNameList();
        if (Asserts.isNotNullCollection(schemaTableNameList)) {
            sourceBuilder.tableList(schemaTableNameList.toArray(new String[0]));
        } else {
            sourceBuilder.tableList();
        }

        sourceBuilder.deserializer(new JsonDebeziumDeserializationSchema());
        sourceBuilder.debeziumProperties(debeziumProperties);

        if(StringUtils.isNotBlank(slotName)){
            sourceBuilder.slotName(slotName);
        }
        if(StringUtils.isNotBlank(decodingPluginName)){
            sourceBuilder.decodingPluginName(decodingPluginName);
        }
        return sourceBuilder.build();
    }
}
