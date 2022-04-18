package com.dlink.cdc.jdbc;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.executor.CustomTableEnvironment;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.dag.Transformation;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MysqlCDCBuilder
 *
 * @author wenmo
 * @since 2022/4/12 21:29
 **/
public class JdbcSinkBuilder extends AbstractSinkBuilder implements SinkBuilder {

    private final static String KEY_WORD = "jdbc";
    private final static String TABLE_NAME = "cdc_table";

    public JdbcSinkBuilder() {
    }

    public JdbcSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new JdbcSinkBuilder(config);
    }

    /*@Override
    public DataStreamSource build(StreamExecutionEnvironment env, DataStreamSource<String> dataStreamSource) {
        final List<Schema> schemaList = config.getSchemaList();
        if (Asserts.isNotNullCollection(schemaList)) {
            for (Schema schema : schemaList) {
                for (Table table : schema.getTables()) {
                    *//*dataStreamSource.filter(new FilterFunction<Map>() {
                        @Override
                        public boolean filter(Map value) throws Exception {
                            return value.containsKey("table_name") && table.getName().equals(value.get("table_name"));
                        }
                    });
                    dataStreamSource.addSink(
                    JdbcSink.sink(
                            "insert into books (id, title, authors, year) values (?, ?, ?, ?)",
                            (statement, book) -> {
                                statement.setLong(1, book.id);
                                statement.setString(2, book.title);
                                statement.setString(3, book.authors);
                                statement.setInt(4, book.year);
                            },
                            JdbcExecutionOptions.builder()
                                    .withBatchSize(1000)
                                    .withBatchIntervalMs(200)
                                    .withMaxRetries(5)
                                    .build(),
                            new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                    .withUrl("jdbc:postgresql://dbhost:5432/postgresdb")
                                    .withDriverName("org.postgresql.Driver")
                                    .withUsername("someUser")
                                    .withPassword("somePassword")
                                    .build()
                    ));*//*
                }
            }
        }
        return dataStreamSource;
    }*/

    @Override
    public DataStreamSource build(CDCBuilder cdcBuilder, StreamExecutionEnvironment env, CustomTableEnvironment customTableEnvironment, DataStreamSource<String> dataStreamSource) {
        final List<Schema> schemaList = config.getSchemaList();
        if (Asserts.isNotNullCollection(schemaList)) {
            /*org.apache.flink.table.api.Table table = env.fromChangelogStream(dataStreamSource);
            env.registerTable("cdc_table",table);*/
            customTableEnvironment.registerDataStream(TABLE_NAME, dataStreamSource);
            List<ModifyOperation> modifyOperations = new ArrayList();
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : config.getSink().entrySet()) {
                sb.append("'");
                sb.append(entry.getKey());
                sb.append("' = '");
                sb.append(entry.getValue());
                sb.append("',\n");
            }
            for (Schema schema : schemaList) {
                for (Table item : schema.getTables()) {
                    customTableEnvironment.executeSql(item.getFlinkTableSql(sb.toString()+"'table-name' = '"+item.getSchemaTableName()+"'\n"));
                    List<Operation> operations = customTableEnvironment.getParser().parse(cdcBuilder.getInsertSQL(item, TABLE_NAME));
                    if (operations.size() > 0) {
                        Operation operation = operations.get(0);
                        if (operation instanceof ModifyOperation) {
                            modifyOperations.add((ModifyOperation) operation);
                        }
                    }
                }
            }
            List<Transformation<?>> trans = customTableEnvironment.getPlanner().translate(modifyOperations);
            for (Transformation<?> item : trans) {
                env.addOperator(item);
            }
        }
        return dataStreamSource;
    }
}
