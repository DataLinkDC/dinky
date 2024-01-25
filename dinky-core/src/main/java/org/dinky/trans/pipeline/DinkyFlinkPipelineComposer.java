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

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.ververica.cdc.common.configuration.Configuration;
import com.ververica.cdc.common.event.Event;
import com.ververica.cdc.common.factories.DataSinkFactory;
import com.ververica.cdc.common.factories.FactoryHelper;
import com.ververica.cdc.common.pipeline.PipelineOptions;
import com.ververica.cdc.common.sink.DataSink;
import com.ververica.cdc.composer.PipelineComposer;
import com.ververica.cdc.composer.PipelineExecution;
import com.ververica.cdc.composer.definition.PipelineDef;
import com.ververica.cdc.composer.definition.SinkDef;
import com.ververica.cdc.composer.flink.FlinkEnvironmentUtils;
import com.ververica.cdc.composer.flink.coordination.OperatorIDGenerator;
import com.ververica.cdc.composer.flink.translator.DataSinkTranslator;
import com.ververica.cdc.composer.flink.translator.DataSourceTranslator;
import com.ververica.cdc.composer.flink.translator.PartitioningTranslator;
import com.ververica.cdc.composer.flink.translator.RouteTranslator;
import com.ververica.cdc.composer.flink.translator.SchemaOperatorTranslator;
import com.ververica.cdc.composer.utils.FactoryDiscoveryUtils;
import com.ververica.cdc.runtime.serializer.event.EventSerializer;

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

        // Source
        DataSourceTranslator sourceTranslator = new DataSourceTranslator();
        DataStream<Event> stream = sourceTranslator.translate(pipelineDef.getSource(), env, pipelineDef.getConfig());

        // Route
        RouteTranslator routeTranslator = new RouteTranslator();
        stream = routeTranslator.translate(stream, pipelineDef.getRoute());

        // Create sink in advance as schema operator requires MetadataApplier
        DataSink dataSink = createDataSink(pipelineDef.getSink(), pipelineDef.getConfig());

        // Schema operator
        SchemaOperatorTranslator schemaOperatorTranslator = new SchemaOperatorTranslator(
                pipelineDef.getConfig().get(PipelineOptions.PIPELINE_SCHEMA_CHANGE_BEHAVIOR),
                pipelineDef.getConfig().get(PipelineOptions.PIPELINE_SCHEMA_OPERATOR_UID));
        stream = schemaOperatorTranslator.translate(stream, parallelism, dataSink.getMetadataApplier());
        OperatorIDGenerator schemaOperatorIDGenerator =
                new OperatorIDGenerator(schemaOperatorTranslator.getSchemaOperatorUid());

        // Add partitioner
        PartitioningTranslator partitioningTranslator = new PartitioningTranslator();
        stream = partitioningTranslator.translate(
                stream, parallelism, parallelism, schemaOperatorIDGenerator.generate());

        // Sink
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
