package org.dinky.zdpx.coder.operator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.table.functions.UserDefinedFunction;
import org.dinky.zdpx.coder.SceneCodeBuilder;
import org.dinky.zdpx.coder.graph.*;
import org.dinky.zdpx.coder.utils.JsonSchemaValidator;
import org.dinky.zdpx.coder.utils.Preconditions;
import org.reflections.Reflections;

import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 宏算子抽象类
 *
 * @author Licho Sun
 */
@Slf4j
public abstract class Operator implements Runnable, Identifier {
    public static final String FIELD_FUNCTIONS = "fieldFunctions";

    protected OperatorWrapper operatorWrapper;
    protected JsonSchemaValidator jsonSchemaValidator = new JsonSchemaValidator();
    protected Parameters parameters = new Parameters();
    @SuppressWarnings("rawtypes")
    protected List<InputPort> inputPorts = new ArrayList<>();
    @SuppressWarnings("rawtypes")
    protected List<OutputPort> outputPorts = new ArrayList<>();
    protected Map<String, String> userFunctions;

    private SceneCodeBuilder sceneCodeBuilder;

    @SuppressWarnings({"NullAway.Init", "PMD.UnnecessaryConstructor"})
    protected Operator() {
        this(null);
    }

    protected Operator(OperatorWrapper operatorWrapper) {
        this.operatorWrapper = operatorWrapper;
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
            final ObjectMapper objectMapper = new ObjectMapper();
            parametersLocal = objectMapper.readValue(parametersStr, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (JsonProcessingException e) {
            log.error(e.toString());

        }
        return parametersLocal;
    }

    @Override
    public void run() {
        if (Objects.isNull(operatorWrapper)) {
            log.error("{} operator not wrapper.", this.getClass().getName());
            return;
        }
        log.info(String.format("execute operator id %s", this.getOperatorWrapper().getId()));
        if (applies()) {
            validParameters();
            generateUdfFunctionByInner();
            execute();
        }
    }

    protected void registerUdfFunctions(List<FieldFunction> fieldFunctions) {
        fieldFunctions.stream().filter(t -> !Strings.isNullOrEmpty(t.getFunctionName()))
                .forEach(t -> registerUdfFunctions(t.getFunctionName()));
    }

    protected void registerUdfFunctions(String functionName) {
        sceneCodeBuilder.getUdfFunctionMap().entrySet().stream()
                .filter(t -> t.getKey().equals(functionName)).findAny().ifPresent(t -> {
                    this.getSchemaUtil().getGenerateResult().registerUdfFunction(t.getKey(), t.getValue());
                });
        sceneCodeBuilder.getUdfFunctionMap().remove(functionName);
    }

    protected void registerUdfFunctions(String functionName, String clazz) {
        this.getSchemaUtil().getGenerateResult().registerUdfFunction(functionName, clazz);
    }

    protected void generate(String sqlStr) {
        this.getSchemaUtil().getGenerateResult().generate(sqlStr);
    }

    /**
     * 校验输入参数是否正确.
     */
    protected void validParameters() {
        String parametersString = getParametersString();
        parametersString = parametersString.substring(parametersString.indexOf("[") + 1,
                parametersString.lastIndexOf("]"));
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

    /**
     * 初始化信息,输出/输入端口应该在该函数中完成注册定义
     */
    protected abstract void initialize();

    /**
     * 定义属性约束
     */
    protected String propertySchemaDefinition() {
        return null;
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

    /**
     * 逻辑执行函数
     */
    protected abstract void execute();

    protected void postOutput(OutputPortObject<TableInfo> outputPortObject, String postTableName,
                              List<Column> columns) {
        TableInfo ti = TableInfo.newBuilder().name(postTableName).columns(columns).build();
        outputPortObject.setPseudoData(ti);
    }

    /**
     * 注册输入端口
     *
     * @param name 端口名称
     * @return 输入端口
     */
    protected InputPortObject<TableInfo> registerInputPort(String name) {
        InputPortObject<TableInfo> inputPortObject = new InputPortObject<>(this, name);
        inputPorts.add(inputPortObject);
        return inputPortObject;
    }

    /**
     * 注册输出端口
     *
     * @param name 端口名称
     * @return 输出端口
     */
    protected OutputPortObject<TableInfo> registerOutputPort(String name) {
        OutputPortObject<TableInfo> outputPortObject = new OutputPortObject<>(this, name);
        outputPorts.add(outputPortObject);
        return outputPortObject;
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

        parametersLocal.forEach(p -> {
            for (Map.Entry<String, Object> entry : p.entrySet()) {
                Parameters ps = getParameters();
                ps.getParameterList().stream()
                        .filter(pp -> Objects.equals(pp.getKey(), entry.getKey()))
                        .findAny()
                        .ifPresent(pp -> {
                            pp.setKey(entry.getKey());
                            pp.setValue(entry.getValue());
                        });
            }
        });
    }

    /**
     * 设置宏算子参数的校验信息.
     */
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
        return getParameterLists(this.operatorWrapper.getParameters());
    }

    /**
     * 获取宏算子名称信息
     *
     * @return 宏算子名称
     */
    public String getName() {
        OperatorWrapper ow = getOperatorWrapper();
        Preconditions.checkNotNull(ow, String.format("%s miss OperatorWrapper.", this.getCode()));
        return ow.getName();
    }

    public String getParametersString() {
        return this.getOperatorWrapper().getParameters();
    }

    /**
     * 生成内部用户自定义函数(算子)对应的注册代码, 以便在flink sql中对其进行引用调用.
     */
    private void generateUdfFunctionByInner() {
        Map<String, String> ufs = this.getUserFunctions();
        if (ufs == null) {
            return;
        }

        Map<String, String> udfFunctions = getSchemaUtil().getUdfFunctionMap();
        Sets.difference(ufs.entrySet(), udfFunctions.entrySet()).forEach(u -> {
            this.getSchemaUtil().getGenerateResult().registerUdfFunction(u.getKey(), u.getValue());
        });
        udfFunctions.putAll(ufs);
    }

    public static JsonNode getNestValue(String json, String path) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            log.error("json not contains path:{}", path);
            return null;
        }

        return root.at(path);
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
    static List<FieldFunction> getFieldFunctions(String primaryTableName, Map<String, Object> parameters) {
        return FieldFunction.analyzeParameters(primaryTableName,
                (List<Map<String, Object>>) parameters.get(FIELD_FUNCTIONS));
    }

    public static Map<String, Object> getJsonAsMap(JsonNode inputs) {
        return new ObjectMapper().<Map<String, Object>>convertValue(inputs, new TypeReference<Map<String, Object>>() {
        });
    }
    public static List<Column> getColumnFromFieldFunctions(List<FieldFunction> ffs) {
        return ffs.stream()
                .map(t -> new Column(getColumnName(t.getOutName()), t.getOutType())).collect(Collectors.toList());
    }

    public static String getColumnName(String fullColumnName) {
        return fullColumnName.substring(fullColumnName.lastIndexOf('.') + 1);
    }

    //region g/s

    public Map<String, String> getUserFunctions() {
        return userFunctions;
    }

    public void setUserFunctions(Map<String, String> userFunctions) {
        this.userFunctions = userFunctions;
    }

    public OperatorWrapper getOperatorWrapper() {
        return operatorWrapper;
    }

    public void setOperatorWrapper(OperatorWrapper operatorWrapper) {
        this.operatorWrapper = operatorWrapper;
        handleParameters(operatorWrapper.getParameters());
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
        return operatorWrapper.equals(operator.operatorWrapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operatorWrapper);
    }

    @SuppressWarnings("rawtypes")
    public List<InputPort> getInputPorts() {
        return inputPorts;
    }

    @SuppressWarnings("rawtypes")
    public void setInputPorts(List<InputPort> inputPorts) {
        this.inputPorts = inputPorts;
    }

    @SuppressWarnings("rawtypes")
    public List<OutputPort> getOutputPorts() {
        return outputPorts;
    }

    @SuppressWarnings("rawtypes")
    public void setOutputPorts(List<OutputPort> outputPorts) {
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

    //endregion
}
