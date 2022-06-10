package org.apache.flink.connector.phoenix.table;

import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

/**
 * PhoenixJdbcSinkFunction
 *
 * @author gy
 * @since 2022/3/17 17:41
 **/
public class PhoenixJdbcSinkFunction<T>  extends RichSinkFunction<T> implements CheckpointedFunction {

    @Override
    public void snapshotState(FunctionSnapshotContext functionSnapshotContext) throws Exception {

    }

    @Override
    public void initializeState(FunctionInitializationContext functionInitializationContext) throws Exception {

    }

    @Override
    public void invoke(T value) throws Exception {

    }

    @Override
    public void invoke(T value, Context context) throws Exception {

    }
}
