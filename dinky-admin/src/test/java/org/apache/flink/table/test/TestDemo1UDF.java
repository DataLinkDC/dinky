package org.apache.flink.table.test;

import org.apache.flink.table.test.utils.TestUtil;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Author: lwjhn
 * Date: 2023/10/19 14:05
 * Description:
 */

@Ignore
public class TestDemo1UDF {
    @Test
    public void test1() {
        TestUtil.executeSql("./sql/TestDemoUnnest.sql");
    }
}
