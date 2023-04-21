package com.zdpx.coder.json;

import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
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
                .filter(t -> !CollectionUtils.isEmpty(t.getInputConnections()) &&
                        !CollectionUtils.isEmpty(t.getOutputConnections()))
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
        initConnection();
    }

    private void initConnection() {
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


    //region getter/setter

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

    //endregion
}
