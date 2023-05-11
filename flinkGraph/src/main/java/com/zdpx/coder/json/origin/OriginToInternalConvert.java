package com.zdpx.coder.json.origin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zdpx.coder.graph.Connection;
import com.zdpx.coder.graph.Environment;
import com.zdpx.coder.graph.InputPort;
import com.zdpx.coder.graph.OperatorWrapper;
import com.zdpx.coder.graph.OutputPort;
import com.zdpx.coder.graph.Process;
import com.zdpx.coder.graph.Scene;
import com.zdpx.coder.json.ToInternalConvert;
import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.operator.TableInfo;
import com.zdpx.coder.utils.InstantiationUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.zdpx.coder.graph.Scene.OPERATOR_MAP;
import static com.zdpx.coder.graph.Scene.getAllOperatorWrappers;

@Slf4j
public class OriginToInternalConvert implements ToInternalConvert {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根据输入流, 生成场景节点(对应于配置)
     *
     * @param in 配置文件输入流
     * @return 场景节点
     */
    public static SceneNode readScene(String in) {
        return readSceneInternal(in);
    }

    private static SceneNode readSceneInternal(Object in) {
        SceneNode scene;
        try {
            if (in instanceof String) {
                scene = objectMapper.readValue((String) in, SceneNode.class);
            } else if (in instanceof InputStream) {
                scene = objectMapper.readValue((InputStream) in, SceneNode.class);
            } else {
                return null;
            }

            scene.initialize();
            return scene;
        } catch (IOException e) {
            log.error("readScene error, exception {}", e.getMessage());
        }
        return null;
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
     * 读取配置文件, 生成场景节点(对应于配置)
     *
     * @param filePath 配置文件路径
     * @return 场景节点
     */
    public static SceneNode readSceneFromFile(String filePath) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(filePath);
            return readScene(fis);
        } catch (FileNotFoundException e) {
            log.error("readScene error, file not exists {}", e.getMessage());
        }
        return null;
    }

    /**
     * 根据输入流, 生成场景节点(对应于配置)
     *
     * @param in 配置文件输入流
     * @return 场景节点
     */
    public static SceneNode readScene(InputStream in) {
        return readSceneInternal(in);
    }


    /**
     * 将外部场景节点(对应于配置文件)转换为内部场景类
     *
     * @param sceneNode 外部场景节点
     * @return 内部场景类
     */
    public static Scene convertToInternal(SceneNode sceneNode) {
        return createScene(sceneNode);
    }


    /**
     * 将{@link OperatorNode} 配置信息转化为{@link OperatorWrapper}节点包裹类
     *
     * @param operatorNode 外部配置信息类
     * @return {@link OperatorWrapper}节点包裹类
     */
    public static OperatorWrapper convertOperator(OperatorNode operatorNode) {
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
    public static Connection<TableInfo> convertConnection(ConnectionNode connectionNode) {
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
    public static Description convertDescription(DescriptionNode description) {
        Description descriptionInternal = new Description();
        BeanUtils.copyProperties(description, descriptionInternal);
        return descriptionInternal;
    }

    /**
     * 将配置中的{@link ProcessNode}和{@link OperatorNode}转化为内部计算图的{@link Process}和{@link Operator}.
     *
     * @param process 开始根过程节点
     * @return 计算图的根节点
     */
    private static Process convertNodeToInner(ProcessNode process) {
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
     * 将配置文件结构信息转换为内部计算逻辑图(计算图)
     *
     * @param process 外部配置的根过程节点
     * @return 内部计算图的根过程节点
     */
    @SuppressWarnings("unchecked")
    public static Process covertProcess(ProcessNode process) {

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


    public static Scene createScene(SceneNode sceneNode) {
        Scene scene = new Scene();
        Environment environment = new Environment();
        scene.setEnvironment(environment);
        BeanUtils.copyProperties(sceneNode.getEnvironment(), scene.getEnvironment());
        scene.setProcess(covertProcess(sceneNode.getProcess()));
        return scene;
    }

    @Override
    public Scene convert(String origin) {
        SceneNode scene = readScene(origin);
        return convertToInternal(scene);
    }
}
