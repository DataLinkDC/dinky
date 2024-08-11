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

package org.dinky.job.builder;

import org.dinky.data.exception.DinkyException;
import org.dinky.executor.Executor;
import org.dinky.job.JobBuilder;
import org.dinky.job.JobConfig;
import org.dinky.job.JobManagerHandler;
import org.dinky.parser.SqlType;
import org.dinky.trans.Operations;
import org.dinky.trans.ddl.CustomSetOperation;
import org.dinky.trans.dml.ExecuteJarOperation;
import org.dinky.trans.parse.AddFileSqlParseStrategy;
import org.dinky.trans.parse.AddJarSqlParseStrategy;
import org.dinky.trans.parse.ExecuteJarParseStrategy;
import org.dinky.trans.parse.SetSqlParseStrategy;
import org.dinky.utils.DinkyClassLoaderUtil;
import org.dinky.utils.SqlUtil;

import org.apache.flink.api.dag.Pipeline;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.hutool.core.lang.Assert;

/**
 * JobJarStreamGraphBuilder
 */
public class JobJarStreamGraphBuilder implements JobBuilder {

    private final JobConfig config;
    private final Executor executor;

    public JobJarStreamGraphBuilder(JobConfig config, Executor executor) {
        this.config = config;
        this.executor = executor;
    }

    public static JobJarStreamGraphBuilder build(JobManagerHandler jobManager) {
        return new JobJarStreamGraphBuilder(jobManager.getConfig(), jobManager.getExecutor());
    }

    @Override
    public void run() throws Exception {}

    public Pipeline getJarStreamGraph(String statement) {
        DinkyClassLoaderUtil.initClassLoader(config, executor.getDinkyClassLoader());
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
                customSetOperation.execute(this.executor.getCustomTableEnvironment());
            } else if (operationType.equals(SqlType.ADD)) {
                Set<File> files = AddJarSqlParseStrategy.getAllFilePath(sqlStatement);
                executor.addJar(files.toArray(new File[0]));
                files.forEach(executor.getUdfPathContextHolder()::addOtherPlugins);
            } else if (operationType.equals(SqlType.ADD_FILE)) {
                Set<File> files = AddFileSqlParseStrategy.getAllFilePath(sqlStatement);
                executor.addJar(files.toArray(new File[0]));
                files.forEach(executor.getUdfPathContextHolder()::addFile);
            }
        }
        Assert.notNull(executeJarOperation, () -> new DinkyException("Not found execute jar operation."));
        List<URL> urLs = executor.getAllFileSet();
        return executeJarOperation.explain(executor.getCustomTableEnvironment(), urLs);
    }

    public List<String> getUris(String statement) {
        String[] statements = SqlUtil.getStatements(statement);
        List<String> uriList = new ArrayList<>();
        for (String sql : statements) {
            String sqlStatement = executor.pretreatStatement(sql);
            if (ExecuteJarParseStrategy.INSTANCE.match(sqlStatement)) {
                uriList.add(ExecuteJarParseStrategy.getInfo(statement).getUri());
                break;
            }
        }

        return uriList;
    }
}
