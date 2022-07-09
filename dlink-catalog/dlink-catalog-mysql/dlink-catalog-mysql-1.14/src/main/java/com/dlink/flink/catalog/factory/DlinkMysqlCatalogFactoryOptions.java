/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dlink.flink.catalog.factory;

import com.dlink.flink.catalog.DlinkMysqlCatalog;
import org.apache.flink.annotation.Internal;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.table.catalog.CommonCatalogOptions;
import org.apache.flink.table.catalog.exceptions.CatalogException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * {@link ConfigOption}s for {@link DlinkMysqlCatalog}.
 */
@Internal
public class DlinkMysqlCatalogFactoryOptions {

    public static final String IDENTIFIER = "dlink_mysql_catalog";

    public static final ConfigOption<String> USERNAME; // =
    // ConfigOptions.key("mysql-catalog-username").stringType().noDefaultValue();

    public static final ConfigOption<String> PASSWORD; // =
    // ConfigOptions.key("mysql-catalog-password").stringType().noDefaultValue();

    public static final ConfigOption<String> URL; // =
    //             ConfigOptions.key("mysql-catalog-url").stringType().noDefaultValue();

    public static final String prefix = "dlink-mysql-catalog";

    static {
        try {
            String configPath = System.getenv(ConfigConstants.ENV_FLINK_CONF_DIR);
            if (!configPath.endsWith("/")) {
                configPath = configPath + "/";
            }
            configPath = configPath + addPrefix(".properties");
            File propFile = new File(configPath);
            if (!propFile.exists()) {
                throw new CatalogException("配置文件不存在！");
            }
            InputStream is = new FileInputStream(propFile);
            Properties props = new Properties();
            props.load(is);
            String username = props.getProperty(addPrefix("-username"));
            USERNAME = ConfigOptions.key(addPrefix("-username"))
                    .stringType()
                    .defaultValue(username);

            String password = props.getProperty(addPrefix("-password"));
            PASSWORD = ConfigOptions.key(addPrefix("-password"))
                    .stringType()
                    .defaultValue(password);

            String url = props.getProperty(addPrefix("-url"));
            URL = ConfigOptions.key(addPrefix("-url"))
                    .stringType()
                    .defaultValue(url);
        } catch (Exception e) {
            throw new CatalogException("获取配置信息失败！", e);
        }
    }

    private static String addPrefix(String key) {
        return prefix + key;
    }

    private DlinkMysqlCatalogFactoryOptions() {
    }
}
