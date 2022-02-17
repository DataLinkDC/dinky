package com.dlink.trans;

import com.dlink.executor.Executor;
import org.apache.flink.table.api.TableResult;

/**
 * Operation
 *
 * @author wenmo
 * @since 2021/6/13 19:24
 */
public interface Operation {

    String getHandle();

    Operation create(String statement);

    TableResult build(Executor executor);

    boolean noExecute();
}
