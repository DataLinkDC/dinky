package com.zdpx.udf;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.functions.TableFunction;

/** */
public class Split extends TableFunction<Tuple2<String, Integer>> implements IUdfDefine {
  private static final String SEPARATOR = " ";

  public void eval(String str) {
    for (String s : str.split(SEPARATOR)) {
      collect(new Tuple2<>(s, s.length()));
    }
  }

  @Override
  public String getUdfName() {
    return "split";
  }
}
