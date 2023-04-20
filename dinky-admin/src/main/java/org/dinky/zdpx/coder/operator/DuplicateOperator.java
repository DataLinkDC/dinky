package org.dinky.zdpx.coder.operator;

import org.dinky.zdpx.coder.graph.InputPortObject;
import org.dinky.zdpx.coder.graph.OutputPortObject;
import org.dinky.zdpx.coder.graph.PseudoData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于端口数据复制, 转为多路输出
 */
public class DuplicateOperator extends Operator {

    @Override
    protected void initialize() {
        final InputPortObject<TableInfo> inputPortInfo = new InputPortObject<>(this, "input_0");
        inputPorts.add(inputPortInfo);

    }

    @Override
    protected void handleParameters(String parameters) {
        if (outputPorts.isEmpty() && this.operatorWrapper != null) {
            List<Map<String, Object>> outputInfo = getParameterLists(parameters);

            for (Map<String, Object> oi : outputInfo) {
                OutputPortObject<TableInfo> opi = new OutputPortObject<>(this, oi.get("outputName").toString());
                outputPorts.add(opi);
            }
        }
    }

    @Override
    protected Map<String, String> declareUdfFunction() {
//        return Map.of();
        return new HashMap<>();
    }

    @Override
    protected boolean applies() {
        return true;
    }

    @Override
    protected void execute() {

        PseudoData pseudoData = inputPorts.stream()
                .map(t -> t.getConnection().getFromPort().getPseudoData())
                .findAny()
                .orElse(null);
        outputPorts.forEach(t -> t.setPseudoData(pseudoData));
    }
}
