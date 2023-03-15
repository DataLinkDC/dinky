package org.dinky.executor;

import org.apache.flink.table.operations.Operation;

import java.util.List;

public interface CustomParser {
    List<Operation> parse(String statement);
}
