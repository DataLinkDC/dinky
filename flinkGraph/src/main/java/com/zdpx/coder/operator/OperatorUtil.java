package com.zdpx.coder.operator;

import com.zdpx.coder.graph.OutputPort;

import java.util.List;

public class OperatorUtil {
    protected static void postTableOutput(
            OutputPort<TableInfo> outputPortObject, String postTableName, List<Column> columns) {
        TableInfo ti = TableInfo.newBuilder().name(postTableName).columns(columns).build();
        outputPortObject.setPseudoData(ti);
    }
}
