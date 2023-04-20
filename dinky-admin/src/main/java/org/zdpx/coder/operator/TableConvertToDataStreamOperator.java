package org.zdpx.coder.operator;

import org.zdpx.coder.code.CodeJavaBuilder;
import org.zdpx.coder.utils.NameHelper;
import org.zdpx.coder.graph.OutputPortObject;
import org.zdpx.coder.graph.PseudoData;

import java.util.HashMap;
import java.util.Map;

import static org.zdpx.coder.Specifications.TABLE_ENV;
import static org.zdpx.coder.Specifications.TABLE_NAME;

/**
 *
 */
public class TableConvertToDataStreamOperator extends Operator {

    private OutputPortObject<TableInfo> outputPortObject;

    @Override
    protected void initialize() {
        parameters.getParameterList().add(new Parameter(TABLE_NAME));
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
        String tn = NameHelper.generateVariableName(parameters.getParameterByName(TABLE_NAME));
        if (!(this.getSchemaUtil().getGenerateResult() instanceof CodeJavaBuilder)) {
            return;
        }

        CodeJavaBuilder gjr = (CodeJavaBuilder) this.getSchemaUtil().getGenerateResult();
        gjr.getCodeContext().getMain().addStatement("DataStream<Row> $2L = $1L.toDataStream($1L.sqlQuery(\"select * from $2L\"))",
                TABLE_ENV, tn)
            .addCode(System.lineSeparator());

        PseudoData pseudoData = inputPorts.stream()
                .map(t -> t.getConnection().getFromPort().getPseudoData())
                .findAny()
                .orElse(null);

        outputPortObject.setPseudoData((TableInfo) pseudoData);
    }
}
