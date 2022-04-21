package com.dlink.cdc.jdbc;

import org.apache.flink.api.dag.Transformation;
import org.apache.flink.connector.jdbc.dialect.ClickHouseDialect;
import org.apache.flink.connector.jdbc.dialect.MySQLDialect;
import org.apache.flink.connector.jdbc.dialect.OracleDialect;
import org.apache.flink.connector.jdbc.dialect.PostgresDialect;
import org.apache.flink.connector.jdbc.dialect.SQLServerDialect;
import org.apache.flink.connector.jdbc.internal.options.JdbcOptions;
import org.apache.flink.connector.jdbc.table.JdbcUpsertTableSink;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.types.logical.LogicalType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dlink.assertion.Asserts;
import com.dlink.cdc.AbstractSinkBuilder;
import com.dlink.cdc.CDCBuilder;
import com.dlink.cdc.SinkBuilder;
import com.dlink.executor.CustomTableEnvironment;
import com.dlink.model.FlinkCDCConfig;
import com.dlink.model.Schema;
import com.dlink.model.Table;

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

    @Override
    public void addSink(
        DataStream<RowData> rowDataDataStream,
        String schemaTableName,
        List<String> columnNameList,
        List<LogicalType> columnTypeList) {

        JdbcUpsertTableSink.Builder builder = JdbcUpsertTableSink.builder();

        Map<String, String> sink = config.getSink();
        if (sink.containsKey("sink.buffer-flush.interval")) {
            builder.setFlushIntervalMills(Integer.valueOf(sink.get("sink.buffer-flush.interval")));
        }
        if (sink.containsKey("sink.buffer-flush.max-rows")) {
            builder.setFlushMaxSize(Integer.valueOf(sink.get("sink.buffer-flush.max-rows")));
        }
        if (sink.containsKey("sink.max-retries")) {
            builder.setMaxRetryTimes(Integer.valueOf(sink.get("sink.max-retries")));
        }
        JdbcOptions.Builder jdbcOptionsBuilder = JdbcOptions.builder();
        if (sink.containsKey("connection.max-retry-timeout")) {
            jdbcOptionsBuilder.setConnectionCheckTimeoutSeconds(Integer.valueOf(sink.get("connection.max-retry-timeout")));
        }
        if (sink.containsKey("url")) {
            jdbcOptionsBuilder.setDBUrl(sink.get("url"));
        }
        if (sink.containsKey("dialect")) {
            switch (sink.get("dialect")) {
                case "MySql":
                    jdbcOptionsBuilder.setDialect(new MySQLDialect());
                    break;
                case "Oracle":
                    jdbcOptionsBuilder.setDialect(new OracleDialect());
                    break;
                case "ClickHouse":
                    jdbcOptionsBuilder.setDialect(new ClickHouseDialect());
                    break;
                case "SQLServer":
                    jdbcOptionsBuilder.setDialect(new SQLServerDialect());
                    break;
                case "Postgres":
                    jdbcOptionsBuilder.setDialect(new PostgresDialect());
                    break;
            }

        }
        if (sink.containsKey("driver")) {
            jdbcOptionsBuilder.setDriverName(sink.get("driver"));
        }
        if (sink.containsKey("sink.parallelism")) {
            jdbcOptionsBuilder.setParallelism(Integer.valueOf(sink.get("sink.parallelism")));
        }
        if (sink.containsKey("password")) {
            jdbcOptionsBuilder.setPassword(sink.get("password"));
        }
        if (sink.containsKey("username")) {
            jdbcOptionsBuilder.setUsername(sink.get("username"));
        }
        jdbcOptionsBuilder.setTableName(schemaTableName);
        builder.setOptions(jdbcOptionsBuilder.build());
        builder.setTableSchema(TableSchema.fromTypeInfo(rowDataDataStream.getType()));
        /*JdbcUpsertTableSink build = builder.build();
        build.consumeDataStream(rowDataDataStream);
        rowDataDataStream.addSink(build.);*/
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
                    customTableEnvironment.executeSql(item.getFlinkTableSql(sb.toString() + "'table-name' = '" + item.getSchemaTableName() + "'\n"));
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
