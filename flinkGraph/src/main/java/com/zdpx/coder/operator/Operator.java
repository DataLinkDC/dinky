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

package com.zdpx.coder.operator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.table.functions.UserDefinedFunction;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.reflections.Reflections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import com.zdpx.coder.SceneCodeBuilder;
import com.zdpx.coder.graph.InputPort;
import com.zdpx.coder.graph.InputPortObject;
import com.zdpx.coder.graph.Node;
import com.zdpx.coder.graph.NodeWrapper;
import com.zdpx.coder.graph.OutputPort;
import com.zdpx.coder.graph.OutputPortObject;
import com.zdpx.coder.graph.PseudoData;
import com.zdpx.coder.utils.JsonSchemaValidator;

import lombok.extern.slf4j.Slf4j;

/**
 * 宏算子抽象类
 *
 * @author Licho Sun
 */
@Slf4j
public abstract class Operator extends Node implements Runnable {
    public static final String FIELD_FUNCTIONS = "fieldFunctions";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, InputPort<? extends PseudoData<?>>> inputPorts = new HashMap<>();

    private Map<String, OutputPort<? extends PseudoData<?>>> outputPorts = new HashMap<>();

    protected Map<String, String> userFunctions;
    private SceneCodeBuilder sceneCodeBuilder;

    protected JsonSchemaValidator jsonSchemaValidator = new JsonSchemaValidator();

    @SuppressWarnings({"NullAway.Init", "PMD.UnnecessaryConstructor"})
    protected Operator() {
        this(null);
    }

    protected Operator(NodeWrapper nodeWrapper) {
        this.nodeWrapper = nodeWrapper;
        initialize();
        definePropertySchema();
    }

    /**
     * 获取所有{@link Operator}子类, 即所有节点定义信息
     *
     * @return 节点定义集合
     */
    public static Set<Class<? extends Operator>> getAllOperatorGenerators() {
        Reflections reflections = new Reflections(Operator.class.getPackage().getName());
        return reflections.getSubTypesOf(Operator.class);
    }

    protected static List<Map<String, Object>> getParameterLists(@Nullable String parametersStr) {
        List<Map<String, Object>> parametersLocal = Collections.emptyList();
        if (Strings.isNullOrEmpty(parametersStr)) {
            return parametersLocal;
        }

        try {
            parametersLocal = objectMapper.readValue(parametersStr, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error(e.toString());
        }
        return parametersLocal;
    }

    @Override
    public void run() {
        if (Objects.isNull(nodeWrapper)) {
            log.error("{} operator not wrapper.", this.getClass().getName());
            return;
        }
        log.info(String.format("execute operator id %s", this.getId()));
        if (applies()) {
            validParameters();
            generateUdfFunctionByInner();
            execute();
        }
    }

    protected void registerUdfFunctions(List<FieldFunction> fieldFunctions) {
        fieldFunctions.stream()
                .filter(t -> !Strings.isNullOrEmpty(t.getFunctionName()))
                .forEach(t -> registerUdfFunctions(t.getFunctionName()));
    }

    protected void registerUdfFunctions(String functionName) {
        sceneCodeBuilder.getUdfFunctionMap().entrySet().stream()
                .filter(t -> t.getKey().equals(functionName))
                .findAny()
                .ifPresent(
                        t -> this.getSchemaUtil()
                                .getGenerateResult()
                                .registerUdfFunction(t.getKey(), t.getValue()));
        sceneCodeBuilder.getUdfFunctionMap().remove(functionName);
    }

    protected void registerUdfFunctions(String functionName, String clazz) {
        this.getSchemaUtil().getGenerateResult().registerUdfFunction(functionName, clazz);
    }

    protected void generate(String sqlStr) {
        this.getSchemaUtil().getGenerateResult().generate(sqlStr);
    }

    /** 校验输入参数是否正确. */
    protected void validParameters() {
        String parametersString = getParametersString();
        if (jsonSchemaValidator.getSchema() == null) {
            log.warn("{} operator not parameter validation schema.", this.getName());
            return;
        }

        Set<ValidationMessage> validationMessages = jsonSchemaValidator.validate(parametersString);
        if (!CollectionUtils.isEmpty(validationMessages)) {
            for (ValidationMessage validationMessage : validationMessages) {
                log.error("{} operator parameters error: {}", this.getName(), validationMessage);
            }
            throw new InvalidParameterException(validationMessages.toString());
        }
    }

    /** 初始化信息,输出/输入端口应该在该函数中完成注册定义 */
    protected abstract void initialize();

    /** 定义属性约束 */
    protected String propertySchemaDefinition() {
        return null;
    }

    @Override
    public String getSpecification() {
        return propertySchemaDefinition();
    }

    /**
     * 声明用到的{@link UserDefinedFunction}自定义函数, 初始化时会根据实际声明情况生成注册到flink的代码
     *
     * @return 自定义函数集
     */
    protected abstract Map<String, String> declareUdfFunction();

    /**
     * 该算子逻辑是否执行
     *
     * @return 是否执行
     */
    protected abstract boolean applies();

    protected Map<String, Object> getFirstParameterMap() {
        return getParameterLists().get(0);
    }

    /** 逻辑执行函数 */
    protected abstract void execute();

    /**
     * 注册输入端口
     *
     * @param name 端口名称
     * @return 输入端口
     */
    protected <S extends PseudoData<S>, T extends InputPort<S>> T registerInputPort(
            String name, BiFunction<Operator, String, T> constructor) {
        final T portObject = constructor.apply(this, name);
        inputPorts.put(name, portObject);
        return portObject;
    }

    protected <T extends PseudoData<T>> InputPortObject<T> registerInputObjectPort(String name) {
        return registerInputPort(name, InputPortObject<T>::new);
    }

    /**
     * 注册输出端口
     *
     * @param name 端口名称
     * @return 输出端口
     */
    protected <S extends PseudoData<S>, T extends OutputPort<S>> T registerOutputPort(
            String name, BiFunction<Operator, String, T> constructor) {
        final T portObject = constructor.apply(this, name);
        outputPorts.put(name, portObject);
        return portObject;
    }

    protected <T extends PseudoData<T>> OutputPortObject<T> registerOutputObjectPort(String name) {
        return registerOutputPort(name, OutputPortObject<T>::new);
    }

    /**
     * 根据场景配置文件参数内容, 转换为计算图的参数形式
     *
     * @param parametersStr 配置文件参数内容
     */
    protected void handleParameters(@Nullable String parametersStr) {
        List<Map<String, Object>> parametersLocal = getParameterLists(parametersStr);

        if (CollectionUtils.isEmpty(parametersLocal)) {
            return;
        }

        parametersLocal.forEach(
                p -> {
                    for (Map.Entry<String, Object> entry : p.entrySet()) {
                        Parameters ps = getParameters();
                        ps.getParameterList().stream()
                                .filter(pp -> Objects.equals(pp.getKey(), entry.getKey()))
                                .findAny()
                                .ifPresent(
                                        pp -> {
                                            pp.setKey(entry.getKey());
                                            pp.setValue(entry.getValue());
                                        });
                    }
                });
    }

    /** 设置宏算子参数的校验信息. */
    protected void definePropertySchema() {
        String propertySchema = propertySchemaDefinition();
        if (Strings.isNullOrEmpty(propertySchema)) {
            log.debug("operator {} don't have schema file.", this.getClass().getSimpleName());
            return;
        }
        JsonSchema schema = jsonSchemaValidator.getJsonSchemaFromStringContent(propertySchema);
        jsonSchemaValidator.setSchema(schema);
    }

    /**
     * 获取宏算子参数
     *
     * @return 非结构化参数信息
     */
    protected List<Map<String, Object>> getParameterLists() {
        return getParameterLists(this.nodeWrapper.getParameters());
    }

    public String getParametersString() {
        return this.getOperatorWrapper().getParameters();
    }

    /** 生成内部用户自定义函数(算子)对应的注册代码, 以便在flink sql中对其进行引用调用. */
    private void generateUdfFunctionByInner() {
        Map<String, String> ufs = this.getUserFunctions();
        if (ufs == null) {
            return;
        }

        Map<String, String> udfFunctions = getSchemaUtil().getUdfFunctionMap();
        Sets.difference(ufs.entrySet(), udfFunctions.entrySet())
                .forEach(
                        u ->
                                this.getSchemaUtil()
                                        .getGenerateResult()
                                        .registerUdfFunction(u.getKey(), u.getValue()));
        udfFunctions.putAll(ufs);
    }

    public static JsonNode getNestValue(Map<String, Object> maps, String path) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        root = mapper.valueToTree(maps);
        return root.at(path);
    }

    public static Map<String, Object> getNestMapValue(Map<String, Object> maps, String path) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        root = mapper.valueToTree(maps);
        return getJsonAsMap(root.at(path));
    }

    @SuppressWarnings("unchecked")
    public static List<FieldFunction> getFieldFunctions(
            String primaryTableName, Map<String, Object> parameters) {
        return FieldFunction.analyzeParameters(
                primaryTableName, (List<Map<String, Object>>) parameters.get(FIELD_FUNCTIONS));
    }

    public static Map<String, Object> getJsonAsMap(JsonNode inputs) {
        return new ObjectMapper().convertValue(inputs, new TypeReference<>() {});
    }

    public static List<Column> getColumnFromFieldFunctions(List<FieldFunction> ffs) {
        return ffs.stream()
                .map(t -> new Column(getColumnName(t.getOutName()), t.getOutType()))
                .collect(Collectors.toList());
    }

    public static String getColumnName(String fullColumnName) {
        return fullColumnName.substring(fullColumnName.lastIndexOf('.') + 1);
    }

    // region g/s

    public Map<String, String> getUserFunctions() {
        return userFunctions;
    }

    public void setUserFunctions(Map<String, String> userFunctions) {
        this.userFunctions = userFunctions;
    }

    public NodeWrapper getOperatorWrapper() {
        return nodeWrapper;
    }

    public void setOperatorWrapper(NodeWrapper originNodeWrapper) {
        if (originNodeWrapper == null) {
            return;
        }
        super.setNodeWrapper(originNodeWrapper);
        handleParameters(originNodeWrapper.getParameters());
        setUserFunctions(declareUdfFunction());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Operator operator = (Operator) o;
        return nodeWrapper.equals(operator.nodeWrapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeWrapper);
    }

    public Map<String, InputPort<? extends PseudoData<?>>> getInputPorts() {
        return inputPorts;
    }

    public void setInputPorts(Map<String, InputPort<? extends PseudoData<?>>> inputPorts) {
        this.inputPorts = inputPorts;
    }

    public Map<String, OutputPort<? extends PseudoData<?>>> getOutputPorts() {
        return outputPorts;
    }

    public void setOutputPorts(Map<String, OutputPort<? extends PseudoData<?>>> outputPorts) {
        this.outputPorts = outputPorts;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public SceneCodeBuilder getSchemaUtil() {
        return sceneCodeBuilder;
    }

    public void setSchemaUtil(SceneCodeBuilder sceneCodeBuilder) {
        this.sceneCodeBuilder = sceneCodeBuilder;
    }

    // endregion
}
