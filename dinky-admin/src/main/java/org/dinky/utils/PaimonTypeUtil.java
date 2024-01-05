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

import org.dinky.shaded.paimon.types.DataType;
import org.dinky.shaded.paimon.types.DataTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import cn.hutool.core.map.MapUtil;

public class PaimonTypeUtil {
    private static final Map<Class<?>, DataType> TYPE_MAP = MapUtil.<Class<?>, DataType>builder(
                    String.class, DataTypes.STRING())
            .put(Integer.class, DataTypes.INT())
            .put(Long.class, DataTypes.BIGINT())
            .put(Double.class, DataTypes.DOUBLE())
            .put(Float.class, DataTypes.FLOAT())
            .put(Boolean.class, DataTypes.BOOLEAN())
            .put(Byte.class, DataTypes.TINYINT())
            .put(Short.class, DataTypes.SMALLINT())
            .put(BigDecimal.class, DataTypes.DECIMAL(30, 4))
            .put(Date.class, DataTypes.TIMESTAMP(3))
            .put(LocalDateTime.class, DataTypes.TIMESTAMP(3))
            .build();

    public static DataType classToDataType(Class<?> clazz) {
        return TYPE_MAP.getOrDefault(clazz, DataTypes.STRING());
    }
}
