package org.dinky.trans.parse;

import org.junit.Test;


import static org.junit.jupiter.api.Assertions.*;

public class ExecuteJarParseStrategyTest {

    @Test
    public void testMatch() {
        ExecuteJarParseStrategy strategy = new ExecuteJarParseStrategy();
        assertTrue(strategy.match("EXECUTE JAR WITH ('jarPath' = 'hdfs://cluster1/user/flink/sql-client-test/test" +
                ".jar', 'className' = 'com.jd.dccs.sql.client.test.TestUDF')"));
        assertFalse(strategy.match("EXECUTE JAR WITH ('jarPath' = 'hdfs://cluster1/user/flink/sql-client-test/test" +
                ".jar', 'className' = 'com.jd.dccs.sql.client.test.TestUDF') ENDOFSQL"));
        assertTrue(strategy.match("EXECUTE  JAR  WITH  ('jarPath' = 'hdfs://cluster1/user/flink/sql-client-test/test" +
                ".jar', 'className' = 'com.jd.dccs.sql.client.test.TestUDF')"));
        assertTrue(strategy.match("EXECUTE  JAR  WITH  ( 'jarPath' = 'hdfs://cluster1/user/flink/sql-client-test/test" +
                ".jar', 'className' = 'com.jd.dccs.sql.client.test.TestUDF' )"));
        assertFalse(strategy.match("EXECUTE  JAR  WITH  ( 'jarPath' = 'hdfs://cluster1/user/flink/sql-client-test/test" +
                ".jar', 'className' = 'com.jd.dccs.sql.client.test.TestUDF' "));
        assertTrue(strategy.match("EXECUTE JAR WITH('jarPath' = 'hdfs://cluster1/user/flink/sql-client-test/test" +
                ".jar', 'className' = 'com.jd.dccs.sql.client.test.TestUDF')"));
    }

}