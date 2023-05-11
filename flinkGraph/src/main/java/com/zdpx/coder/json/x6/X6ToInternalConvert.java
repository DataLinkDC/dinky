package com.zdpx.coder.json.x6;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.zdpx.coder.graph.Connection;
import com.zdpx.coder.graph.InputPort;
import com.zdpx.coder.graph.NodeWrapper;
import com.zdpx.coder.graph.OutputPort;
import com.zdpx.coder.graph.ProcessPackage;
import com.zdpx.coder.graph.Scene;
import com.zdpx.coder.json.ToInternalConvert;
import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.operator.TableInfo;
import com.zdpx.coder.utils.InstantiationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.zdpx.coder.graph.Scene.OPERATOR_MAP;


public final class X6ToInternalConvert implements ToInternalConvert {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Scene convert(String origin) {
        Map<String, JsonNode> operatorMap = new HashMap<>();
        Map<String, JsonNode> connectionNodes = new HashMap<>();
        Map<String, JsonNode> processNodes = new HashMap<>();
        try {
            JsonNode x6 = objectMapper.readTree(origin);
            JsonNode cells = x6.path("cells");
            for (JsonNode cell : cells) {
                String cell_shape = cell.get("shape").asText();
                String id = cell.get("id").asText();
                switch (cell_shape) {
                    case "edge":
                        connectionNodes.put(id, cell);
                        break;
                    case "package":
                        processNodes.put(id, cell);
                        break;
                    default: {
                        // operatorMap
                        operatorMap.put(id, cell);
                    }
                }
            }


            Map<String, Operator> operators = processOperators(operatorMap);
            Map<String, Connection<TableInfo>> connections = processConnections(connectionNodes, operators);
            processPackage(processNodes, operators, connections);


        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private static void processPackage(Map<String, JsonNode> processNodes,
                                       Map<String, Operator> operators,
                                       Map<String, Connection<TableInfo>> connections) {
        processNodes.forEach((id, cell) -> {
            NodeWrapper nodeWrapper = new X6NodeWrapper();
            ProcessPackage process = new ProcessPackage();
            process.setNodeWrapper(nodeWrapper);
            process.setId(id);
            String name = cell.path("attrs").path("text").path("text").asText();
            process.setName(name);
            ArrayNode children = (ArrayNode) cell.get("children");
            for (JsonNode child : children) {
                Operator operator = operators.get(child.asText());
                if (operator != null) {
                    process.getNodeWrapper().getChildren().add(operator);
                }
            }
        });
    }

    private static Map<String, Connection<TableInfo>> processConnections(Map<String, JsonNode> connectionNodes,
                                                                         Map<String, Operator> operators) {
        Map<String, Connection<TableInfo>> connections = new HashMap<>();
        connectionNodes.forEach((id, cell) -> {
            JsonNode source = cell.get("source");
            String sourceCell = source.get("cell").asText();
            String sourcePort = source.get("port").asText();
            Operator sourceOperator = operators.get(sourceCell);
            OutputPort<TableInfo> outputPort = sourceOperator.getOutputPorts().get(sourcePort);

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
        operatorMap.forEach((id, cell) -> {
            String cell_shape = cell.get("shape").asText();
            Operator operator = createOperatorByCode(cell_shape);
            operator.setId(id);
            String name = cell.path("attrs").path("text").path("text").asText();
            operator.setName(name);

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
}
