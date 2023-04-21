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

package com.zdpx.coder.graph;

import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.table.functions.UserDefinedFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.springframework.beans.BeanUtils;

import com.zdpx.coder.SceneCodeBuilder;
import com.zdpx.coder.json.ConnectionNode;
import com.zdpx.coder.json.Description;
import com.zdpx.coder.json.DescriptionNode;
import com.zdpx.coder.json.OperatorNode;
import com.zdpx.coder.json.ProcessNode;
import com.zdpx.coder.json.SceneNode;
import com.zdpx.coder.operator.Identifier;
import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.operator.TableInfo;
import com.zdpx.coder.utils.InstantiationUtil;
import com.zdpx.udf.IUdfDefine;

import lombok.extern.slf4j.Slf4j;

/** 场景配置类, */
@Slf4j
public class Scene {

    /** 保存所有已定义算子, 类初始化时进行加载 */
    protected static final Map<String, Class<? extends Operator>> OPERATOR_MAP = getOperatorMaps();

    public static final Map<String, String> USER_DEFINED_FUNCTION = getUserDefinedFunctionMaps();

    private Environment environment;
    private Process process;

    public Scene(SceneNode scene) {
        environment = new Environment();
        BeanUtils.copyProperties(scene.getEnvironment(), this.getEnvironment());
        this.process = covertProcess(scene.getProcess());
    }

    /**
     * 获取所有节点的包裹类
     *
     * @param process 计算图的过程信息
     * @return 所有节点的包裹类
     */
    public static List<OperatorWrapper> getAllOperatorWrappers(Process process) {
        List<OperatorWrapper> operatorWrapperAllNodes = new ArrayList<>();
        List<Process> processes = new LinkedList<>();
        processes.add(process);
        while (processes.iterator().hasNext()) {
            Process processLocal = processes.iterator().next();
            Set<OperatorWrapper> operatorWrappers = processLocal.getOperators();
            if (!CollectionUtils.isEmpty(operatorWrappers)) {
                operatorWrapperAllNodes.addAll(operatorWrappers);
                for (OperatorWrapper operatorWrapper : operatorWrappers) {
                    if (!Objects.isNull(operatorWrapper.getProcesses())) {
                        processes.addAll(operatorWrapper.getProcesses());
                    }
                }
            }
            processes.remove(processLocal);
        }
        return operatorWrapperAllNodes;
    }

    /**
     * get all source nodes, source nodes is operator that not have {@link InputPort} define or all
     * {@link InputPort}'s {@link Connection} is null.
     *
     * @param process process level
     * @return List<OperatorWrapper>
     */
    public static List<OperatorWrapper> getSourceOperatorNodes(Process process) {
        List<OperatorWrapper> operatorWrappers = getAllOperatorWrappers(process);
        return operatorWrappers.stream()
                .filter(
                        t ->
                                t.getOperator().getInputPorts().stream()
                                        .allMatch(p -> Objects.isNull(p.getConnection())))
                .collect(Collectors.toList());
    }

    public static List<OperatorWrapper> getSinkOperatorNodes(Process process) {
        List<OperatorWrapper> operatorWrappers = getAllOperatorWrappers(process);
        return operatorWrappers.stream()
                .filter(t -> CollectionUtils.isEmpty(t.getOperator().getOutputPorts()))
                .collect(Collectors.toList());
    }

    public static List<OperatorWrapper> getOperatorNodes(Process process) {
        List<OperatorWrapper> operatorWrappers = getAllOperatorWrappers(process);
        return operatorWrappers.stream()
                .filter(
                        t ->
                                !CollectionUtils.isEmpty(t.getOperator().getInputPorts())
                                        && !CollectionUtils.isEmpty(
                                                t.getOperator().getOutputPorts()))
                .collect(Collectors.toList());
    }

    public static List<Connection<TableInfo>> getAllConnections(Process process) {
        List<Connection<TableInfo>> connections = new ArrayList<>();
        List<Process> processes = new LinkedList<>();
        processes.add(process);
        while (processes.iterator().hasNext()) {
            Process processLocal = processes.iterator().next();
            Set<OperatorWrapper> operatorWrappers = processLocal.getOperators();
            connections.addAll(processLocal.getConnects());
            if (!CollectionUtils.isEmpty(operatorWrappers)) {
                for (OperatorWrapper operatorWrapper : operatorWrappers) {
                    if (!Objects.isNull(operatorWrapper.getProcesses())) {
                        processes.addAll(operatorWrapper.getProcesses());
                    }
                }
            }
            processes.remove(processLocal);
        }
        return connections;
    }

    /**
     * 获取所有operator的定义, key为{@link Identifier#getCode()} 返回值, 目前为Operator类的全限定名 value为类型定义.
     *
     * @return 返回operator集
     */
    public static Map<String, Class<? extends Operator>> getOperatorMaps() {
        Set<Class<? extends Operator>> operators = Operator.getAllOperatorGenerators();
        return SceneCodeBuilder.getCodeClassMap(operators);
    }

    /**
     * 获取所有算子类, key 为 {@link IUdfDefine#getUdfName()}定义
     *
     * @return 算子类字典
     */
    public static Map<String, Class<? extends UserDefinedFunction>>
            getUserDefinedFunctionClassMaps() {
        String iun = IUdfDefine.class.getPackage().getName();
        Reflections reflections = new Reflections(iun);
        Set<Class<? extends UserDefinedFunction>> udfFunctions =
                reflections.getSubTypesOf(UserDefinedFunction.class);
        List<Class<? extends UserDefinedFunction>> uf =
                udfFunctions.stream()
                        .filter(IUdfDefine.class::isAssignableFrom)
                        .collect(Collectors.toList());
        return uf.stream()
                .collect(
                        Collectors.toMap(
                                t -> {
                                    try {
                                        UserDefinedFunction ob =
                                                t.getDeclaredConstructor().newInstance();
                                        Method method = t.getMethod("getUdfName");
                                        return (String) method.invoke(ob);
                                    } catch (NoSuchMethodException
                                            | InvocationTargetException
                                            | IllegalAccessException
                                            | InstantiationException ignore) {
                                        //
                                    }
                                    return null;
                                },
                                Function.identity()));
    }

    public static Map<String, String> getUserDefinedFunctionMaps() {
        return getUserDefinedFunctionClassMaps().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, t -> t.getValue().getName()));
    }

    /**
     * 将配置文件结构信息转换为内部计算逻辑图(计算图)
     *
     * @param process 外部配置的根过程节点
     * @return 内部计算图的根过程节点
     */
    @SuppressWarnings("unchecked")
    public Process covertProcess(ProcessNode process) {

        Process root = convertNodeToInner(process);

        // process connection relation
        List<OperatorWrapper> operatorWrappers = getAllOperatorWrappers(root);
        List<ConnectionNode> connections = SceneNode.getAllConnections(process);

        for (ConnectionNode connection : connections) {
            final Connection<TableInfo> connectionInternal = convertConnection(connection);
            BeanUtils.copyProperties(connection, connectionInternal);

            Optional<OutputPort> outputPort =
                    operatorWrappers.stream()
                            .filter(t -> t.getId().equals(connection.getFromOp()))
                            .findAny()
                            .flatMap(
                                    from ->
                                            from.getOperator().getOutputPorts().stream()
                                                    .filter(
                                                            t ->
                                                                    Objects.equals(
                                                                            t.getName(),
                                                                            connection
                                                                                    .getFromPort()))
                                                    .findAny());

            if (outputPort.isPresent()) {
                OutputPort t = outputPort.get();
                connectionInternal.setFromPort(t);
                t.setConnection(connectionInternal);
            } else {
                log.error("not find connection FromOperator: {}", connection.getFromOp());
            }

            Optional<InputPort> inputPort =
                    operatorWrappers.stream()
                            .filter(t -> t.getId().equals(connection.getToOp()))
                            .findAny()
                            .flatMap(
                                    to ->
                                            to.getOperator().getInputPorts().stream()
                                                    .filter(
                                                            t ->
                                                                    Objects.equals(
                                                                            t.getName(),
                                                                            connection.getToPort()))
                                                    .findAny());

            if (inputPort.isPresent()) {
                InputPort t = inputPort.get();
                connectionInternal.setToPort(t);
                t.setConnection(connectionInternal);
            } else {
                log.error(
                        "not find connection ToOperator: {}, From: {}",
                        connection.getToPort(),
                        connection.getFromOp());
            }
        }

        return root;
    }

    /**
     * 将配置中的{@link ProcessNode}和{@link OperatorNode}转化为内部计算图的{@link Process}和{@link Operator}.
     *
     * @param process 开始根过程节点
     * @return 计算图的根节点
     */
    private Process convertNodeToInner(ProcessNode process) {
        List<ProcessNode> unWalkProcesses = new LinkedList<>();
        unWalkProcesses.add(process);

        Process processCurrent = new Process();
        Map<String, OperatorWrapper> operatorCodeWrapperMap = new HashMap<>();
        Process root = processCurrent;

        while (unWalkProcesses.iterator().hasNext()) {
            // BFS
            ProcessNode processNodeCurrent = unWalkProcesses.iterator().next();
            BeanUtils.copyProperties(processNodeCurrent, processCurrent);
            if (processNodeCurrent.getParent() != null) {
                // add inner parentProcess
                operatorCodeWrapperMap
                        .get(processNodeCurrent.getParent().getCode())
                        .getProcesses()
                        .add(processCurrent);
            }

            Set<OperatorNode> operatorNodesInProcess = processNodeCurrent.getOperators();
            if (!CollectionUtils.isEmpty(operatorNodesInProcess)) {
                for (OperatorNode operatorNode : operatorNodesInProcess) {
                    OperatorWrapper operatorWrapper = convertOperator(operatorNode);
                    operatorWrapper.setParentProcess(processCurrent);
                    operatorCodeWrapperMap.put(operatorNode.getCode(), operatorWrapper);
                    processCurrent.getOperators().add(operatorWrapper);

                    if (!Objects.isNull(operatorNode.getProcesses())) {
                        unWalkProcesses.addAll(operatorNode.getProcesses());
                        for (ProcessNode processInNode : operatorNode.getProcesses()) {
                            processInNode.setParent(operatorNode);
                        }
                    }
                }
            }

            for (DescriptionNode descriptor : processNodeCurrent.getDescriptions()) {
                final Description description = convertDescription(descriptor);
                processCurrent.getDescriptions().add(description);
            }

            unWalkProcesses.remove(processNodeCurrent);
            processCurrent = new Process();
        }
        return root;
    }

    /**
     * 将{@link OperatorNode} 配置信息转化为{@link OperatorWrapper}节点包裹类
     *
     * @param operatorNode 外部配置信息类
     * @return {@link OperatorWrapper}节点包裹类
     */
    public OperatorWrapper convertOperator(OperatorNode operatorNode) {
        final OperatorWrapper operatorWrapper = new OperatorWrapper();
        BeanUtils.copyProperties(operatorNode, operatorWrapper, null, "parameters");
        operatorWrapper.setParameters(operatorNode.getParameters().toString());
        Class<? extends Operator> cl = OPERATOR_MAP.get(operatorNode.getCode());

        if (cl == null) {
            String l = String.format("operator %s not exists.", operatorNode.getCode());
            log.error(l);
            throw new NullPointerException(l);
        }

        Operator operator = InstantiationUtil.instantiate(cl);
        // operator.setScene(this);
        operator.setOperatorWrapper(operatorWrapper);
        operatorWrapper.setOperator(operator);

        return operatorWrapper;
    }

    /**
     * 将配置的{@link ConnectionNode}软化为{@link Connection}
     *
     * @param connectionNode 配置中的连接信息
     * @return 计算图的连接信息
     */
    public Connection<TableInfo> convertConnection(ConnectionNode connectionNode) {
        Connection connectionInternal = new Connection<TableInfo>();
        BeanUtils.copyProperties(connectionNode, connectionInternal);
        return connectionInternal;
    }

    /**
     * 将外部{@link DescriptionNode}说明信息节点映射成内部{@link Description} 信息节点
     *
     * @param description 说明信息节点
     * @return {@link Description} 信息节点
     */
    public Description convertDescription(DescriptionNode description) {
        Description descriptionInternal = new Description();
        BeanUtils.copyProperties(description, descriptionInternal);
        return descriptionInternal;
    }

    // region g/s
    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
    // endregion
}
