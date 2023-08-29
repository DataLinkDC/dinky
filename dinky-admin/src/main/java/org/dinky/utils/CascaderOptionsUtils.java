/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.dinky.utils;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.dinky.data.vo.CascaderVO;

public class CascaderOptionsUtils {

    private final static String FLINK_CONFIG_REPLACE_SUFFIX = "Options";

    /**
     * build flink config cascade options
     * * @param name
     * @param dataList
     */
    public static void buildCascadeOptions(String name, List<CascaderVO> dataList) {
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
                String key = (String) ReflectUtil.invoke(fieldValue, "key");
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
        } catch (ClassNotFoundException ignored) {
        }
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
