package com.zdpx.udf;

import org.apache.flink.table.functions.AggregateFunction;

/** */
public class CountHitAggFunction extends AggregateFunction<Long, CountFunction.MyAccumulator> implements IUdfDefine {
  @Override
  public Long getValue(CountFunction.MyAccumulator accumulator) {
    return accumulator.getCount();
  }

  @Override
  public CountFunction.MyAccumulator createAccumulator() {
    return new CountFunction.MyAccumulator();
  }

  public void accumulate(
          CountFunction.MyAccumulator accumulator, Double longitude, Double latitude) {
    if (longitude < 90 && latitude < 30) {
      accumulator.setCount(accumulator.getCount() + 1);
    }
  }

  @Override
  public String getUdfName() {
    return "countHit";
  }
}
