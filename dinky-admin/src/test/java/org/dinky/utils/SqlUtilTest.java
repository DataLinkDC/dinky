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

import static org.dinky.data.enums.Status.SYS_FLINK_SETTINGS_SQLSEPARATOR;

import org.dinky.data.model.SystemConfiguration;

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
    public void getStatements() {
        String sql = "set 'state.savepoints.dir' = 'hdfs://namenode:9000/tmp/checkpoint'; --ddd\n"
                + "set 'state.checkpoints.dir' = 'hdfs://namenode:9000/tmp/checkpoint'; --dd \n"
                + "create table abc ;\n";
        SystemConfiguration.getInstances()
                .setConfiguration(SYS_FLINK_SETTINGS_SQLSEPARATOR.getKey(), ";\\s*(?:\\n|--.*)");
        String[] statements = SqlUtil.getStatements(sql);
        Assertions.assertThat(statements.length).isEqualTo(3);
    }
}
