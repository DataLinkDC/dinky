package com.dlink.executor;

import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.StatementSet;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.delegation.Parser;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.dlink.result.SqlExplainResult;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * CustomTableEnvironment
 *
 * @author wenmo
 * @since 2022/2/5 10:35
 */
public interface CustomTableEnvironment {

    TableConfig getConfig();

    CatalogManager getCatalogManager();

    void registerCatalog(String catalogName, Catalog catalog);

    String[] listCatalogs();

    Optional<Catalog> getCatalog(String catalogName);

    TableResult executeSql(String statement);

    Table sqlQuery(String statement);

    void registerTable(String name, Table table);

    String explainSql(String statement, ExplainDetail... extraDetails);

    ObjectNode getStreamGraph(String statement);

    JobPlanInfo getJobPlanInfo(List<String> statements);

    StreamGraph getStreamGraphFromInserts(List<String> statements);

    JobGraph getJobGraphFromInserts(List<String> statements);

    SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails);

    boolean parseAndLoadConfiguration(String statement, StreamExecutionEnvironment config, Map<String, Object> setMap);

    StatementSet createStatementSet();

    <T> void createTemporaryView(String path, DataStream<T> dataStream, Expression... fields);

    <T> void createTemporaryView(String path, DataStream<T> dataStream, String fields);

//    <T> void createTemporaryView(String path, DataStream<T> dataStream, Schema schema);

    Parser getParser();

    Planner getPlanner();
}
