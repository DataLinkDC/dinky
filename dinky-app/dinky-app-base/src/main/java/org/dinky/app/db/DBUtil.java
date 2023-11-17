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

package org.dinky.app.db;

import org.dinky.app.model.SysConfig;
import org.dinky.data.app.AppDatabase;
import org.dinky.data.app.AppParamConfig;
import org.dinky.data.app.AppTask;

import java.sql.SQLException;
import java.util.List;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.simple.SimpleDataSource;

/**
 * DBUtil
 *
 * @since 2021/10/27
 */
public class DBUtil {

    private static Db db;

    public static void init(AppParamConfig config) {
        db = Db.use(new SimpleDataSource(config.getUrl(), config.getUsername(), config.getPassword()));
    }

    public static AppTask getTask(int taskId) throws SQLException {
        Entity option = Entity.create("dinky_task").set("id", taskId).set("enabled", true);
        List<AppTask> entities = db.find(option, AppTask.class);
        if (entities.size() <= 0) {
            throw new IllegalArgumentException(
                    StrFormatter.format("The Task is not found: {}, please check! ", taskId));
        } else {
            return entities.get(0);
        }
    }

    public static String getDbSourceSQLStatement() throws SQLException {
        StringBuilder sb = new StringBuilder();
        Entity option = Entity.create("dinky_database").set("enabled", true);
        List<AppDatabase> entities = db.find(option, AppDatabase.class);
        for (AppDatabase entity : entities) {
            sb.append(entity.getName())
                    .append(":=")
                    .append(entity.getFlinkConfig())
                    .append("\n;\n");
        }
        return sb.toString();
    }

    public static List<SysConfig> getSysConfigList() throws SQLException {
        Entity option = Entity.create("dinky_sys_config");
        return db.find(option, SysConfig.class);
    }
}
