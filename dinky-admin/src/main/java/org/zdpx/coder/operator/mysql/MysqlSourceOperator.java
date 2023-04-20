package org.zdpx.coder.operator.mysql;


import org.zdpx.coder.graph.OutputPortObject;
import org.zdpx.coder.operator.TableInfo;
import org.zdpx.coder.utils.TableDataStreamConverter;
import org.zdpx.coder.utils.TemplateUtils;

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
