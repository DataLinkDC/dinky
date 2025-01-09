package org.dinky.admin

import org.apache.calcite.tools.RuleSets
import org.apache.flink.table.planner.plan.rules.FlinkStreamRuleSets
import org.junit.Assert._
import org.junit.Test
import scala.collection.JavaConverters._


class FlinkStreamRuleSetsTest {

  @Test
  def testRuleSetLoading(): Unit = {
    try {
      // 加载一个示例 RuleSet
      val ruleSet = FlinkStreamRuleSets.DEFAULT_REWRITE_RULES

      // 验证规则数量
      assertNotNull("RuleSet should not be null", ruleSet)
      val rules = ruleSet.iterator().asScala.toList
      assertTrue("RuleSet should contain rules", rules.nonEmpty)

      // 打印规则的名称
      println("Loaded RuleSet with the following rules:")
      rules.foreach(rule => println(rule.getClass.getName))

    } catch {
      case e: Exception =>
        // 打印错误信息
        e.printStackTrace()
        fail(s"Failed to load RuleSet: ${e.getMessage}")
    }
  }
}
