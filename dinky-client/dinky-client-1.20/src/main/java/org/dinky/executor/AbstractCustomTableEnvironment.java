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

package org.dinky.executor;

import org.dinky.data.model.LineageRel;
import org.dinky.data.result.SqlExplainResult;
import org.dinky.utils.LineageContext;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.ExplainFormat;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.TableException;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.bridge.java.internal.StreamTableEnvironmentImpl;
import org.apache.flink.table.delegation.Planner;
import org.apache.flink.table.operations.ExplainOperation;
import org.apache.flink.table.operations.ModifyOperation;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.QueryOperation;

import java.util.List;

import cn.hutool.core.util.ReflectUtil;

/** */
public abstract class AbstractCustomTableEnvironment
        implements CustomTableEnvironment, DefaultTableEnvironmentInternal, DefaultStreamTableEnvironment {

    protected StreamTableEnvironment streamTableEnvironment;
    protected ClassLoader userClassLoader;

    protected AbstractCustomTableEnvironment() {}

    protected AbstractCustomTableEnvironment(StreamTableEnvironment streamTableEnvironment) {
        this.streamTableEnvironment = streamTableEnvironment;
    }

    @Override
    public TableEnvironment getTableEnvironment() {
        return streamTableEnvironment;
    }

    public StreamExecutionEnvironment getStreamExecutionEnvironment() {
        return ((StreamTableEnvironmentImpl) streamTableEnvironment).execEnv();
    }

    @Override
    public ClassLoader getUserClassLoader() {
        return userClassLoader;
    }

    public Planner getPlanner() {
        return ((StreamTableEnvironmentImpl) streamTableEnvironment).getPlanner();
    }

    @Override
    public void injectParser(CustomParser parser) {
        ReflectUtil.setFieldValue(getPlanner(), "parser", new ParserWrapper(parser));
    }

    @Override
    public void injectExtendedExecutor(CustomExtendedOperationExecutor extendedExecutor) {}

    @Override
    public Configuration getRootConfiguration() {
        return (Configuration) this.getConfig().getRootConfiguration();
    }

    @Override
    public SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails) {
        List<Operation> operations = getParser().parse(statement);
        if (operations.size() != 1) {
            throw new TableException("Unsupported SQL query! explainSql() only accepts a single SQL query.");
        }

        Operation operation = operations.get(0);
        SqlExplainResult data = new SqlExplainResult();
        data.setParseTrue(true);
        data.setExplainTrue(true);

        if (operation instanceof ModifyOperation) {
            data.setType("Modify DML");
        } else if (operation instanceof ExplainOperation) {
            data.setType("Explain DML");
        } else if (operation instanceof QueryOperation) {
            data.setType("Query DML");
        } else {
            data.setExplain(operation.asSummaryString());
            data.setType("DDL");

            // data.setExplain("DDL statement needn't comment。");
            return data;
        }

        data.setExplain(getPlanner().explain(operations, ExplainFormat.TEXT, extraDetails));
        return data;
    }

    @Override
    public List<LineageRel> getLineage(String statement) {
        LineageContext lineageContext = new LineageContext(this);
        return lineageContext.analyzeLineage(statement);
    }
}
