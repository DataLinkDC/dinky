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

package org.dinky.utils;

import static org.apache.flink.util.Preconditions.checkNotNull;

import org.apache.flink.api.common.Plan;
import org.apache.flink.api.dag.Pipeline;
import org.apache.flink.client.PlanTranslator;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.core.execution.JobListener;
import org.apache.flink.core.execution.PipelineExecutorFactory;
import org.apache.flink.core.execution.PipelineExecutorServiceLoader;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import cn.hutool.core.util.ReflectUtil;

public enum FlinkStreamEnvironmentUtil {
    ;

    public static JobClient executeAsync(Pipeline pipeline, StreamExecutionEnvironment env) throws Exception {
        Configuration configuration = new Configuration((Configuration) env.getConfiguration());
        checkNotNull(pipeline, "pipeline cannot be null.");
        checkNotNull(
                configuration.get(DeploymentOptions.TARGET),
                "No execution.target specified in your configuration file.");

        PipelineExecutorServiceLoader executorServiceLoader =
                (PipelineExecutorServiceLoader) ReflectUtil.getFieldValue(env, "executorServiceLoader");
        ClassLoader userClassloader = (ClassLoader) ReflectUtil.getFieldValue(env, "userClassloader");
        final PipelineExecutorFactory executorFactory = executorServiceLoader.getExecutorFactory(configuration);

        checkNotNull(
                executorFactory,
                "Cannot find compatible factory for specified execution.target (=%s)",
                configuration.get(DeploymentOptions.TARGET));

        CompletableFuture<JobClient> jobClientFuture =
                executorFactory.getExecutor(configuration).execute(pipeline, configuration, userClassloader);

        List<JobListener> jobListeners = env.getJobListeners();
        try {
            JobClient jobClient = jobClientFuture.get();
            jobListeners.forEach(jobListener -> jobListener.onJobSubmitted(jobClient, null));
            return jobClient;
        } catch (ExecutionException executionException) {
            final Throwable strippedException = ExceptionUtils.stripExecutionException(executionException);
            jobListeners.forEach(jobListener -> jobListener.onJobSubmitted(null, strippedException));

            throw new FlinkException(
                    String.format("Failed to execute job '%s'.", ReflectUtil.invoke(pipeline, "getJobName")),
                    strippedException);
        }
    }

    public static String getStreamingPlanAsJSON(Pipeline pipeline) {
        if (pipeline instanceof StreamGraph) {
            return ((StreamGraph) pipeline).getStreamingPlanAsJSON();
        } else if (pipeline instanceof Plan) {
            ;
            PlanTranslator planTranslator = new PlanTranslator();
            return planTranslator.translateToJSONExecutionPlan(pipeline);
        } else {
            throw new IllegalArgumentException("Unknown pipeline type: " + pipeline);
        }
    }

    public static JobGraph getJobGraph(Pipeline pipeline, Configuration configuration) {
        if (pipeline instanceof StreamGraph) {
            return ((StreamGraph) pipeline).getJobGraph();
        } else if (pipeline instanceof Plan) {
            ;
            Plan plan = ((Plan) pipeline);
            PlanTranslator planTranslator = new PlanTranslator();
            return planTranslator.translateToJobGraph(pipeline, configuration, plan.getDefaultParallelism());
        } else {
            throw new IllegalArgumentException("Unknown pipeline type: " + pipeline);
        }
    }
}
