package com.zdpx.coder;

import org.apache.flink.calcite.shaded.com.google.common.base.CaseFormat;

/**
 *
 * The features that may be code generated.
 * @author licho
 * */
public enum Feature {
  ENVIRONMENT,
  SOURCES,
  OPERATOR,
  OPERATOR_SQL,
  SINKS;

  public static String makeEnumName(String enumName) {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, enumName);
  }
}
