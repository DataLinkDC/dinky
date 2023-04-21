package com.zdpx.udf;

import org.apache.flink.table.functions.ScalarFunction;

/**
 *
 */
public class NTimes extends ScalarFunction implements IUdfDefine {

    public long eval(Long input, int n) {
        return input * n;
    }

    public double eval(Double input, int n) {
        return input * n;
    }

    public String eval(String input, int n) {
        return String.valueOf(Double.parseDouble(input) * n);
    }

    @Override
    public String getUdfName() {
        return "ntimes";
    }
}
