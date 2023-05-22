package com.zdpx.coder.operator;

import java.util.List;

import com.zdpx.coder.graph.OutputPort;

public class OperatorUtil {
    public static void postTableOutput(
            OutputPort<TableInfo> outputPortObject, String postTableName, List<Column> columns) {
        TableInfo ti = TableInfo.newBuilder().name(postTableName).columns(columns).build();
        outputPortObject.setPseudoData(ti);
    }
}
