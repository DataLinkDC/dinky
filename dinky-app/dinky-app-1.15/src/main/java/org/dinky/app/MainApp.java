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

package org.dinky.app;

import org.dinky.app.constant.AppParamConstant;
import org.dinky.app.db.DBUtil;
import org.dinky.app.flinksql.Submitter;
import org.dinky.app.util.FlinkAppUtil;
import org.dinky.data.app.AppParamConfig;
import org.dinky.utils.JsonUtils;

import org.apache.flink.api.java.utils.ParameterTool;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MainApp
 *
 * @since 2022/11/05
 */
public class MainApp {

    private static final Logger log = LoggerFactory.getLogger(Submitter.class);

    public static void main(String[] args) throws Exception {
        log.info("=========================Start run dinky app job===============================");
        ParameterTool parameters = ParameterTool.fromArgs(args);
        boolean isEncrypt = parameters.getBoolean(AppParamConstant.isEncrypt, true);
        String config = parameters.get(AppParamConstant.config);
        config = isEncrypt ? new String(Base64.getDecoder().decode(config)) : config;
        AppParamConfig appConfig = JsonUtils.toJavaBean(config, AppParamConfig.class);
        try {
            log.info("dinky app is Ready to run, config is {}", appConfig);
            DBUtil.init(appConfig);
            Submitter.submit(appConfig);
        } catch (Exception e) {
            log.error("exectue app failed : ", e);
        } finally {
            log.info("Start Monitor Job");
            FlinkAppUtil.monitorFlinkTask(Submitter.executor, appConfig.getTaskId());
        }
    }
}
