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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zdpx.coder.SceneCodeBuilder;
import com.zdpx.coder.operator.Identifier;
import com.zdpx.coder.operator.Operator;
import com.zdpx.coder.utils.InstantiationUtil;
import com.zdpx.udf.IUdfDefine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.table.functions.UserDefinedFunction;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 场景配置类,
 */
@Slf4j
@Data
public class Scene {

    /**
     * 保存所有已定义算子, 类初始化时进行加载
     */
    public static final Map<String, Class<? extends Operator>> OPERATOR_MAP = getOperatorMaps();

    public static final Map<String, String> USER_DEFINED_FUNCTION = getUserDefinedFunctionMaps();
    private static final ObjectMapper mapper = new ObjectMapper();
    private Environment environment = new Environment();
    private ProcessPackage processPackage;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取所有operator的定义, key为{@link Identifier#getCode()} 返回值, 目前为Operator类的全限定名 value为类型定义.
     *
     * @return 返回operator集
     */
    public static Map<String, Class<? extends Operator>> getOperatorMaps() {
        Set<Class<? extends Operator>> operators = Operator.getAllOperatorGenerators();
        return SceneCodeBuilder.getCodeClassMap(operators);
    }

    public static List<Operator> getSinkOperatorNodes(ProcessPackage processPackage) {
        List<Operator> originOperator = getAllOperator(processPackage);
        return originOperator.stream()
                .filter(t -> CollectionUtils.isEmpty(t.getOutputPorts().values()))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有节点的包裹类
     *
     * @param processPackage 计算图的过程信息
     * @return 所有节点的包裹类
     */
    public static List<Operator> getAllOperator(ProcessPackage processPackage) {
        Set<Operator> operators = processPackage.getOperators();
        List<Operator> originOperatorAllNodes = new ArrayList<>(operators);
        Set<ProcessPackage> processPackages = processPackage.getProcessPackages();
        if (processPackages != null && !processPackages.isEmpty()) {
            for (ProcessPackage pp : processPackages) {
                originOperatorAllNodes.addAll(getAllOperator(pp));
            }
        }
        return originOperatorAllNodes;
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
                                             | InstantiationException ex) {
                                        log.error(ex.getMessage());
                                    }
                                    return null;
                                },
                                Function.identity()));
    }

    public static Map<String, String> getUserDefinedFunctionMaps() {
        return getUserDefinedFunctionClassMaps().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, t -> t.getValue().getName()));
    }

    public static List<JsonNode> getOperatorConfigurations() {
        return OPERATOR_MAP.entrySet().stream()
                .map(
                        t -> {
                            Operator operator = InstantiationUtil.instantiate(t.getValue());
                            try {
                                ObjectNode result =  (ObjectNode)mapper.readTree(String.format(
                                        "{\"name\": \"%s\",%n" +
                                                "\"group\":\"%s\",%n" +
                                                "\"specification\": %s}",
                                        t.getKey(), operator.getGroup(), operator.getSpecification()));
                                JsonNode portsJsonNode = generateJsonPorts(operator);
                                result.set("ports", portsJsonNode);
                                return result;
                            } catch (JsonProcessingException e) {
                                log.error("getOperatorConfigurations error");
                                throw new RuntimeException(e);
                            }
                        })
                .collect(Collectors.toList());
    }

    private static JsonNode generateJsonPorts(Operator operator) {
        List<ObjectNode> inputIds = operator.getInputPorts().keySet().stream().map(k -> {
            ObjectNode inputPortNode = mapper.createObjectNode();
            inputPortNode.put("id", k);
            return inputPortNode;
        }).collect(Collectors.toList());
        ArrayNode inputPortsNode = mapper.createArrayNode();
        inputPortsNode.addAll(inputIds);

        List<ObjectNode> outputIds = operator.getOutputPorts().keySet().stream().map(k -> {
            ObjectNode outputPortNode = mapper.createObjectNode();
            outputPortNode.put("id", k);
            return outputPortNode;
        }).collect(Collectors.toList());
        ArrayNode outputPortsNode = mapper.createArrayNode();
        outputPortsNode.addAll(outputIds);

        ObjectNode rootPortNode = mapper.createObjectNode();
        rootPortNode.set("inputs", inputPortsNode);
        rootPortNode.set ("outputs", outputPortsNode);
        return rootPortNode;
    }
}
