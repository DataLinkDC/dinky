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

package org.dinky.data.model;

import org.dinky.assertion.Asserts;
import org.dinky.config.Dialect;
import org.dinky.data.typehandler.TaskExtConfigTypeHandler;
import org.dinky.job.JobConfig;
import org.dinky.mybatis.model.SuperEntity;

import org.apache.ibatis.type.JdbcType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务
 *
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dinky_task")
@Slf4j
@ApiModel(value = "Task", description = "Task Information")
public class Task extends SuperEntity<Task> {

    private static final long serialVersionUID = 5988972129893667154L;

    @ApiModelProperty(value = "Dialect", dataType = "String", notes = "Dialect for the task")
    private String dialect;

    @ApiModelProperty(
            value = "Tenant ID",
            dataType = "Integer",
            example = "1001",
            notes = "ID of the tenant associated with the task")
    private Integer tenantId;

    @ApiModelProperty(value = "Type", dataType = "String", notes = "Type of the task")
    private String type;

    @ApiModelProperty(value = "Check Point", dataType = "Integer", example = "1", notes = "Check point for the task")
    private Integer checkPoint;

    @ApiModelProperty(
            value = "Save Point Strategy",
            dataType = "Integer",
            example = "2",
            notes = "Save point strategy for the task")
    private Integer savePointStrategy;

    @ApiModelProperty(value = "Save Point Path", dataType = "String", notes = "Save point path for the task")
    private String savePointPath;

    @ApiModelProperty(value = "Parallelism", dataType = "Integer", example = "4", notes = "Parallelism for the task")
    private Integer parallelism;

    @ApiModelProperty(
            value = "Fragment",
            dataType = "Boolean",
            example = "true",
            notes = "Fragment option for the task")
    private Boolean fragment;

    @ApiModelProperty(
            value = "Statement Set",
            dataType = "Boolean",
            example = "false",
            notes = "Statement set option for the task")
    private Boolean statementSet;

    @ApiModelProperty(
            value = "Batch Model",
            dataType = "Boolean",
            example = "true",
            notes = "Batch model option for the task")
    private Boolean batchModel;

    @ApiModelProperty(
            value = "Cluster ID",
            dataType = "Integer",
            example = "2001",
            notes = "ID of the cluster associated with the task")
    private Integer clusterId;

    @ApiModelProperty(
            value = "Cluster Configuration ID",
            dataType = "Integer",
            example = "3001",
            notes = "ID of the cluster configuration associated with the task")
    private Integer clusterConfigurationId;

    @ApiModelProperty(
            value = "Database ID",
            dataType = "Integer",
            example = "4001",
            notes = "ID of the database associated with the task")
    private Integer databaseId;

    @ApiModelProperty(
            value = "JAR ID",
            dataType = "Integer",
            example = "5001",
            notes = "ID of the JAR associated with the task")
    private Integer jarId;

    @ApiModelProperty(
            value = "Environment ID",
            dataType = "Integer",
            example = "6001",
            notes = "ID of the environment associated with the task")
    private Integer envId;

    @ApiModelProperty(
            value = "Alert Group ID",
            dataType = "Integer",
            example = "7001",
            notes = "ID of the alert group associated with the task")
    private Integer alertGroupId;

    @ApiModelProperty(value = "Note", dataType = "String", notes = "Additional notes for the task")
    private String note;

    @ApiModelProperty(value = "Step", dataType = "Integer", example = "1", notes = "Step for the task")
    private Integer step;

    @ApiModelProperty(
            value = "Job Instance ID",
            dataType = "Integer",
            example = "8001",
            notes = "ID of the job instance associated with the task")
    private Integer jobInstanceId;

    @ApiModelProperty(
            value = "Version ID",
            dataType = "Integer",
            example = "9001",
            notes = "ID of the version associated with the task")
    private Integer versionId;

    @ApiModelProperty(value = "Statement", dataType = "String", notes = "SQL statement for the task")
    @TableField(exist = false)
    private String statement;

    @ApiModelProperty(value = "Cluster Name", dataType = "String", notes = "Name of the associated cluster")
    @TableField(exist = false)
    private String clusterName;

    @ApiModelProperty(
            value = "Save Points",
            dataType = "List<Savepoints>",
            notes = "List of save points associated with the task")
    @TableField(exist = false)
    private List<Savepoints> savePoints;

    @ApiModelProperty(
            value = "Configuration JSON",
            dataType = "TaskExtConfig",
            notes = "Extended configuration in JSON format for the task")
    @TableField(typeHandler = TaskExtConfigTypeHandler.class, jdbcType = JdbcType.VARCHAR)
    private TaskExtConfig configJson;

    @ApiModelProperty(value = "Path", dataType = "String", notes = "Path associated with the task")
    @TableField(exist = false)
    private String path;

    @ApiModelProperty(value = "JAR Name", dataType = "String", notes = "Name of the associated JAR")
    @TableField(exist = false)
    private String jarName;

    @ApiModelProperty(
            value = "Cluster Configuration Name",
            dataType = "String",
            notes = "Name of the associated cluster configuration")
    @TableField(exist = false)
    private String clusterConfigurationName;

    @ApiModelProperty(value = "Database Name", dataType = "String", notes = "Name of the associated database")
    @TableField(exist = false)
    private String databaseName;

    @ApiModelProperty(value = "Environment Name", dataType = "String", notes = "Name of the associated environment")
    @TableField(exist = false)
    private String envName;

    @ApiModelProperty(value = "Alert Group Name", dataType = "String", notes = "Name of the associated alert group")
    @TableField(exist = false)
    private String alertGroupName;

    public JobConfig buildSubmitConfig() {
        boolean useRemote = clusterId != null && clusterId != 0;

        List<ConfigItem> extCustomConfig = this.configJson.getCustomConfig();

        Map<String, String> parsedConfig =
                extCustomConfig.stream().collect(Collectors.toMap(ConfigItem::getKey, ConfigItem::getValue));

        int jid = Asserts.isNull(jarId) ? 0 : jarId;
        boolean fg = Asserts.isNotNull(fragment) && fragment;
        boolean sts = Asserts.isNotNull(statementSet) && statementSet;
        return new JobConfig(
                type,
                step,
                false,
                false,
                useRemote,
                clusterId,
                clusterConfigurationId,
                jid,
                getId(),
                getName(),
                fg,
                sts,
                batchModel,
                checkPoint,
                parallelism,
                savePointStrategy,
                savePointPath,
                parsedConfig,
                isJarTask());
    }

    /**
     * 根据key获取自定义配置的值
     * @param key
     * @return String
     */
    public String findCustomConfigKeyOfValue(String key) {
        return this.configJson.containsKey(key) ? this.configJson.getCustomConfigValue(key) : null;
    }

    /**
     * 判断是否有自定义配置
     * @param key
     * @return
     */
    public boolean hasCustomConfigKey(String key) {
        return this.configJson.containsKey(key);
    }

    public JsonNode parseJsonNode(ObjectMapper mapper) {
        ObjectNode jsonNode = mapper.createObjectNode();
        jsonNode.put("name", this.getName());
        jsonNode.put("dialect", this.dialect);
        jsonNode.put("type", this.type);
        jsonNode.put("statement", this.statement);
        jsonNode.put("checkPoint", this.checkPoint);
        jsonNode.put("savePointStrategy", this.savePointStrategy);
        jsonNode.put("savePointPath", this.savePointPath);
        jsonNode.put("parallelism", this.parallelism);
        jsonNode.put("fragment", this.fragment);
        jsonNode.put("statementSet", this.statementSet);
        jsonNode.put("batchModel", this.batchModel);
        jsonNode.put("clusterName", this.clusterName);
        jsonNode.put("note", this.note);
        jsonNode.put("step", this.step);
        jsonNode.put("enabled", this.getEnabled());
        return jsonNode;
    }

    public boolean isJarTask() {
        return Dialect.isJarDialect(dialect);
    }
}
