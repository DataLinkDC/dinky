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

package org.dinky.job.runner;

import org.dinky.classloader.DinkyClassLoader;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.job.SqlType;
import org.dinky.executor.Executor;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManager;
import org.dinky.trans.Operations;
import org.dinky.trans.ddl.CustomSetOperation;
import org.dinky.trans.dml.ExecuteJarOperation;
import org.dinky.trans.parse.AddFileSqlParseStrategy;
import org.dinky.trans.parse.AddJarSqlParseStrategy;
import org.dinky.trans.parse.ExecuteJarParseStrategy;
import org.dinky.trans.parse.SetSqlParseStrategy;
import org.dinky.utils.DinkyClassLoaderUtil;
import org.dinky.utils.FlinkStreamEnvironmentUtil;
import org.dinky.utils.JsonUtils;
import org.dinky.utils.SqlUtil;

import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.jsonplan.JsonPlanGenerator;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ObjectNode;

import cn.hutool.core.lang.Assert;

public class FlinkJarUtil {
    public static ObjectNode getJobPlan(String statement, JobManager jobManager) {
        try {
            Pipeline pipeline = getJarStreamGraph(statement, jobManager.getDinkyClassLoader(), jobManager);
            Configuration configuration =
                    Configuration.fromMap(jobManager.getExecutorConfig().getConfig());
            JobGraph jobGraph = FlinkStreamEnvironmentUtil.getJobGraph(pipeline, configuration);
            return JsonUtils.parseObject(JsonPlanGenerator.generatePlan(jobGraph));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Pipeline getJarStreamGraph(
            String statement, DinkyClassLoader dinkyClassLoader, JobManager jobManager) {
        Executor executor = jobManager.getExecutor();
        JobConfig config = jobManager.getConfig();

        DinkyClassLoaderUtil.initClassLoader(config, dinkyClassLoader);
        String[] statements = SqlUtil.getStatements(statement);
        ExecuteJarOperation executeJarOperation = null;
        for (String sql : statements) {
            String sqlStatement = executor.pretreatStatement(sql);
            if (ExecuteJarParseStrategy.INSTANCE.match(sqlStatement)) {
                executeJarOperation = new ExecuteJarOperation(sqlStatement);
                break;
            }
            SqlType operationType = Operations.getOperationType(sqlStatement);
            if (operationType.equals(SqlType.SET) && SetSqlParseStrategy.INSTANCE.match(sqlStatement)) {
                CustomSetOperation customSetOperation = new CustomSetOperation(sqlStatement);
                customSetOperation.execute(executor.getCustomTableEnvironment());
            } else if (operationType.equals(SqlType.ADD)) {
                Set<File> files = AddJarSqlParseStrategy.getAllFilePath(sqlStatement);
                files.forEach(executor::addJar);
                files.forEach(jobManager.getUdfPathContextHolder()::addOtherPlugins);
            } else if (operationType.equals(SqlType.ADD_FILE)) {
                Set<File> files = AddFileSqlParseStrategy.getAllFilePath(sqlStatement);
                files.forEach(executor::addJar);
                files.forEach(jobManager.getUdfPathContextHolder()::addFile);
            }
        }
        Assert.notNull(executeJarOperation, () -> new DinkyException("Not found execute jar operation."));
        List<URL> urLs = jobManager.getAllFileSet();
        return executeJarOperation.explain(executor.getCustomTableEnvironment(), urLs);
    }
}
