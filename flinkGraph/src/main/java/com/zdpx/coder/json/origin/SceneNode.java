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

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/** */
public class SceneNode {

    private EnvironmentNode environment;
    private ProcessNode rootProcess;

    public static List<OperatorNode> getSourceOperatorNodes(ProcessNode process) {
        List<OperatorNode> operators = getAllOperatorNodes(process);
        return operators.stream()
                .filter(t -> CollectionUtils.isEmpty(t.getInputConnections()))
                .collect(Collectors.toList());
    }

    public static List<OperatorNode> getSinkOperatorNodes(ProcessNode process) {
        List<OperatorNode> operators = getAllOperatorNodes(process);
        return operators.stream()
                .filter(t -> CollectionUtils.isEmpty(t.getOutputConnections()))
                .collect(Collectors.toList());
    }

    public static List<OperatorNode> getOperatorNodes(ProcessNode process) {
        List<OperatorNode> operators = getAllOperatorNodes(process);
        return operators.stream()
                .filter(
                        t ->
                                !CollectionUtils.isEmpty(t.getInputConnections())
                                        && !CollectionUtils.isEmpty(t.getOutputConnections()))
                .collect(Collectors.toList());
    }

    public static List<ConnectionNode> getAllConnections(ProcessNode process) {
        List<ConnectionNode> connections = new ArrayList<>();
        List<ProcessNode> processes = new LinkedList<>();
        processes.add(process);
        while (processes.iterator().hasNext()) {
            ProcessNode processLocal = processes.iterator().next();
            Set<OperatorNode> operators = processLocal.getOperators();
            connections.addAll(processLocal.getConnects());
            if (!CollectionUtils.isEmpty(operators)) {
                for (OperatorNode operator : operators) {
                    if (!Objects.isNull(operator.getProcesses())) {
                        processes.addAll(operator.getProcesses());
                    }
                }
            }
            processes.remove(processLocal);
        }
        return connections;
    }

    public static List<OperatorNode> getAllOperatorNodes(ProcessNode process) {
        List<OperatorNode> operatorAllNodes = new ArrayList<>();
        List<ProcessNode> processes = new LinkedList<>();
        processes.add(process);
        while (processes.iterator().hasNext()) {
            ProcessNode processLocal = processes.iterator().next();
            Set<OperatorNode> operators = processLocal.getOperators();
            if (!CollectionUtils.isEmpty(operators)) {
                operatorAllNodes.addAll(operators);
                for (OperatorNode operator : operators) {
                    if (!Objects.isNull(operator.getProcesses())) {
                        processes.addAll(operator.getProcesses());
                    }
                }
            }
            processes.remove(processLocal);
        }
        return operatorAllNodes;
    }

    public void initialize() {
        List<ConnectionNode> connections = getAllConnections(rootProcess);
        List<OperatorNode> operators = getAllOperatorNodes(rootProcess);
        for (ConnectionNode connection : connections) {
            operators.stream()
                    .filter(t -> t.getCode().equals(connection.getFromOp()))
                    .findAny()
                    .ifPresent(connection::setFrom);

            operators.stream()
                    .filter(t -> t.getCode().equals(connection.getToOp()))
                    .findAny()
                    .ifPresent(connection::setTo);
        }
    }

    // region getter/setter

    public EnvironmentNode getEnvironment() {
        return environment;
    }

    public void setEnvironment(EnvironmentNode environment) {
        this.environment = environment;
    }

    public ProcessNode getProcess() {
        return rootProcess;
    }

    public void setProcess(ProcessNode process) {
        this.rootProcess = process;
    }

    // endregion
}
