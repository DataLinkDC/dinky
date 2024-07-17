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

package org.dinky.trans.pipeline;

import org.dinky.executor.Executor;

import org.apache.flink.cdc.common.configuration.Configuration;
import org.apache.flink.cdc.common.event.Event;
import org.apache.flink.cdc.common.factories.DataSinkFactory;
import org.apache.flink.cdc.common.factories.FactoryHelper;
import org.apache.flink.cdc.common.pipeline.PipelineOptions;
import org.apache.flink.cdc.common.sink.DataSink;
import org.apache.flink.cdc.composer.PipelineComposer;
import org.apache.flink.cdc.composer.PipelineExecution;
import org.apache.flink.cdc.composer.definition.PipelineDef;
import org.apache.flink.cdc.composer.definition.SinkDef;
import org.apache.flink.cdc.composer.flink.FlinkEnvironmentUtils;
import org.apache.flink.cdc.composer.flink.coordination.OperatorIDGenerator;
import org.apache.flink.cdc.composer.flink.translator.DataSinkTranslator;
import org.apache.flink.cdc.composer.flink.translator.DataSourceTranslator;
import org.apache.flink.cdc.composer.flink.translator.PartitioningTranslator;
import org.apache.flink.cdc.composer.flink.translator.SchemaOperatorTranslator;
import org.apache.flink.cdc.composer.flink.translator.TransformTranslator;
import org.apache.flink.cdc.composer.utils.FactoryDiscoveryUtils;
import org.apache.flink.cdc.runtime.serializer.event.EventSerializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * DinkyFlinkPipelineComposer
 *
 * @author wenmo
 * @since 2023/12/22 0:16
 */
public class DinkyFlinkPipelineComposer implements PipelineComposer {

    private final StreamExecutionEnvironment env;

    public static DinkyFlinkPipelineComposer of(Executor executor) {

        return new DinkyFlinkPipelineComposer(executor.getStreamExecutionEnvironment());
    }

    private DinkyFlinkPipelineComposer(StreamExecutionEnvironment env) {
        this.env = env;
    }

    public PipelineExecution compose(PipelineDef pipelineDef) {
        int parallelism = pipelineDef.getConfig().get(PipelineOptions.PIPELINE_PARALLELISM);
        env.getConfig().setParallelism(parallelism);

        // Build Source Operator
        DataSourceTranslator sourceTranslator = new DataSourceTranslator();
        DataStream<Event> stream = sourceTranslator.translate(pipelineDef.getSource(), env, pipelineDef.getConfig());

        // Build TransformSchemaOperator for processing Schema Event
        TransformTranslator transformTranslator = new TransformTranslator();
        stream = transformTranslator.translateSchema(stream, pipelineDef.getTransforms());

        // Schema operator
        SchemaOperatorTranslator schemaOperatorTranslator = new SchemaOperatorTranslator(
                pipelineDef.getConfig().get(PipelineOptions.PIPELINE_SCHEMA_CHANGE_BEHAVIOR),
                pipelineDef.getConfig().get(PipelineOptions.PIPELINE_SCHEMA_OPERATOR_UID),
                pipelineDef.getConfig().get(PipelineOptions.PIPELINE_SCHEMA_OPERATOR_RPC_TIMEOUT));
        OperatorIDGenerator schemaOperatorIDGenerator =
                new OperatorIDGenerator(schemaOperatorTranslator.getSchemaOperatorUid());

        // Build TransformDataOperator for processing Data Event
        stream = transformTranslator.translateData(
                stream,
                pipelineDef.getTransforms(),
                schemaOperatorIDGenerator.generate(),
                pipelineDef.getConfig().get(PipelineOptions.PIPELINE_LOCAL_TIME_ZONE));

        // Build DataSink in advance as schema operator requires MetadataApplier
        DataSink dataSink = createDataSink(pipelineDef.getSink(), pipelineDef.getConfig());

        stream = schemaOperatorTranslator.translate(
                stream, parallelism, dataSink.getMetadataApplier(), pipelineDef.getRoute());

        // Build Partitioner used to shuffle Event
        PartitioningTranslator partitioningTranslator = new PartitioningTranslator();
        stream = partitioningTranslator.translate(
                stream, parallelism, parallelism, schemaOperatorIDGenerator.generate());

        // Build Sink Operator
        DataSinkTranslator sinkTranslator = new DataSinkTranslator();
        sinkTranslator.translate(pipelineDef.getSink(), stream, dataSink, schemaOperatorIDGenerator.generate());

        // Add framework JARs
        addFrameworkJars();

        return new DinkyFlinkPipelineExecution(env, pipelineDef.getConfig().get(PipelineOptions.PIPELINE_NAME));
    }

    private DataSink createDataSink(SinkDef sinkDef, Configuration pipelineConfig) {
        // Search the data sink factory
        DataSinkFactory sinkFactory =
                FactoryDiscoveryUtils.getFactoryByIdentifier(sinkDef.getType(), DataSinkFactory.class);

        // Include sink connector JAR
        FactoryDiscoveryUtils.getJarPathByIdentifier(sinkDef.getType(), DataSinkFactory.class)
                .ifPresent(jar -> FlinkEnvironmentUtils.addJar(env, jar));

        // Create data sink
        return sinkFactory.createDataSink(new FactoryHelper.DefaultContext(
                sinkDef.getConfig(), pipelineConfig, Thread.currentThread().getContextClassLoader()));
    }

    private void addFrameworkJars() {
        try {
            Set<URI> frameworkJars = new HashSet<>();
            // Common JAR
            // We use the core interface (Event) to search the JAR
            Optional<URL> commonJar = getContainingJar(Event.class);
            if (commonJar.isPresent()) {
                frameworkJars.add(commonJar.get().toURI());
            }
            // Runtime JAR
            // We use the serializer of the core interface (EventSerializer) to search the JAR
            Optional<URL> runtimeJar = getContainingJar(EventSerializer.class);
            if (runtimeJar.isPresent()) {
                frameworkJars.add(runtimeJar.get().toURI());
            }
            for (URI jar : frameworkJars) {
                FlinkEnvironmentUtils.addJar(env, jar.toURL());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to search and add Flink CDC framework JARs", e);
        }
    }

    private Optional<URL> getContainingJar(Class<?> clazz) throws Exception {
        URL container = clazz.getProtectionDomain().getCodeSource().getLocation();
        if (Files.isDirectory(Paths.get(container.toURI()))) {
            return Optional.empty();
        }
        return Optional.of(container);
    }
}
