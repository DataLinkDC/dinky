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

package com.dlink.utils;

import static org.apache.calcite.jdbc.CalciteSchemaBuilder.asRootSchema;

import com.dlink.model.LineageRel;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Snapshot;
import org.apache.calcite.rel.metadata.RelColumnOrigin;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rex.RexBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.api.TableConfig;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.ValidationException;
import org.apache.flink.table.api.internal.TableEnvironmentImpl;
import org.apache.flink.table.catalog.CatalogManager;
import org.apache.flink.table.catalog.FunctionCatalog;
import org.apache.flink.table.module.ModuleManager;
import org.apache.flink.table.operations.CatalogSinkModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.planner.calcite.SqlExprToRexConverter;
import org.apache.flink.table.planner.calcite.SqlExprToRexConverterFactory;
import org.apache.flink.table.planner.catalog.CatalogManagerCalciteSchema;
import org.apache.flink.table.planner.delegation.PlannerBase;
import org.apache.flink.table.planner.delegation.PlannerContext;
import org.apache.flink.table.planner.operations.PlannerQueryOperation;
import org.apache.flink.table.planner.plan.optimize.program.FlinkChainedProgram;
import org.apache.flink.table.planner.plan.optimize.program.StreamOptimizeContext;
import org.apache.flink.table.planner.plan.trait.MiniBatchInterval;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;

/**
 * LineageContext
 *
 * @author wenmo
 * @since 2022/11/21
 */
public class LineageContext {

    private final FlinkChainedProgram flinkChainedProgram;
    private final TableEnvironmentImpl tableEnv;

    public LineageContext(FlinkChainedProgram flinkChainedProgram, TableEnvironmentImpl tableEnv) {
        this.flinkChainedProgram = flinkChainedProgram;
        this.tableEnv = tableEnv;
    }

    /**
     * Dynamic add getColumnOrigins method to class RelMdColumnOrigins by javassist:
     *
     * public Set<RelColumnOrigin> getColumnOrigins(Snapshot rel,RelMetadataQuery mq, int iOutputColumn) {
     *      return mq.getColumnOrigins(rel.getInput(), iOutputColumn);
     * }
     */
    static {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.getCtClass("org.apache.calcite.rel.metadata.RelMdColumnOrigins");

            CtClass[] parameters = new CtClass[]{classPool.get(Snapshot.class.getName()),
                    classPool.get(RelMetadataQuery.class.getName()), CtClass.intType
            };
            // add method
            CtMethod ctMethod = new CtMethod(classPool.get("java.util.Set"), "getColumnOrigins", parameters, ctClass);
            ctMethod.setModifiers(Modifier.PUBLIC);
            ctMethod.setBody("{return $2.getColumnOrigins($1.getInput(), $3);}");
            ctClass.addMethod(ctMethod);
            // load the class
            ctClass.toClass();
        } catch (Exception e) {
            throw new TableException("Dynamic add getColumnOrigins() method exception.", e);
        }
    }

    public List<LineageRel> getLineage(String statement) {
        // 1. Generate original relNode tree
        Tuple2<String, RelNode> parsed = parseStatement(statement);
        String sinkTable = parsed.getField(0);
        RelNode oriRelNode = parsed.getField(1);

        // 2. Optimize original relNode to generate Optimized Logical Plan
        RelNode optRelNode = optimize(oriRelNode);

        // 3. Build lineage based from RelMetadataQuery
        return buildFiledLineageResult(sinkTable, optRelNode);
    }

    private Tuple2<String, RelNode> parseStatement(String sql) {
        List<Operation> operations = tableEnv.getParser().parse(sql);

        if (operations.size() != 1) {
            throw new TableException(
                    "Unsupported SQL query! only accepts a single SQL statement.");
        }
        Operation operation = operations.get(0);
        if (operation instanceof CatalogSinkModifyOperation) {
            CatalogSinkModifyOperation sinkOperation = (CatalogSinkModifyOperation) operation;

            PlannerQueryOperation queryOperation = (PlannerQueryOperation) sinkOperation.getChild();
            RelNode relNode = queryOperation.getCalciteTree();
            return new Tuple2<>(
                    sinkOperation.getTableIdentifier().asSummaryString(),
                    relNode);
        } else {
            throw new TableException("Only insert is supported now.");
        }
    }

    /**
     * Calling each program's optimize method in sequence.
     */
    private RelNode optimize(RelNode relNode) {
        return flinkChainedProgram.optimize(relNode, new StreamOptimizeContext() {

            @Override
            public TableConfig getTableConfig() {
                return tableEnv.getConfig();
            }

            @Override
            public FunctionCatalog getFunctionCatalog() {
                return new FunctionCatalog(tableEnv.getConfig(), tableEnv.getCatalogManager(), new ModuleManager());
            }

            @Override
            public CatalogManager getCatalogManager() {
                return tableEnv.getCatalogManager();
            }

            @Override
            public SqlExprToRexConverterFactory getSqlExprToRexConverterFactory() {
                return new SqlExprToRexConverterFactory() {

                    @Override
                    public SqlExprToRexConverter create(RelDataType relDataType) {
                        return new PlannerContext(
                                tableEnv.getConfig(),
                                new FunctionCatalog(tableEnv.getConfig(), tableEnv.getCatalogManager(),
                                        new ModuleManager()),
                                tableEnv.getCatalogManager(),
                                asRootSchema(new CatalogManagerCalciteSchema(tableEnv.getCatalogManager(), true)),
                                new ArrayList<>()).createSqlExprToRexConverter(relDataType);
                    }
                };
            }

            @Override
            public <C> C unwrap(Class<C> clazz) {
                if(clazz.isInterface()){
                    return clazz.cast(this);
                }else{
                    return null;
                }
            }

            @Override
            public RexBuilder getRexBuilder() {
                return getPlanner().getRelBuilder().getRexBuilder();
            }

            @Override
            public boolean needFinalTimeIndicatorConversion() {
                return true;
            }

            @Override
            public boolean isUpdateBeforeRequired() {
                return false;
            }

            @Override
            public MiniBatchInterval getMiniBatchInterval() {
                return MiniBatchInterval.NONE();
            }

            private PlannerBase getPlanner() {
                return (PlannerBase) tableEnv.getPlanner();
            }

        });
    }

    /**
     * Check the size of query and sink fields match
     */
    private void validateSchema(String sinkTable, RelNode relNode, List<String> sinkFieldList) {
        List<String> queryFieldList = relNode.getRowType().getFieldNames();
        if (queryFieldList.size() != sinkFieldList.size()) {
            throw new ValidationException(
                    String.format(
                            "Column types of query result and sink for %s do not match.\n"
                                    + "Query schema: %s\n"
                                    + "Sink schema:  %s",
                            sinkTable, queryFieldList, sinkFieldList));
        }
    }

    private List<LineageRel> buildFiledLineageResult(String sinkTable, RelNode optRelNode) {
        // target columns
        List<String> targetColumnList = tableEnv.from(sinkTable).getSchema().getTableColumns()
                .stream().map(tableColumn -> tableColumn.getName()).collect(Collectors.toList());

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
                    List<String> fieldNames = table.getRowType().getFieldNames();
                    String sourceColumn = fieldNames.get(ordinal);

                    // add record
                    resultList.add(LineageRel.build(sourceTable, sourceColumn, sinkTable, targetColumn));
                }
            }
        }
        return resultList;
    }
}
