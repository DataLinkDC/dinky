package org.dinky.zdpx.coder.operator.mysql;


import org.dinky.zdpx.coder.graph.OutputPortObject;
import org.dinky.zdpx.coder.operator.TableInfo;
import org.dinky.zdpx.coder.utils.TableDataStreamConverter;
import org.dinky.zdpx.coder.utils.TemplateUtils;

import java.util.Map;



/**
 *
 */
public class MysqlSourceOperator extends MysqlTable {

    private OutputPortObject<TableInfo> outputPortObject;

    @Override
    protected void initialize() {
        outputPortObject = new OutputPortObject<>(this, "output_0");
        outputPorts.add(outputPortObject);
    }

    @Override
    protected void execute() {

        String sqlStr = TemplateUtils.format("Source", getDataModel(), TEMPLATE);
        this.getSchemaUtil().getGenerateResult().generate(sqlStr);

        Map<String, Object> parameters = getParameterLists().get(0);
        final TableInfo ti = TableDataStreamConverter.getTableInfo(parameters);
        ti.setName(generateTableName(ti.getName()));
        outputPortObject.setPseudoData(ti);
    }

    //endregion
}
