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

import org.dinky.data.flink.config.FlinkConfigOption;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;

public class FlinkConfigOptionsUtils {
    private static final Logger logger = Logger.getLogger(FlinkConfigOptionsUtils.class.getName());

    private static final String FLINK_CONFIG_REPLACE_SUFFIX = "Options";

    private static final Map<String, List<FlinkConfigOption>> cache = new HashMap<>();
    /**
     * build flink config cascade options
     * * @param name
     */
    public static List<FlinkConfigOption> loadOptionsByClassName(String name) {

        if (cache.containsKey(name) && cache.get(name) != null) {
            return cache.get(name);
        }

        List<FlinkConfigOption> configList = new ArrayList<>();
        try {
            Class<?> loadClass = ClassLoaderUtil.getContextClassLoader().loadClass(name);
            Field[] fields = ReflectUtil.getFields(loadClass, f -> {
                try {
                    return f.getType().isAssignableFrom(Class.forName("org.apache.flink.configuration.ConfigOption"))
                            && Modifier.isStatic(f.getModifiers());
                } catch (ClassNotFoundException e) {
                    return false;
                }
            });
            for (Field field : fields) {
                FlinkConfigOption config = new FlinkConfigOption();
                Object fieldValue = ReflectUtil.getStaticFieldValue(field);
                String key = ReflectUtil.invoke(fieldValue, "key");
                if (!key.contains(".")) {
                    continue;
                }
                Object defaultValue = ReflectUtil.invoke(fieldValue, "defaultValue");
                config.setKey(key);
                if (ObjectUtil.isBasicType(defaultValue)) {
                    config.setDefaultValue(String.valueOf(defaultValue));
                } else {
                    config.setDefaultValue("");
                }
                configList.add(config);
            }
        } catch (ClassNotFoundException ignored) {
            logger.warning("Could not get config option, class not found: " + name);
        }
        return configList;
    }

    public static String parsedBinlogGroup(String name) {
        String[] names = name.split("\\.");
        // delete Option suffix and return
        String lastName = names[names.length - 1];
        if (lastName.endsWith(FLINK_CONFIG_REPLACE_SUFFIX)) {
            return lastName.replace(FLINK_CONFIG_REPLACE_SUFFIX, "");
        }
        return lastName;
    }

    public static String[] getConfigOptionsClass() {
        return ResourceUtil.readUtf8Str("dinky-loader/FlinkConfClass")
                .replace("\r", "")
                .split("\n");
    }
}
