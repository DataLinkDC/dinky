package com.javaudf;

import org.apache.flink.table.functions.ScalarFunction;

public class demo extends ScalarFunction {
    public String eval(String s) {
        return "this is java udf "+s;
    }
}
