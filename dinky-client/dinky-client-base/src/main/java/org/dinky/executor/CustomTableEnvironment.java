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

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.ExplainDetail;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.api.internal.TableEnvironmentInternal;
import org.apache.flink.table.delegation.Planner;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.URLUtil;

/**
 * CustomTableEnvironment
 *
 * @since 2022/2/5 10:35
 */
public interface CustomTableEnvironment
        extends StreamTableEnvironment, TableEnvironmentInternal, TableEnvironmentInstance {

    ObjectNode getStreamGraph(String statement);

    JobPlanInfo getJobPlanInfo(List<String> statements);

    StreamGraph getStreamGraphFromInserts(List<String> statements);

    SqlExplainResult explainSqlRecord(String statement, ExplainDetail... extraDetails);

    StreamExecutionEnvironment getStreamExecutionEnvironment();

    Planner getPlanner();

    ClassLoader getUserClassLoader();

    Configuration getRootConfiguration();

    default JobGraph getJobGraphFromInserts(List<String> statements) {
        return getStreamGraphFromInserts(statements).getJobGraph();
    }

    default List<LineageRel> getLineage(String statement) {
        return Collections.emptyList();
    }

    default void addJar(File... jarPath) {
        Configuration configuration = this.getRootConfiguration();
        List<String> pathList =
                Arrays.stream(URLUtil.getURLs(jarPath)).map(URL::toString).collect(Collectors.toList());
        List<String> jars = configuration.get(PipelineOptions.JARS);
        if (jars == null) {
            configuration.set(PipelineOptions.JARS, pathList);
        } else {
            CollUtil.addAll(jars, pathList);
        }
    }

    default <T> void addConfiguration(ConfigOption<T> option, T value) {
        Configuration configuration =
                (Configuration) getStreamExecutionEnvironment().getConfiguration();
        configuration.set(option, value);
    }
}
