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

package com.dlink.flink.catalog.factory;

import com.dlink.flink.catalog.DlinkMysqlCatalog;

import org.apache.flink.annotation.Internal;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;

/**
 * {@link ConfigOption}s for {@link DlinkMysqlCatalog}.
 */
@Internal
public class DlinkMysqlCatalogFactoryOptions {

    public static final String IDENTIFIER = "dlink_mysql";

    public static final ConfigOption<String> USERNAME = ConfigOptions.key("username").stringType().noDefaultValue();

    public static final ConfigOption<String> PASSWORD = ConfigOptions.key("password").stringType().noDefaultValue();

    public static final ConfigOption<String> URL = ConfigOptions.key("url").stringType().noDefaultValue();

    private DlinkMysqlCatalogFactoryOptions() {
    }
}
