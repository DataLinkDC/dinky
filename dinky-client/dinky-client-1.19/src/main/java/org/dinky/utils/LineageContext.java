/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.utils;

import org.dinky.data.model.FunctionResult;
import org.dinky.data.model.LineageRel;
import org.dinky.executor.CustomParser;
import org.dinky.executor.CustomTableEnvironment;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.metadata.RelColumnOrigin;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.sql.SqlNode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.ValidationException;
import org.apache.flink.table.catalog.ContextResolvedFunction;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.catalog.UnresolvedIdentifier;
import org.apache.flink.table.functions.FunctionIdentifier;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.SinkModifyOperation;
import org.apache.flink.table.planner.delegation.PlannerBase;
import org.apache.flink.table.planner.operations.PlannerQueryOperation;
import org.apache.flink.table.planner.plan.schema.TableSourceTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LineageContext
 *
 * @since 2022/11/22
 */
public class LineageContext {

    private static final Logger LOG = LoggerFactory.getLogger(LineageContext.class);

    private final CustomTableEnvironment tableEnv;

    public LineageContext(CustomTableEnvironment tableEnv) {
        this.tableEnv = tableEnv;
    }

    public List<LineageRel> analyzeLineage(String statement) {
        // 1. Generate original relNode tree
        Tuple2<String, RelNode> parsed = parseStatement(statement);
        String sinkTable = parsed.getField(0);
        RelNode oriRelNode = parsed.getField(1);

        // 2. Build lineage based from RelMetadataQuery
        return buildFiledLineageResult(sinkTable, oriRelNode);
    }

    private Tuple2<String, RelNode> parseStatement(String sql) {
        List<Operation> operations = tableEnv.getParser().parse(sql);

        if (operations.size() != 1) {
            throw new TableException("Unsupported SQL query! only accepts a single SQL statement.");
        }
        Operation operation = operations.get(0);
        if (operation instanceof SinkModifyOperation) {
            SinkModifyOperation sinkOperation = (SinkModifyOperation) operation;

            PlannerQueryOperation queryOperation = (PlannerQueryOperation) sinkOperation.getChild();
            RelNode relNode = queryOperation.getCalciteTree();
            return new Tuple2<>(
                    sinkOperation.getContextResolvedTable().getIdentifier().asSummaryString(), relNode);
        } else {
            throw new TableException("Only insert is supported now.");
        }
    }

    /** Check the size of query and sink fields match */
    private void validateSchema(String sinkTable, RelNode relNode, List<String> sinkFieldList) {
        List<String> queryFieldList = relNode.getRowType().getFieldNames();
        if (queryFieldList.size() != sinkFieldList.size()) {
            throw new ValidationException(String.format(
                    "Column types of query result and sink for %s do not match.\n"
                            + "Query schema: %s\n"
                            + "Sink schema:  %s",
                    sinkTable, queryFieldList, sinkFieldList));
        }
    }

    private List<LineageRel> buildFiledLineageResult(String sinkTable, RelNode optRelNode) {
        // target columns
        List<String> targetColumnList =
                tableEnv.from(sinkTable).getResolvedSchema().getColumnNames();

        // check the size of query and sink fields match
        validateSchema(sinkTable, optRelNode, targetColumnList);

        RelMetadataQuery metadataQuery = optRelNode.getCluster().getMetadataQuery();
        List<LineageRel> resultList = new ArrayList<>();

        for (int index = 0; index < targetColumnList.size(); index++) {
            String targetColumn = targetColumnList.get(index);

            Set<RelColumnOrigin> relColumnOriginSet = metadataQuery.getColumnOrigins(optRelNode, index);

            if (CollectionUtils.isNotEmpty(relColumnOriginSet)) {
                for (RelColumnOrigin relColumnOrigin : relColumnOriginSet) {
                    // table
                    RelOptTable table = relColumnOrigin.getOriginTable();
                    String sourceTable = String.join(".", table.getQualifiedName());

                    // filed
                    int ordinal = relColumnOrigin.getOriginColumnOrdinal();
                    List<String> fieldNames = ((TableSourceTable) table)
                            .contextResolvedTable()
                            .getResolvedSchema()
                            .getColumnNames();
                    String sourceColumn = fieldNames.get(ordinal);

                    // add record
                    resultList.add(LineageRel.build(
                            sourceTable, sourceColumn, sinkTable, targetColumn, relColumnOrigin.getTransform()));
                }
            }
        }
        return resultList;
    }

    /**
     *  Analyze custom functions from SQL, does not contain system functions.
     *
     * @param singleSql the SQL statement to analyze
     * @return custom functions set
     */
    public Set<FunctionResult> analyzeFunction(String singleSql) {
        LOG.info("Analyze function Sql: \n {}", singleSql);
        CustomParser parser = (CustomParser) tableEnv.getParser();

        // parsing sql and return the abstract syntax tree
        SqlNode sqlNode = parser.parseSql(singleSql);

        // validate the query
        SqlNode validated = parser.validate(sqlNode);

        // look for all functions
        FunctionVisitor visitor = new FunctionVisitor();
        validated.accept(visitor);
        List<UnresolvedIdentifier> fullFunctionList = visitor.getFunctionList();

        // filter custom functions
        Set<FunctionResult> resultSet = new HashSet<>();
        for (UnresolvedIdentifier unresolvedIdentifier : fullFunctionList) {
            getFunctionCatalog()
                    .lookupFunction(unresolvedIdentifier)
                    .flatMap(ContextResolvedFunction::getIdentifier)
                    // the objectIdentifier of the built-in function is null
                    .flatMap(FunctionIdentifier::getIdentifier)
                    .ifPresent(identifier -> {
                        FunctionResult functionResult = new FunctionResult()
                                .setCatalogName(identifier.getCatalogName())
                                .setDatabase(identifier.getDatabaseName())
                                .setFunctionName(identifier.getObjectName());
                        LOG.debug("analyzed function: {}", functionResult);
                        resultSet.add(functionResult);
                    });
        }
        return resultSet;
    }

    private FunctionCatalog getFunctionCatalog() {
        PlannerBase planner = (PlannerBase) tableEnv.getPlanner();
        return planner.getFlinkContext().getFunctionCatalog();
    }
}
