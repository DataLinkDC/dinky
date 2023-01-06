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

package com.dlink.app;

import com.dlink.app.db.DBConfig;
import com.dlink.app.flinksql.Submiter;
import com.dlink.assertion.Asserts;
import com.dlink.constant.FlinkParamConstant;
import com.dlink.utils.FlinkBaseUtil;

import java.io.IOException;
import java.util.Map;

/**
 * MainApp
 *
 * @author wenmo
 * @since 2021/10/27
 **/
public class MainApp {

    public static void main(String[] args) throws IOException {
        Map<String, String> params = FlinkBaseUtil.getParamsFromArgs(args);
        String id = params.get(FlinkParamConstant.ID);
        Asserts.checkNullString(id, "请配置入参 id ");
        DBConfig dbConfig = DBConfig.build(params);
        Submiter.submit(Integer.valueOf(id), dbConfig,params.get(FlinkParamConstant.DINKY_ADDR));
    }
}
