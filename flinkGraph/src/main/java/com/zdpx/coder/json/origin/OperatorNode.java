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

package com.zdpx.coder.json.origin;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 节点配置信息,对应于json配置文件
 *
 * @author Licho Sun
 */
public class OperatorNode {
    /** 节点并发度 */
    private int parallelism;
    /** 节点ID */
    private String id;
    /** 节点代码, 目前使用类路径作为值 */
    private String code;
    /** 节点名称 */
    private String name;
    /** 节点是否牌活动状态 */
    private boolean activated;
    /** 节点是否可展开 */
    private boolean expanded;
    /** 版本兼容性信息 */
    private String compatibility;
    /** 所在过程信息 */
    private ProcessNode parentProcess;
    /** 节点来源 */
    private String origin;
    /** 节点高度 */
    private int height;
    /** 节点宽度 */
    private int width;
    /** x坐标 */
    private int x;
    /** y坐标 */
    private int y;
    /** 包含的过程信息 */
    private List<ProcessNode> processes;
    /** 节点所需参数 */
    private JsonNode parameters;
    /** 输入连接信息 */
    private List<ConnectionNode> inputConnections;
    /** 输出连接信息 */
    private List<ConnectionNode> outputConnections;

    // region getter/setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessNode getParentProcess() {
        return parentProcess;
    }

    public void setParentProcess(ProcessNode parentProcess) {
        this.parentProcess = parentProcess;
    }

    public List<ConnectionNode> getInputConnections() {
        return inputConnections;
    }

    public void setInputConnections(List<ConnectionNode> inputConnections) {
        this.inputConnections = inputConnections;
    }

    public List<ConnectionNode> getOutputConnections() {
        return outputConnections;
    }

    public void setOutputConnections(List<ConnectionNode> outputConnections) {
        this.outputConnections = outputConnections;
    }

    public JsonNode getParameters() {
        return parameters;
    }

    public void setParameters(JsonNode parameters) {
        this.parameters = parameters;
    }

    public String getCompatibility() {
        return compatibility;
    }

    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }

    public List<ProcessNode> getProcesses() {
        return processes;
    }

    public void setProcesses(List<ProcessNode> processes) {
        this.processes = processes;
    }

    // endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OperatorNode operator = (OperatorNode) o;
        return parallelism == operator.parallelism
                && activated == operator.activated
                && expanded == operator.expanded
                && height == operator.height
                && width == operator.width
                && x == operator.x
                && y == operator.y
                && code.equals(operator.code)
                && name.equals(operator.name)
                && Objects.equals(compatibility, operator.compatibility)
                && Objects.equals(parentProcess, operator.parentProcess)
                && Objects.equals(origin, operator.origin)
                && Objects.equals(processes, operator.processes)
                && Objects.equals(parameters, operator.parameters)
                && Objects.equals(inputConnections, operator.inputConnections)
                && Objects.equals(outputConnections, operator.outputConnections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, name);
    }
}
