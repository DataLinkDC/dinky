package com.dlink.trans.ddl;

import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.CDCBuilder;
import com.dlink.cdc.CDCBuilderFactory;
import com.dlink.cdc.SinkBuilderFactory;
import com.dlink.executor.Executor;
import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import com.dlink.trans.AbstractOperation;
import com.dlink.trans.Operation;

/**
 * CreateCDCSourceOperation
 *
 * @author wenmo
 * @since 2022/1/29 23:25
 */
public class CreateCDCSourceOperation extends AbstractOperation implements Operation {

    private String KEY_WORD = "EXECUTE CDCSOURCE";

    public CreateCDCSourceOperation() {
    }

    public CreateCDCSourceOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new CreateCDCSourceOperation(statement);
    }

    @Override
    public TableResult build(Executor executor) {
        CDCSource cdcSource = CDCSource.build(statement);
        FlinkCDCConfig config = new FlinkCDCConfig(cdcSource.getConnector(), cdcSource.getHostname(), cdcSource.getPort(), cdcSource.getUsername()
            , cdcSource.getPassword(), cdcSource.getCheckpoint(), cdcSource.getParallelism(), cdcSource.getDatabase(), cdcSource.getSchema()
            , cdcSource.getTable(), cdcSource.getStartupMode(), cdcSource.getDebezium(), cdcSource.getSink());
        try {
            CDCBuilder cdcBuilder = CDCBuilderFactory.buildCDCBuilder(config);
            Map<String, Map<String, String>> allConfigMap = cdcBuilder.parseMetaDataConfigs();
            config.setSchemaFieldName(cdcBuilder.getSchemaFieldName());
            List<Schema> schemaList = new ArrayList<>();
            final List<String> schemaNameList = cdcBuilder.getSchemaList();
            final List<String> tableRegList = cdcBuilder.getTableList();
            final List<String> schemaTableNameList = new ArrayList<>();
            for (String schemaName : schemaNameList) {
                Schema schema = Schema.build(schemaName);
                if (!allConfigMap.containsKey(schemaName)) {
                    continue;
                }
                DriverConfig driverConfig = DriverConfig.build(allConfigMap.get(schemaName));
                Driver driver = Driver.build(driverConfig);
                final List<Table> tables = driver.listTables(schemaName);
                for (Table table : tables) {
                    if (Asserts.isNotNullCollection(tableRegList)) {
                        for (String tableReg : tableRegList) {
                            if (table.getSchemaTableName().matches(tableReg) && !schema.getTables().contains(Table.build(table.getName()))) {
                                table.setColumns(driver.listColumns(schemaName, table.getName()));
                                schema.getTables().add(table);
                                schemaTableNameList.add(table.getSchemaTableName());
                                break;
                            }
                        }
                    } else {
                        table.setColumns(driver.listColumns(schemaName, table.getName()));
                        schemaTableNameList.add(table.getSchemaTableName());
                        schema.getTables().add(table);
                    }
                }
                schemaList.add(schema);
            }
            config.setSchemaTableNameList(schemaTableNameList);
            config.setSchemaList(schemaList);
            StreamExecutionEnvironment streamExecutionEnvironment = executor.getStreamExecutionEnvironment();
            if (Asserts.isNotNull(config.getParallelism())) {
                streamExecutionEnvironment.setParallelism(config.getParallelism());
            }
            if (Asserts.isNotNull(config.getCheckpoint())) {
                streamExecutionEnvironment.enableCheckpointing(config.getCheckpoint());
            }
            DataStreamSource<String> streamSource = cdcBuilder.build(streamExecutionEnvironment);
            SinkBuilderFactory.buildSinkBuilder(config).build(cdcBuilder, streamExecutionEnvironment, executor.getCustomTableEnvironment(), streamSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
