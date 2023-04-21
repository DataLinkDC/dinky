package com.zdpx.udf;

import org.apache.flink.table.functions.AggregateFunction;

/**
 *
 */
public class CountFunction extends AggregateFunction<Long, CountFunction.MyAccumulator> implements IUdfDefine {

    @Override
    public MyAccumulator createAccumulator() {
        return new MyAccumulator();
    }

    public void accumulate(MyAccumulator accumulator, Integer i) {
        if (i != null) {
            accumulator.setCount(accumulator.count + i);
        }
    }

    @Override
    public Long getValue(MyAccumulator accumulator) {
        return accumulator.getCount();
    }

    @Override
    public String getUdfName() {
        return "count";
    }

    public static class MyAccumulator {
        private volatile long count = 0L;

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}
