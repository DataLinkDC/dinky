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

import static org.dinky.utils.RunTimeUtil.extractArgs;

import org.dinky.config.Dialect;
import org.dinky.context.TaskContextHolder;
import org.dinky.executor.CustomTableEnvironment;
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.ExtendOperation;
import org.dinky.trans.parse.ExecuteJarParseStrategy;
import org.dinky.utils.FlinkStreamEnvironmentUtil;
import org.dinky.utils.URLUtils;

import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableResult;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import cn.hutool.core.convert.Convert;
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
            StreamExecutionEnvironment streamExecutionEnvironment = tEnv.getStreamExecutionEnvironment();
            if (TaskContextHolder.getDialect().equals(Dialect.FLINK_JAR)) {
                FlinkStreamEnvironmentUtil.executeAsync(getStreamGraph(tEnv), streamExecutionEnvironment);
            } else {
                throw new RuntimeException("Please perform Execute jar syntax in the FlinkJar task !");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Optional.of(TABLE_RESULT_OK);
    }

    public Pipeline getStreamGraph(CustomTableEnvironment tEnv) {
        return getStreamGraph(tEnv, Collections.emptyList());
    }

    public Pipeline getStreamGraph(CustomTableEnvironment tEnv, List<URL> classpaths) {
        JarSubmitParam submitParam = JarSubmitParam.build(statement);
        return getStreamGraph(submitParam, tEnv, classpaths);
    }

    public static Pipeline getStreamGraph(
            JarSubmitParam submitParam, CustomTableEnvironment tEnv, List<URL> classpaths) {
        SavepointRestoreSettings savepointRestoreSettings = StrUtil.isBlank(submitParam.getSavepointPath())
                ? SavepointRestoreSettings.none()
                : SavepointRestoreSettings.forPath(
                        submitParam.getSavepointPath(), submitParam.getAllowNonRestoredState());
        PackagedProgram program;
        try {
            Configuration configuration = tEnv.getRootConfiguration();
            File file =
                    Opt.ofBlankAble(submitParam.getUri()).map(URLUtils::toFile).orElse(null);
            String submitArgs = Opt.ofBlankAble(submitParam.getArgs()).orElse("");
            if (!PackagedProgramUtils.isPython(submitParam.getMainClass())) {
                tEnv.addJar(file);
            } else {
                // python submit
                submitParam.setArgs("--python " + file.getAbsolutePath() + " " + submitArgs);
                file = null;
            }

            program = PackagedProgram.newBuilder()
                    .setJarFile(file)
                    .setEntryPointClassName(submitParam.getMainClass())
                    .setConfiguration(configuration)
                    .setSavepointRestoreSettings(savepointRestoreSettings)
                    .setArguments(extractArgs(submitArgs.trim()).toArray(new String[0]))
                    .setUserClassPaths(classpaths)
                    .build();
            int parallelism = StrUtil.isNumeric(submitParam.getParallelism())
                    ? Convert.toInt(submitParam.getParallelism())
                    : tEnv.getStreamExecutionEnvironment().getParallelism();
            Pipeline pipeline = PackagedProgramUtils.getPipelineFromProgram(program, configuration, parallelism, true);

            // When the UserCodeClassLoader is used to obtain the JobGraph in advance,
            // the code generated by the StreamGraph is compiled.
            // When the JobGraph is obtained again in the future,
            // the already compiled code is used directly to prevent the DinkyClassLoader from failing to compile the
            // code of the user Jar.
            if (pipeline instanceof StreamGraph) {
                ClassLoader dinkyClassLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(program.getUserCodeClassLoader());
                ((StreamGraph) pipeline).getJobGraph();
                Thread.currentThread().setContextClassLoader(dinkyClassLoader);
            }

            program.close();
            return pipeline;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String asSummaryString() {
        return statement;
    }

    public Pipeline explain(CustomTableEnvironment tEnv) {
        return getStreamGraph(tEnv);
    }

    public Pipeline explain(CustomTableEnvironment tEnv, List<URL> classpaths) {
        return getStreamGraph(tEnv, classpaths);
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
            return submitParam;
        }
    }
}
