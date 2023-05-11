package com.zdpx.coder.json.x6;

import static com.zdpx.coder.graph.Scene.OPERATOR_MAP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.zdpx.coder.graph.Connection;
import com.zdpx.coder.graph.InputPort;
import com.zdpx.coder.graph.Node;
import com.zdpx.coder.graph.NodeWrapper;
import com.zdpx.coder.graph.OutputPort;
import com.zdpx.coder.graph.ProcessPackage;
import com.zdpx.coder.graph.Scene;
import com.zdpx.coder.json.ToInternalConvert;
import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.operator.TableInfo;
import com.zdpx.coder.utils.InstantiationUtil;

import lombok.Data;

public final class X6ToInternalConvert implements ToInternalConvert {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Scene convert(String origin) {

        Map<String, TempNode> tempNodes = new HashMap<>();
        Map<String, JsonNode> operatorMap = new HashMap<>();
        Map<String, JsonNode> connectionNodes = new HashMap<>();
        Map<String, JsonNode> processNodes = new HashMap<>();
        try {
            JsonNode x6 = objectMapper.readTree(origin);
            JsonNode cells = x6.path("cells");

            for (JsonNode cell : cells) {
                String id = cell.get("id").asText();
                JsonNode parent = cell.get("parent");
                ArrayNode children = (ArrayNode) cell.get("children");

                String parentStr = null;
                if (parent != null) {
                    parentStr = parent.asText();
                }

                List<String> childrenList = new ArrayList<>();
                if (children != null) {
                    for (JsonNode child : children) {
                        childrenList.add(child.asText());
                    }
                }

                TempNode tempNode = new TempNode(id, cell, parentStr, childrenList);
                tempNodes.put(id, tempNode);
            }

            final List<TempNode> topNodes =
                    tempNodes.values().stream()
                            .filter(tempNode -> tempNode.getParent() == null)
                            .collect(Collectors.toList());

            List<Node> nodes = createNodesWithPackage(tempNodes, topNodes);

            Map<String, Operator> operators = processOperators(operatorMap);
            Map<String, Connection<TableInfo>> connections =
                    processConnections(connectionNodes, operators);
            List<ProcessPackage> processPackages =
                    processPackage(processNodes, operators, connections);
            Scene scene = new Scene();

            scene.setProcessPackage(processPackages);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 按照从顶层向下初始化垂直信息
     *
     * @param allTempNodes
     * @param topNodes
     * @return
     */
    private List<Node> createNodesWithPackage(
            Map<String, TempNode> allTempNodes, List<TempNode> topNodes) {
        List<Node> nodes = new ArrayList<>();
        for (TempNode tempNode : topNodes) {
            Node node;
            JsonNode cell = tempNode.getNode();
            String cell_shape = cell.get("shape").asText();
            switch (cell_shape) {
                case "edge":
                    node = new Connection<>();
                    break;
                case "package":
                    node = new ProcessPackage();
                    List<String> childrenId = tempNode.getChildren();
                    List<TempNode> children =
                            childrenId.stream().map(allTempNodes::get).collect(Collectors.toList());
                    List<Node> childrenNode = createNodesWithPackage(allTempNodes, children);
                    node.getNodeWrapper().setChildren(childrenNode);
                    break;
                default:
                    {
                        node = createOperatorByCode(cell_shape);
                    }
            }
            nodes.add(node);
        }
        return nodes;
    }

    private static List<ProcessPackage> processPackage(
            Map<String, JsonNode> processNodes,
            Map<String, Operator> operators,
            Map<String, Connection<TableInfo>> connections) {
        List<ProcessPackage> processPackages = new ArrayList<>();
        processNodes.forEach(
                (id, cell) -> {
                    ProcessPackage process = new ProcessPackage();
                    process.setNodeWrapper(new X6NodeWrapper());
                    process.setId(id);
                    String name = cell.path("attrs").path("text").path("text").asText();
                    process.setName(name);
                    ArrayNode children = (ArrayNode) cell.get("children");
                    for (JsonNode child : children) {
                        Operator operator = operators.get(child.asText());
                        if (operator != null) {
                            process.getNodeWrapper().getChildren().add(operator);
                            processPackages.add(process);
                        }
                        // todo:                这里好像有package添加同类的问题
                    }
                });
        return processPackages;
    }

    private static Map<String, Connection<TableInfo>> processConnections(
            Map<String, JsonNode> connectionNodes, Map<String, Operator> operators) {
        Map<String, Connection<TableInfo>> connections = new HashMap<>();
        connectionNodes.forEach(
                (id, cell) -> {
                    JsonNode source = cell.get("source");
                    String sourceCell = source.get("cell").asText();
                    String sourcePort = source.get("port").asText();
                    Operator sourceOperator = operators.get(sourceCell);
                    OutputPort<TableInfo> outputPort =
                            sourceOperator.getOutputPorts().get(sourcePort);

                    JsonNode target = cell.get("target");
                    String targetCell = target.get("cell").asText();
                    String targetPort = target.get("port").asText();
                    Operator targetOperator = operators.get(targetCell);
                    InputPort<TableInfo> inputPort = targetOperator.getInputPorts().get(targetPort);

                    Connection<TableInfo> connection = new Connection();
                    connection.setFromPort(outputPort);
                    connection.setToPort(inputPort);
                    connection.setId(id);
                    NodeWrapper nodeWrapper = new X6NodeWrapper();
                    connection.setNodeWrapper(nodeWrapper);
                });
        return connections;
    }

    private Map<String, Operator> processOperators(Map<String, JsonNode> operatorMap) {
        Map<String, Operator> operators = new HashMap<>();
        operatorMap.forEach(
                (id, cell) -> {
                    String cell_shape = cell.get("shape").asText();
                    Operator operator = createOperatorByCode(cell_shape);
                    operator.setId(id);
                    String name = cell.path("attrs").path("text").path("text").asText();
                    operator.setName(name);

                    final JsonNode p = cell.get("parent");
                    if (p != null) {
                        String parent = p.asText();
                    }

                    NodeWrapper nodeWrapper = new X6NodeWrapper();
                    operator.setOperatorWrapper(nodeWrapper);

                    JsonNode data = cell.get("data");
                    if (data != null) {
                        String parameters = data.get("parameters").asText();
                        nodeWrapper.setParameters(parameters);
                    }

                    operators.put(id, operator);
                });
        return operators;
    }

    public Operator createOperatorByCode(String code) {
        Class<? extends Operator> cl = OPERATOR_MAP.get(code);
        if (cl == null) {
            String l = String.format("operator %s not exists.", code);
            log.error(l);
            throw new NullPointerException(l);
        }

        return InstantiationUtil.instantiate(cl);
    }

    @Data
    private static class TempNode {
        private final String id;
        private final JsonNode node;
        private final String parent;
        private final List<String> children;

        public TempNode(String id, JsonNode node, String parent, List<String> children) {
            this.id = id;
            this.node = node;
            this.parent = parent;
            this.children = children;
        }
    }
}
