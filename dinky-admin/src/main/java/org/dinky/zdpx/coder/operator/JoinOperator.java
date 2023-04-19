package org.dinky.zdpx.coder.operator;

import lombok.extern.slf4j.Slf4j;
import org.dinky.zdpx.coder.Specifications;
import org.dinky.zdpx.coder.utils.NameHelper;
import org.dinky.zdpx.coder.utils.TemplateUtils;
import org.dinky.zdpx.coder.graph.InputPortObject;
import org.dinky.zdpx.coder.graph.OutputPortObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Slf4j
public class JoinOperator extends Operator {
    public static final String TEMPLATE =
            String.format("<#import \"%s\" as e>CREATE VIEW ${tableName} AS " +
                            "SELECT <@e.fieldsProcess fieldFunctions/> " +
                            "FROM ${inputTableName} " +
                            "${joinType?upper_case} JOIN ${anotherTableName} " +
                            "<#if systemTimeColumn??>FOR SYSTEM_TIME AS OF ${systemTimeColumn}</#if> " +
                            "<#if where??>WHERE ${where}</#if> " +
                            "<#if onLeftColumn??>ON ${onLeftColumn} = ${onRightColumn}</#if>",
                    Specifications.TEMPLATE_FILE);

    private InputPortObject<TableInfo> primaryInput;
    private InputPortObject<TableInfo> secondInput;
    private OutputPortObject<TableInfo> outputPort;

    @Override
    protected void initialize() {
        primaryInput = registerInputPort("primaryInput");
        secondInput = registerInputPort("secondInput");
        outputPort = registerOutputPort("output_0");
    }

    @Override
    protected Map<String, String> declareUdfFunction() {
        return Map.of();
    }

    @Override
    protected boolean applies() {
        return true;
    }

    @Override
    protected void execute() {
        if (outputPorts.isEmpty() || this.operatorWrapper == null) {
            log.error("JoinOperator information err.");
            return;
        }

        Map<String, Object> parameters = getFirstParameterMap();
        String joinType = getNestValue(parameters, "/join/type").textValue();
        String forSystemTime = getNestValue(parameters, "/systemTimeColumn").textValue();
        String onLeftColumn = getNestValue(parameters, "/on/leftColumn").textValue();
        String onRightColumn = getNestValue(parameters, "/on/rightColumn").textValue();

        String outputTableName = NameHelper.generateVariableName("JoinOperator");
        String primaryTableName = primaryInput.getOutputPseudoData().getName();
        String secondTableName = secondInput.getOutputPseudoData().getName();
        List<FieldFunction> ffsPrimary = getFieldFunctions(primaryTableName, getNestMapValue(parameters,
                "/primaryInput"));
        List<FieldFunction> ffsSecond = getFieldFunctions(secondTableName, getNestMapValue(parameters, "/secondInput"));
        ffsPrimary.addAll(ffsSecond);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("tableName", outputTableName);
        dataModel.put("inputTableName", primaryTableName);
        dataModel.put("anotherTableName", secondTableName);
        dataModel.put(FIELD_FUNCTIONS, ffsPrimary);
        dataModel.put("joinType", joinType);
        dataModel.put("systemTimeColumn", FieldFunction.insertTableName(primaryTableName, null, forSystemTime));
        dataModel.put("onLeftColumn", FieldFunction.insertTableName(primaryTableName, null, onLeftColumn));
        dataModel.put("onRightColumn", FieldFunction.insertTableName(secondTableName, null, onRightColumn));

        String sqlStr = TemplateUtils.format(this.getName(), dataModel, TEMPLATE);
        registerUdfFunctions(ffsPrimary);

        List<Column> cls = getColumnFromFieldFunctions(ffsPrimary);
        generate(sqlStr);

        postOutput(outputPort, outputTableName, cls);
    }

}
