package com.zdpx.udf;

import org.apache.flink.table.functions.AggregateFunction;
import org.apache.flink.table.functions.FunctionKind;
import org.apache.flink.table.functions.ScalarFunction;

/**
 *
 */
public interface IUdfDefine {
    default String getUdfName() {
        return getClass().getSimpleName();
    }

    default FunctionKind getFunctionType() {
        if (ScalarFunction.class.isAssignableFrom(this.getClass())) {
            return FunctionKind.SCALAR;
        } else if (AggregateFunction.class.isAssignableFrom(this.getClass())) {
            return FunctionKind.AGGREGATE;
        } else {
            return FunctionKind.OTHER;
        }
    }
}
