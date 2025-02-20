/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.utils;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;

/** DirUtilTest */
@Ignore
public class SqlUtilTest {

    @Test
    public void testRemoveNote() {
        String testSql = "/**\n"
                + "test1\n"
                + "*/\n"
                + "//test2\n"
                + "-- test3\n"
                + "--test4\n"
                + "select 1 --test5\n"
                + " from test # test9\n"
                + " where '1'  <> '-- ::.' //test6\n"
                + " and 1=1 --test7\n"
                + " and 'zz' <> null; /**test8*/";

        String removedNoteSql = SqlUtil.removeNote(testSql);
        Assertions.assertThat(removedNoteSql).isNotNull();
        Assertions.assertThat(removedNoteSql)
                .isEqualTo("//test2\n" + "\n"
                        + "\n"
                        + "select 1 \n"
                        + " from test # test9\n"
                        + " where '1'  <> '-- ::.' //test6\n"
                        + " and 1=1 \n"
                        + " and 'zz' <> null;");
    }

    @Test
    public void testMultiComment() {
        String sql = "set 'state.savepoints.dir' = 'hdfs://namenode:9000/tmp/checkpoint'; --ddd\n"
                + "set 'state.checkpoints.dir' = 'hdfs://namenode:9000/tmp/checkpoint'; --dd \n"
                + "create table abc ;\n";
        String[] statements = SqlUtil.getStatements(sql);
        Assertions.assertThat(statements.length).isEqualTo(3);
    }

    @Test
    public void testGetStatements() {
        String sql = "set 'key1' =  'value1';-- 同行注释1\n" +
                "-- 多行注释1\n" +
                " -- 多行注释2\n" +
                "-- 多行注释3\n" +
                "set 'key2' =  'value2'; -- 同行注释2\n" +
                "  -- 注释1\n" +
                "set 'key3' =  'value4'; -- 同行注释3 --同行注释4\n" +
                "-- 注释2\n" +
                ";\n" +
                "\n" +
                ";;\n" +
                "\n" +
                "\n" +
                "select \n" +
                "  * \n" +
                "   -- sql内注释1\n" +
                "   -- sql内注释2\n" +
                "  from table1 ; \n" +
                " select ';--' as `test1`,';  --' as `test2`,'; \n" +
                " --' as `test2`,* from table2;\n" +
                "select ' select * from tb1 ; \n" +
                "\n" +
                " select * from tb2; ' as sql_stmt;\n" +
                "-- 注释3\n" +
                "--注释4\n" +
                " --注释5\n" +
                "EXECUTE JAR WITH (\n" +
                "'uri'='a.jar',\n" +
                "'main-class'='realtime.app.DwdKafkaToDelay',\n" +
                "'args'='base64@LS1vZmZzZXQuaW5pdCBsYXRlc3Q=',\n" +
                "'allowNonRestoredState'='false'\n" +
                ");\n";
        String[] statements = SqlUtil.getStatements(sql);
        Assertions.assertThat(statements.length).isEqualTo(7);
        Assertions.assertThat(statements[0]).isEqualTo("set 'key1' =  'value1'");
        Assertions.assertThat(statements[1]).isEqualTo("set 'key2' =  'value2'");
        Assertions.assertThat(statements[2]).isEqualTo("set 'key3' =  'value4'");
        Assertions.assertThat(statements[3]).isEqualTo("select \n" +
                "  * \n" +
                "   \n" +
                "   \n" +
                "  from table1");
        Assertions.assertThat(statements[4]).isEqualTo("select ';--' as `test1`,';  --' as `test2`,'; \n" +
                " --' as `test2`,* from table2");
        Assertions.assertThat(statements[5]).isEqualTo("select ' select * from tb1 ; \n" +
                " select * from tb2; ' as sql_stmt");
        Assertions.assertThat(statements[6]).isEqualTo("EXECUTE JAR WITH (\n" +
                "'uri'='a.jar',\n" +
                "'main-class'='realtime.app.DwdKafkaToDelay',\n" +
                "'args'='base64@LS1vZmZzZXQuaW5pdCBsYXRlc3Q=',\n" +
                "'allowNonRestoredState'='false'\n" +
                ")");
    }
}
