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
import org.dinky.trans.AbstractOperation;
import org.dinky.trans.Operation;

import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.internal.TableResultImpl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Nullable;

import com.ververica.cdc.composer.PipelineComposer;
import com.ververica.cdc.composer.definition.PipelineDef;

/**
 * FlinkCDCPipelineOperation
 *
 * ################################################################################
 * # Description: Sync MySQL all tables to Doris
 * ################################################################################
 * source:
 *   type: mysql
 *   hostname: localhost
 *   port: 3306
 *   username: root
 *   password: 123456
 *   tables: app_db.\.*
 *   server-id: 5400-5404
 *   server-time-zone: UTC
 *
 * sink:
 *   type: doris
 *   fenodes: 127.0.0.1:8030
 *   username: root
 *   password: ""
 *   table.create.properties.light_schema_change: true
 *   table.create.properties.replication_num: 1
 *
 * pipeline:
 *   name: Sync MySQL Database to Doris
 *   parallelism: 2
 */
public class FlinkCDCPipelineOperation extends AbstractOperation implements Operation {

    private static final String KEY_WORD = "EXECUTE PIPELINE";

    public FlinkCDCPipelineOperation() {}

    public FlinkCDCPipelineOperation(String statement) {
        super(statement);
    }

    @Override
    public String getHandle() {
        return KEY_WORD;
    }

    @Override
    public Operation create(String statement) {
        return new FlinkCDCPipelineOperation(statement);
    }

    @Override
    public TableResult execute(Executor executor) {
        String yamlText = getPipelineConfigure(statement);
        com.ververica.cdc.common.configuration.Configuration globalPipelineConfig =
                com.ververica.cdc.common.configuration.Configuration.fromMap(executor.getSetConfig());
        // Parse pipeline definition file
        YamlTextPipelineDefinitionParser pipelineDefinitionParser = new YamlTextPipelineDefinitionParser();
        // Create composer
        PipelineComposer composer = createComposer(executor);

        try {
            PipelineDef pipelineDef = pipelineDefinitionParser.parse(yamlText, globalPipelineConfig);
            // Compose pipeline
            composer.compose(pipelineDef);
            return TableResultImpl.TABLE_RESULT_OK;
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public String getPipelineConfigure(String statement) {
        Pattern patternYaml = Pattern.compile("(?is)^EXECUTE\\s+PIPELINE\\s+WITHYAML\\s+\\((.+)\\)");
        Matcher matcherYaml = patternYaml.matcher(statement);
        if (matcherYaml.find()) {
            return matcherYaml.group(1);
        }
        return "";
    }

    public DinkyFlinkPipelineComposer createComposer(Executor executor) {

        return DinkyFlinkPipelineComposer.of(executor);
    }
}
