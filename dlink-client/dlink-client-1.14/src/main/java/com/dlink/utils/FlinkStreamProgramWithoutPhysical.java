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

import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.hep.HepMatchOrder;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.api.config.OptimizerConfigOptions;
import org.apache.flink.table.planner.plan.nodes.FlinkConventions;
import org.apache.flink.table.planner.plan.optimize.program.FlinkChainedProgram;
import org.apache.flink.table.planner.plan.optimize.program.FlinkDecorrelateProgram;
import org.apache.flink.table.planner.plan.optimize.program.FlinkGroupProgramBuilder;
import org.apache.flink.table.planner.plan.optimize.program.FlinkHepRuleSetProgramBuilder;
import org.apache.flink.table.planner.plan.optimize.program.FlinkVolcanoProgramBuilder;
import org.apache.flink.table.planner.plan.optimize.program.HEP_RULES_EXECUTION_TYPE;
import org.apache.flink.table.planner.plan.rules.FlinkStreamRuleSets;

/**
 * FlinkStreamProgramWithoutPhysical
 *
 * @author wenmo
 * @since 2022/8/20 23:33
 */
public class FlinkStreamProgramWithoutPhysical {

    private static final String SUBQUERY_REWRITE = "subquery_rewrite";
    private static final String TEMPORAL_JOIN_REWRITE = "temporal_join_rewrite";
    private static final String DECORRELATE = "decorrelate";
    private static final String DEFAULT_REWRITE = "default_rewrite";
    private static final String PREDICATE_PUSHDOWN = "predicate_pushdown";
    private static final String JOIN_REORDER = "join_reorder";
    private static final String PROJECT_REWRITE = "project_rewrite";
    private static final String LOGICAL = "logical";
    private static final String LOGICAL_REWRITE = "logical_rewrite";

    public static FlinkChainedProgram buildProgram(Configuration config) {
        FlinkChainedProgram chainedProgram = new FlinkChainedProgram();

        // rewrite sub-queries to joins
        chainedProgram.addLast(
                SUBQUERY_REWRITE,
                FlinkGroupProgramBuilder.newBuilder()
                        // rewrite QueryOperationCatalogViewTable before rewriting sub-queries
                        .addProgram(FlinkHepRuleSetProgramBuilder.newBuilder()
                                .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_SEQUENCE())
                                .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                                .add(FlinkStreamRuleSets.TABLE_REF_RULES())
                                .build(), "convert table references before rewriting sub-queries to semi-join")
                        .addProgram(FlinkHepRuleSetProgramBuilder.newBuilder()
                                .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_SEQUENCE())
                                .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                                .add(FlinkStreamRuleSets.SEMI_JOIN_RULES())
                                .build(), "rewrite sub-queries to semi-join")
                        .addProgram(FlinkHepRuleSetProgramBuilder.newBuilder()
                                .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_COLLECTION())
                                .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                                .add(FlinkStreamRuleSets.TABLE_SUBQUERY_RULES())
                                .build(), "sub-queries remove")
                        // convert RelOptTableImpl (which exists in SubQuery before) to FlinkRelOptTable
                        .addProgram(FlinkHepRuleSetProgramBuilder.newBuilder()
                                .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_SEQUENCE())
                                .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                                .add(FlinkStreamRuleSets.TABLE_REF_RULES())
                                .build(), "convert table references after sub-queries removed")
                        .build());

        // rewrite special temporal join plan
        chainedProgram.addLast(
                TEMPORAL_JOIN_REWRITE,
                FlinkGroupProgramBuilder.newBuilder()
                        .addProgram(
                                FlinkHepRuleSetProgramBuilder.newBuilder()
                                        .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_SEQUENCE())
                                        .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                                        .add(FlinkStreamRuleSets.EXPAND_PLAN_RULES())
                                        .build(),
                                "convert correlate to temporal table join")
                        .addProgram(
                                FlinkHepRuleSetProgramBuilder.newBuilder()
                                        .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_SEQUENCE())
                                        .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                                        .add(FlinkStreamRuleSets.POST_EXPAND_CLEAN_UP_RULES())
                                        .build(),
                                "convert enumerable table scan")
                        .build());

        // query decorrelation
        chainedProgram.addLast(DECORRELATE,
                FlinkGroupProgramBuilder.newBuilder()
                        // rewrite before decorrelation
                        .addProgram(
                                FlinkHepRuleSetProgramBuilder.newBuilder()
                                        .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_SEQUENCE())
                                        .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                                        .add(FlinkStreamRuleSets.PRE_DECORRELATION_RULES())
                                        .build(),
                                "pre-rewrite before decorrelation")
                        .addProgram(new FlinkDecorrelateProgram(), "")
                        .build());

        // default rewrite, includes: predicate simplification, expression reduction, window
        // properties rewrite, etc.
        chainedProgram.addLast(
                DEFAULT_REWRITE,
                FlinkHepRuleSetProgramBuilder.newBuilder()
                        .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_SEQUENCE())
                        .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                        .add(FlinkStreamRuleSets.DEFAULT_REWRITE_RULES())
                        .build());

        // rule based optimization: push down predicate(s) in where clause, so it only needs to read
        // the required data
        chainedProgram.addLast(
                PREDICATE_PUSHDOWN,
                FlinkGroupProgramBuilder.newBuilder()
                        .addProgram(
                                FlinkHepRuleSetProgramBuilder.newBuilder()
                                        .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_COLLECTION())
                                        .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                                        .add(FlinkStreamRuleSets.FILTER_PREPARE_RULES())
                                        .build(),
                                "filter rules")
                        .addProgram(
                                FlinkHepRuleSetProgramBuilder.newBuilder()
                                        .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_SEQUENCE())
                                        .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                                        .add(FlinkStreamRuleSets.FILTER_TABLESCAN_PUSHDOWN_RULES())
                                        .build(),
                                "push predicate into table scan")
                        .addProgram(
                                FlinkHepRuleSetProgramBuilder.newBuilder()
                                        .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_SEQUENCE())
                                        .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                                        .add(FlinkStreamRuleSets.PRUNE_EMPTY_RULES())
                                        .build(),
                                "prune empty after predicate push down")
                        .build());

        // join reorder
        if (config.getBoolean(OptimizerConfigOptions.TABLE_OPTIMIZER_JOIN_REORDER_ENABLED)) {
            chainedProgram.addLast(
                    JOIN_REORDER,
                    FlinkGroupProgramBuilder.newBuilder()
                            .addProgram(FlinkHepRuleSetProgramBuilder.newBuilder()
                                    .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_COLLECTION())
                                    .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                                    .add(FlinkStreamRuleSets.JOIN_REORDER_PREPARE_RULES())
                                    .build(), "merge join into MultiJoin")
                            .addProgram(FlinkHepRuleSetProgramBuilder.newBuilder()
                                    .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_SEQUENCE())
                                    .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                                    .add(FlinkStreamRuleSets.JOIN_REORDER_RULES())
                                    .build(), "do join reorder")
                            .build());
        }

        // project rewrite
        chainedProgram.addLast(
                PROJECT_REWRITE,
                FlinkHepRuleSetProgramBuilder.newBuilder()
                        .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_COLLECTION())
                        .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                        .add(FlinkStreamRuleSets.PROJECT_RULES())
                        .build());

        // optimize the logical plan
        chainedProgram.addLast(
                LOGICAL,
                FlinkVolcanoProgramBuilder.newBuilder()
                        .add(FlinkStreamRuleSets.LOGICAL_OPT_RULES())
                        .setRequiredOutputTraits(new Convention.Impl[]{
                                FlinkConventions.LOGICAL()
                        })
                        .build());

        // logical rewrite
        chainedProgram.addLast(
                LOGICAL_REWRITE,
                FlinkHepRuleSetProgramBuilder.newBuilder()
                        .setHepRulesExecutionType(HEP_RULES_EXECUTION_TYPE.RULE_SEQUENCE())
                        .setHepMatchOrder(HepMatchOrder.BOTTOM_UP)
                        .add(FlinkStreamRuleSets.LOGICAL_REWRITE())
                        .build());

        return chainedProgram;
    }

}
