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

import org.dinky.executor.CustomTableEnvironment;
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.ExtendOperation;
import org.dinky.trans.parse.ExecuteJarParseStrategy;
import org.dinky.utils.RunTimeUtil;
import org.dinky.utils.URLUtils;

import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableResult;

import java.io.File;
import java.util.Optional;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

public class ExecuteJarOperation extends AbstractOperation implements ExtendOperation {

    public ExecuteJarOperation(String statement) {
        super(statement);
    }

    @Override
    public Optional<? extends TableResult> execute(CustomTableEnvironment tEnv) {
        try {
            tEnv.getStreamExecutionEnvironment().execute(getStreamGraph(tEnv));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.of(TABLE_RESULT_OK);
    }

    public StreamGraph getStreamGraph(CustomTableEnvironment tEnv) {
        JarSubmitParam submitParam = JarSubmitParam.build(statement);
        return getStreamGraph(submitParam, tEnv);
    }

    public static StreamGraph getStreamGraph(JarSubmitParam submitParam, CustomTableEnvironment tEnv) {
        SavepointRestoreSettings savepointRestoreSettings = StrUtil.isBlank(submitParam.getSavepointPath())
                ? SavepointRestoreSettings.none()
                : SavepointRestoreSettings.forPath(
                        submitParam.getSavepointPath(), submitParam.getAllowNonRestoredState());
        PackagedProgram program;
        try {
            Configuration configuration = tEnv.getConfig().getConfiguration();
            File file =
                    Opt.ofBlankAble(submitParam.getUri()).map(URLUtils::toFile).orElse(null);
            if (!PackagedProgramUtils.isPython(submitParam.getMainClass())) {
                tEnv.addJar(file);
            } else {
                // python submit
                submitParam.setArgs("--python " + file.getAbsolutePath() + " "
                        + Opt.ofBlankAble(submitParam.getArgs()).orElse(""));
                file = null;
            }
            program = PackagedProgram.newBuilder()
                    .setJarFile(file)
                    .setEntryPointClassName(submitParam.getMainClass())
                    .setConfiguration(configuration)
                    .setSavepointRestoreSettings(savepointRestoreSettings)
                    .setArguments(RunTimeUtil.handleCmds(submitParam.getArgs()))
                    .build();
            Pipeline pipeline = PackagedProgramUtils.getPipelineFromProgram(program, configuration, 1, true);
            Assert.isTrue(pipeline instanceof StreamGraph, "can not translate");
            return (StreamGraph) pipeline;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asSummaryString() {
        return statement;
    }

    public StreamGraph explain(CustomTableEnvironment tEnv) {
        return getStreamGraph(tEnv);
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
