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

package org.dinky.trans;

import org.dinky.trans.dml.ExecuteJarOperation;
import org.dinky.trans.parse.ExecuteJarParseStrategy;

import org.junit.jupiter.api.Test;

import cn.hutool.core.lang.Assert;

class ExecuteJarParseStrategyTest {

    @Test
    void getInfo() {
        String statement = "EXECUTE JAR WITH ( \n"
                + "'uri'='file:///C:/Users/ZackYoung/Downloads/paimon-flink-1.16-0.5-20230818.001833-127.jar',\n"
                + "'main-class'='org.apache.paimon.flink.action.FlinkActions',\n"
                + "'args'='mysql-sync-table --warehouse hdfs://10.8.16.157:9000/save --database cdc-test --table cdc_test1 --primary-keys id --mysql-conf hostname=121.5.136.161 --mysql-conf port=3371 --mysql-conf username=root --mysql-conf password=dinky --mysql-conf database-name=cdc-test --mysql-conf table-name=table_1 --mysql-conf server-time-zone=Asia/Shanghai --table-conf bucket=4 --table-conf changelog-producer=input --table-conf sink.parallelism=1',\n"
                + "'parallelism'='',\n"
                + "'savepoint-path'='' \n"
                + ")";
        ExecuteJarOperation.JarSubmitParam submitParam = ExecuteJarParseStrategy.getInfo(statement);
        Assert.notBlank(submitParam.getUri());
        Assert.notBlank(submitParam.getMainClass());
    }
}
