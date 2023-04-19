package org.dinky.zdpx.coder.operator;


import org.dinky.zdpx.coder.Specifications;
import org.dinky.zdpx.coder.utils.NameHelper;
import org.dinky.zdpx.coder.utils.TemplateUtils;
import org.dinky.zdpx.coder.graph.InputPortObject;
import org.dinky.zdpx.coder.graph.OutputPortObject;
import org.dinky.zdpx.coder.graph.Scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 单表通用函数处理类, 根据配置定义的对每个字段的函数处理方式, 生成相应的sql语句
 *
 * @author Licho Sun
 */
public class CommSelectFunctionOperator extends Operator {
    public static final String TEMPLATE =
        String.format("<#import \"%s\" as e>CREATE VIEW ${tableName} AS SELECT <@e.fieldsProcess fieldFunctions/> FROM ${inputTableName} <#if where??>WHERE ${where}</#if>",
            Specifications.TEMPLATE_FILE);

    /**
     * 函数字段名常量
     */
    public static final String WHERE = "where";
    private InputPortObject<TableInfo> inputPortObject;
    private OutputPortObject<TableInfo> outputPortObject;

    @Override
    protected void initialize() {
        inputPortObject = registerInputPort("input_0");
        outputPortObject = registerOutputPort("output_0");
    }

    @Override
    protected Map<String, String> declareUdfFunction() {
        Map<String, Object> parameters = getParameterLists().get(0);
        List<FieldFunction> ffs = getFieldFunctions(null, parameters);
        List<String> functions = ffs.stream().map(FieldFunction::getFunctionName).collect(Collectors.toList());
        return Scene.getUserDefinedFunctionMaps().entrySet().stream()
            .filter(k -> functions.contains(k.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    protected boolean applies() {
        return true;
    }

    @Override
    protected void execute() {
        Map<String, Object> parameters = getFirstParameterMap();
        String outputTableName = NameHelper.generateVariableName("CommSelectFunctionResult");

        final String tableName = inputPortObject.getOutputPseudoData().getName();
        List<FieldFunction> ffs = getFieldFunctions(tableName, parameters);
        Map<String, Object> p = new HashMap<>();
        p.put("tableName", outputTableName);
        p.put(FIELD_FUNCTIONS, ffs);
        p.put("inputTableName", tableName);
        p.put(WHERE, parameters.get(WHERE));
        String sqlStr = TemplateUtils.format("CommSelectFunction", p, TEMPLATE);

        this.getSchemaUtil().getGenerateResult().generate(sqlStr);

        postOutput(outputPortObject, outputTableName, Specifications.convertFieldFunctionToColumns(ffs));
    }

}
