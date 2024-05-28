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
import org.dinky.utils.FlinkStreamEnvironmentUtil;
import org.dinky.utils.URLUtils;

import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.TableResult;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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
            FlinkStreamEnvironmentUtil.executeAsync(getStreamGraph(tEnv), streamExecutionEnvironment);
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
                    .setArguments(extractArgs(submitParam.getArgs().trim()).toArray(new String[0]))
                    .setUserClassPaths(classpaths)
                    .build();
            int parallelism = StrUtil.isNumeric(submitParam.getParallelism())
                    ? Convert.toInt(submitParam.getParallelism())
                    : tEnv.getStreamExecutionEnvironment().getParallelism();
            Pipeline pipeline = PackagedProgramUtils.getPipelineFromProgram(program, configuration, parallelism, true);
            program.close();
            return pipeline;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> extractArgs(String args) {
        List<String> programArgs = new ArrayList<>();
        if (StrUtil.isNotEmpty(args)) {
            String[] array = args.split("\\s+");
            Iterator<String> iter = Arrays.asList(array).iterator();
            while (iter.hasNext()) {
                String v = iter.next();
                String p = v.substring(0, 1);
                if (p.equals("'") || p.equals("\"")) {
                    String value = v;
                    if (!v.endsWith(p)) {
                        while (!value.endsWith(p) && iter.hasNext()) {
                            value += " " + iter.next();
                        }
                    }
                    programArgs.add(value.substring(1, value.length() - 1));
                } else {
                    programArgs.add(v);
                }
            }
        }
        return programArgs;
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
