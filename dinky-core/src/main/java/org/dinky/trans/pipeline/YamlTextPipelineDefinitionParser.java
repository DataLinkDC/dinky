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

import static com.ververica.cdc.common.utils.Preconditions.checkNotNull;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.type.TypeReference;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ververica.cdc.common.configuration.Configuration;
import com.ververica.cdc.composer.definition.PipelineDef;
import com.ververica.cdc.composer.definition.RouteDef;
import com.ververica.cdc.composer.definition.SinkDef;
import com.ververica.cdc.composer.definition.SourceDef;

/**
 * YamlTextPipelineDefinitionParser
 */
/** Parser for converting YAML formatted pipeline definition to {@link PipelineDef}. */
public class YamlTextPipelineDefinitionParser {

    // Parent node keys
    private static final String SOURCE_KEY = "source";
    private static final String SINK_KEY = "sink";
    private static final String ROUTE_KEY = "route";
    private static final String PIPELINE_KEY = "pipeline";

    // Source / sink keys
    private static final String TYPE_KEY = "type";
    private static final String NAME_KEY = "name";

    // Route keys
    private static final String ROUTE_SOURCE_TABLE_KEY = "source-table";
    private static final String ROUTE_SINK_TABLE_KEY = "sink-table";
    private static final String ROUTE_DESCRIPTION_KEY = "description";

    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /** Parse the specified pipeline definition text. */
    public PipelineDef parse(String text, Configuration globalPipelineConfig) throws Exception {
        JsonNode root = mapper.readTree(text);

        // Source is required
        SourceDef sourceDef = toSourceDef(
                checkNotNull(root.get(SOURCE_KEY), "Missing required field \"%s\" in pipeline definition", SOURCE_KEY));

        // Sink is required
        SinkDef sinkDef = toSinkDef(
                checkNotNull(root.get(SINK_KEY), "Missing required field \"%s\" in pipeline definition", SINK_KEY));

        // Routes are optional
        List<RouteDef> routeDefs = new ArrayList<>();
        Optional.ofNullable(root.get(ROUTE_KEY))
                .ifPresent(node -> node.forEach(route -> routeDefs.add(toRouteDef(route))));

        // Pipeline configs are optional
        Configuration userPipelineConfig = toPipelineConfig(root.get(PIPELINE_KEY));

        // Merge user config into global config
        Configuration pipelineConfig = new Configuration();
        pipelineConfig.addAll(globalPipelineConfig);
        pipelineConfig.addAll(userPipelineConfig);

        return new PipelineDef(sourceDef, sinkDef, routeDefs, null, pipelineConfig);
    }

    private SourceDef toSourceDef(JsonNode sourceNode) {
        Map<String, String> sourceMap = mapper.convertValue(sourceNode, new TypeReference<Map<String, String>>() {});

        // "type" field is required
        String type = checkNotNull(
                sourceMap.remove(TYPE_KEY), "Missing required field \"%s\" in source configuration", TYPE_KEY);

        // "name" field is optional
        String name = sourceMap.remove(NAME_KEY);

        return new SourceDef(type, name, Configuration.fromMap(sourceMap));
    }

    private SinkDef toSinkDef(JsonNode sinkNode) {
        Map<String, String> sinkMap = mapper.convertValue(sinkNode, new TypeReference<Map<String, String>>() {});

        // "type" field is required
        String type =
                checkNotNull(sinkMap.remove(TYPE_KEY), "Missing required field \"%s\" in sink configuration", TYPE_KEY);

        // "name" field is optional
        String name = sinkMap.remove(NAME_KEY);

        return new SinkDef(type, name, Configuration.fromMap(sinkMap));
    }

    private RouteDef toRouteDef(JsonNode routeNode) {
        String sourceTable = checkNotNull(
                        routeNode.get(ROUTE_SOURCE_TABLE_KEY),
                        "Missing required field \"%s\" in route configuration",
                        ROUTE_SOURCE_TABLE_KEY)
                .asText();
        String sinkTable = checkNotNull(
                        routeNode.get(ROUTE_SINK_TABLE_KEY),
                        "Missing required field \"%s\" in route configuration",
                        ROUTE_SINK_TABLE_KEY)
                .asText();
        String description = Optional.ofNullable(routeNode.get(ROUTE_DESCRIPTION_KEY))
                .map(JsonNode::asText)
                .orElse(null);
        return new RouteDef(sourceTable, sinkTable, description);
    }

    private Configuration toPipelineConfig(JsonNode pipelineConfigNode) {
        if (pipelineConfigNode == null || pipelineConfigNode.isNull()) {
            return new Configuration();
        }
        Map<String, String> pipelineConfigMap =
                mapper.convertValue(pipelineConfigNode, new TypeReference<Map<String, String>>() {});
        return Configuration.fromMap(pipelineConfigMap);
    }
}
