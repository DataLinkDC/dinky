package com.zdpx.coder.operator;

import com.zdpx.coder.Specifications;
import com.zdpx.coder.code.CodeJavaBuilder;
import com.zdpx.coder.utils.NameHelper;
import com.zdpx.coder.graph.OutputPortObject;
import com.zdpx.coder.graph.PseudoData;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TableConvertToDataStreamOperator extends Operator {

    private OutputPortObject<TableInfo> outputPortObject;

    @Override
    protected void initialize() {
        parameters.getParameterList().add(new Parameter(Specifications.TABLE_NAME));
        outputPortObject = registerOutputPort("output_0)");
        registerInputPort("input_0");
    }

    @Override
    protected Map<String, String> declareUdfFunction() {
        return new HashMap<>();
    }

    @Override
    protected boolean applies() {
        return true;
    }

    @Override
    protected void execute() {
        String tn = NameHelper.generateVariableName(parameters.getParameterByName(Specifications.TABLE_NAME));
        if (!(this.getSchemaUtil().getGenerateResult() instanceof CodeJavaBuilder)) {
            return;
        }

        CodeJavaBuilder gjr = (CodeJavaBuilder) this.getSchemaUtil().getGenerateResult();
        gjr.getCodeContext().getMain().addStatement("DataStream<Row> $2L = $1L.toDataStream($1L.sqlQuery(\"select * from $2L\"))",
                Specifications.TABLE_ENV, tn)
            .addCode(System.lineSeparator());

        PseudoData pseudoData = inputPorts.stream()
                .map(t -> t.getConnection().getFromPort().getPseudoData())
                .findAny()
                .orElse(null);

        outputPortObject.setPseudoData((TableInfo) pseudoData);
    }
}
