package org.dinky.zdpx.coder.operator.mysql;

import lombok.extern.slf4j.Slf4j;
import org.dinky.zdpx.coder.graph.InputPortObject;
import org.dinky.zdpx.coder.operator.TableInfo;
import org.dinky.zdpx.coder.utils.TemplateUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Slf4j
public class MysqlSinkOperator extends MysqlTable {

    private InputPortObject<TableInfo> inputPortObject;

    @Override
    protected void initialize() {
        inputPortObject = new InputPortObject<>(this, "input_0");
        inputPorts.add(inputPortObject);
    }

    @Override
    protected void execute() {
        Map<String, Object> dataModel = getDataModel();
        String sqlStr = TemplateUtils.format("sink", dataModel, TEMPLATE);

        this.getSchemaUtil().getGenerateResult().generate(sqlStr);

        String sql = String.format(
            "INSERT INTO ${tableName} (<#list tableInfo.columns as column>${column.name}<#sep>,</#sep></#list>) SELECT <#list tableInfo.columns as column>${column.name}<#sep>, </#list> FROM ${tableInfo.name}");

        TableInfo pseudoData = inputPortObject.getOutputPseudoData();
        if (pseudoData == null) {
            log.warn("{} input table info empty error.", getName());
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("tableName", dataModel.get("tableName"));
        data.put("tableInfo", pseudoData);
        String insertSqlStr = TemplateUtils.format("insert", data, sql);
        this.getSchemaUtil().getGenerateResult().generate(insertSqlStr);

    }

}
