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

package org.dinky.trans.dml;

import org.dinky.executor.CustomTableResultImpl;
import org.dinky.executor.Executor;
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.parse.ExecuteJarParseStrategy;
import org.dinky.utils.RunTimeUtil;

import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.client.StreamGraphTranslator;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableResult;

import java.io.File;
import java.util.Optional;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

public class ExecuteJarOperation extends AbstractOperation implements DmlOperation {

    public ExecuteJarOperation(String statement) {
        super(statement);
    }

    @Override
    public Optional<? extends TableResult> execute(Executor executor) {
        try {
            executor.getStreamExecutionEnvironment().execute(getStreamGraph(executor));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.of(CustomTableResultImpl.TABLE_RESULT_OK);
    }

    protected StreamGraph getStreamGraph(Executor executor) {
        JarSubmitParam submitParam = JarSubmitParam.build(statement);
        SavepointRestoreSettings savepointRestoreSettings = StrUtil.isBlank(submitParam.getSavepointPath())
                ? SavepointRestoreSettings.none()
                : SavepointRestoreSettings.forPath(
                        submitParam.getSavepointPath(), submitParam.getAllowNonRestoredState());
        PackagedProgram program;
        try {
            Configuration configuration = executor.getTableConfig().getConfiguration();
            File file = FileUtil.file(submitParam.getUri());
            program = PackagedProgram.newBuilder()
                    .setJarFile(file)
                    .setEntryPointClassName(submitParam.getMainClass())
                    .setConfiguration(configuration)
                    .setSavepointRestoreSettings(savepointRestoreSettings)
                    .setArguments(RunTimeUtil.handleCmds(submitParam.getArgs()))
                    .build();
            executor.addJar(file);
            Pipeline pipeline = PackagedProgramUtils.getPipelineFromProgram(program, configuration, 1, true);
            StreamGraphTranslator streamGraphTranslator = new StreamGraphTranslator(program.getUserCodeClassLoader());
            boolean canTranslate = streamGraphTranslator.canTranslate(pipeline);
            Assert.isTrue(canTranslate, "can not translate");
            return (StreamGraph) pipeline;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asSummaryString() {
        return statement;
    }

    @Override
    public Optional<? extends TableResult> explain(Executor executor) {
        getStreamGraph(executor);
        return Optional.of(CustomTableResultImpl.TABLE_RESULT_OK);
    }

    @Setter
    @Getter
    public static class JarSubmitParam {
        protected JarSubmitParam() {}

        private String uri;
        private String mainClass;
        private String args;
        private String parallelism;
        private String savepointPath;
        private Boolean allowNonRestoredState = SavepointConfigOptions.SAVEPOINT_IGNORE_UNCLAIMED_STATE.defaultValue();

        public static JarSubmitParam build(String statement) {
            JarSubmitParam submitParam = ExecuteJarParseStrategy.getInfo(statement);
            Assert.notBlank(submitParam.getUri());
            Assert.notBlank(submitParam.getMainClass());
            return submitParam;
        }
    }
}
