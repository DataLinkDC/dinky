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

import org.dinky.data.vo.CascaderVO;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ReflectUtil;

public class CascaderOptionsUtils {
    private static final Logger logger = Logger.getLogger(CascaderOptionsUtils.class.getName());

    private static final String FLINK_CONFIG_REPLACE_SUFFIX = "Options";

    private static final Map<String, List<CascaderVO>> cache = new HashMap<>();
    /**
     * build flink config cascade options
     * * @param name
     */
    public static List<CascaderVO> buildCascadeOptions(String name) {

        if (cache.containsKey(name) && cache.get(name) != null) {
            return cache.get(name);
        }

        List<CascaderVO> dataList = new ArrayList<>();
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
            List<CascaderVO> configList = new ArrayList<>();
            for (Field field : fields) {
                CascaderVO config = new CascaderVO();
                Object fieldValue = ReflectUtil.getStaticFieldValue(field);
                String key = ReflectUtil.invoke(fieldValue, "key");
                config.setValue(key);
                config.setLabel(key);
                configList.add(config);
            }
            CascaderVO cascaderVO = new CascaderVO();
            cascaderVO.setChildren(configList);
            // parsed binlog group
            String parsedBinlogGroup = parsedBinlogGroup(name);

            cascaderVO.setLabel(parsedBinlogGroup);
            cascaderVO.setValue(parsedBinlogGroup);
            dataList.add(cascaderVO);
            cache.put(name, dataList);
        } catch (ClassNotFoundException ignored) {
            logger.warning("get config option error, class not found: " + name);
        }
        return dataList;
    }

    private static String parsedBinlogGroup(String name) {
        String[] names = name.split("\\.");
        // delete Option suffix and return
        String lastName = names[names.length - 1];
        if (lastName.endsWith(FLINK_CONFIG_REPLACE_SUFFIX)) {
            return lastName.replace(FLINK_CONFIG_REPLACE_SUFFIX, "");
        }
        return lastName;
    }
}
