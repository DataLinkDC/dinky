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

package org.dinky.controller;

import org.dinky.data.dto.TaskDTO;
import org.dinky.data.model.JarSubmitParam;
import org.dinky.data.result.Result;
import org.dinky.data.vo.FlinkJarSqlConvertVO;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

@Ignore
class TaskControllerTest {

    @Test
    void testFlinkJarSqlConvertForm() {
        TaskController taskController = new TaskController(null);
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setStatement("set 'taskmanager.memory.process.size'='4000m';\n" + "-- 注释2\n"
                + "-- 注释3\n"
                + "-- 注释\n"
                + "aaa\n"
                + "\n"
                + "EXECUTE JAR WITH (\n"
                + "'uri'='url1',\n"
                + "'main-class'='class1',\n"
                + "'args'='arg1',\n"
                + "'allowNonRestoredState'='false'\n"
                + ");\n"
                + "\n"
                + "EXECUTE JAR WITH (\n"
                + "'uri'='rs:/dwd/realtime-dwd-delay-1.0-SNAPSHOT.jar',\n"
                + "'main-class'='realtime.app.DwdKafkaToDelay',\n"
                + "'args'='base64@LS1rZXkxIHZhbHVlMSApOycgIg==',\n"
                + "'allowNonRestoredState'='false'\n"
                + ");\n");
        Result<FlinkJarSqlConvertVO> result = taskController.flinkJarSqlConvertForm(taskDTO);

        String expectedInitSqlStatement =
                "set 'taskmanager.memory.process.size'='4000m';\n" + "-- 注释2\n" + "-- 注释3\n" + "-- 注释\n" + "aaa\n";
        String expectedJarSubmitParam = JarSubmitParam.build(
                        "EXECUTE JAR WITH (\n" + "'uri'='rs:/dwd/realtime-dwd-delay-1.0-SNAPSHOT.jar',\n"
                                + "'main-class'='realtime.app.DwdKafkaToDelay',\n"
                                + "'args'='base64@LS1rZXkxIHZhbHVlMSApOycgIg==',\n"
                                + "'allowNonRestoredState'='false'\n"
                                + ");\n")
                .toString();
        Assertions.assertThat(result.getData().getInitSqlStatement()).isEqualTo(expectedInitSqlStatement);
        Assertions.assertThat(result.getData().getJarSubmitParam().toString()).isEqualTo(expectedJarSubmitParam);
    }

    @Test
    void testFlinkJarFormConvertSql() {
        TaskController taskController = new TaskController(null);
        FlinkJarSqlConvertVO flinkJarSqlConvertVO = new FlinkJarSqlConvertVO();

        flinkJarSqlConvertVO.setInitSqlStatement("set 'taskmanager.memory.process.size'='4000m';\n" + "-- 注释2\n"
                + "-- 注释3\n"
                + "-- 注释\n"
                + "aaa\n"
                + "\n"
                + "EXECUTE JAR WITH (\n"
                + "'uri'='url1',\n"
                + "'main-class'='class1',\n"
                + "'args'='arg1',\n"
                + "'allowNonRestoredState'='false'\n"
                + ");\n"
                + "\n");
        flinkJarSqlConvertVO.setJarSubmitParam(
                JarSubmitParam.build("EXECUTE JAR WITH (\n" + "'uri'='rs:/dwd/realtime-dwd-delay-1.0-SNAPSHOT.jar',\n"
                        + "'main-class'='realtime.app.DwdKafkaToDelay',\n"
                        + "'args'='base64@LS1rZXkxIHZhbHVlMSApOycgIg==',\n"
                        + "'allowNonRestoredState'='false'\n"
                        + ");\n"));

        Result<String> result = taskController.flinkJarFormConvertSql(flinkJarSqlConvertVO);
        String expected = "set 'taskmanager.memory.process.size'='4000m';\n" + "-- 注释2\n"
                + "-- 注释3\n"
                + "-- 注释\n"
                + "aaa\n"
                + "\n"
                + "EXECUTE JAR WITH (\r\n"
                + "'uri'='rs:/dwd/realtime-dwd-delay-1.0-SNAPSHOT.jar',\r\n"
                + "'main-class'='realtime.app.DwdKafkaToDelay',\r\n"
                + "'args'='base64@LS1rZXkxIHZhbHVlMSApOycgIg==',\r\n"
                + "'allowNonRestoredState'='false'\r\n"
                + ");\r\n";
        Assertions.assertThat(result.getData()).isEqualTo(expected);
    }
}
