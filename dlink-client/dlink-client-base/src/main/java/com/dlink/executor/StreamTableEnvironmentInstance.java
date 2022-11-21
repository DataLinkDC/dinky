package com.dlink.executor;

import org.apache.flink.table.api.TableEnvironment;

/**
 *
 */
public interface StreamTableEnvironmentInstance {

    TableEnvironment getTableEnvironment();
}
