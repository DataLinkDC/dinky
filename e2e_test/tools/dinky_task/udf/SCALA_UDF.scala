package com.scala.udf;

import org.apache.flink.table.api._
import org.apache.flink.table.functions.ScalarFunction

// 定义可参数化的函数逻辑
class demo extends ScalarFunction {
  def eval(s: String, begin: Integer, end: Integer): String = {
    "this is scala"+s
  }
}
