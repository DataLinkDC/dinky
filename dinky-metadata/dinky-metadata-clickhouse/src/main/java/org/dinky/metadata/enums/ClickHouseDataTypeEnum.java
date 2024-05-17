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

package org.dinky.metadata.enums;

import org.dinky.data.enums.ColumnType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ClickHouseColTypeEnum
 *
 * @since 2024/5/17 11:28
 */
@Getter
@AllArgsConstructor
public enum ClickHouseDataTypeEnum {
    Nullable("Nullable", Boolean.TRUE, 0, null, null),

    Array("Array", Boolean.TRUE, 0, ColumnType.T, ColumnType.T),
    Map("Map", Boolean.TRUE, 0, ColumnType.MAP, ColumnType.MAP),

    UInt8("UInt8", Boolean.FALSE, 0, ColumnType.SHORT, ColumnType.JAVA_LANG_SHORT),
    UInt16("UInt16", Boolean.FALSE, 0, ColumnType.INT, ColumnType.INTEGER),
    UInt32("UInt32", Boolean.FALSE, 0, ColumnType.LONG, ColumnType.JAVA_LANG_LONG),
    UInt64("UInt64", Boolean.FALSE, 0, ColumnType.BIG_INTEGER, ColumnType.BIG_INTEGER),
    UInt128("UInt128", Boolean.FALSE, 0, ColumnType.BIG_INTEGER, ColumnType.BIG_INTEGER),
    UInt256("UInt256", Boolean.FALSE, 0, ColumnType.BIG_INTEGER, ColumnType.BIG_INTEGER),

    Int8("Int8", Boolean.FALSE, 0, ColumnType.BYTE, ColumnType.JAVA_LANG_BYTE),
    Int16("Int16", Boolean.FALSE, 0, ColumnType.SHORT, ColumnType.JAVA_LANG_SHORT),
    Int32("Int32", Boolean.FALSE, 0, ColumnType.INT, ColumnType.INTEGER),
    Int64("Int64", Boolean.FALSE, 0, ColumnType.LONG, ColumnType.JAVA_LANG_LONG),
    Int128("Int128", Boolean.FALSE, 0, ColumnType.BIG_INTEGER, ColumnType.BIG_INTEGER),
    Int256("Int256", Boolean.FALSE, 0, ColumnType.BIG_INTEGER, ColumnType.BIG_INTEGER),

    Float32("Float32", Boolean.FALSE, 0, ColumnType.FLOAT, ColumnType.JAVA_LANG_FLOAT),
    Float64("Float64", Boolean.FALSE, 0, ColumnType.DOUBLE, ColumnType.JAVA_LANG_DOUBLE),

    String("String", Boolean.FALSE, 0, ColumnType.STRING, ColumnType.STRING),
    FixedString("FixedString", Boolean.TRUE, 0, ColumnType.STRING, ColumnType.STRING) {
        @Override
        public Integer getLength(String dataType) {
            // FixedString(N)
            if (StrUtil.isBlank(dataType)) {
                return null;
            }
            String lengthStr = removeType(dataType, Lists.newArrayList(Nullable, FixedString));
            if (!NumberUtil.isNumber(lengthStr)) {
                return null;
            }
            return Integer.parseInt(lengthStr);
        }
    },

    Bool("Bool", Boolean.FALSE, 0, ColumnType.BOOLEAN, ColumnType.JAVA_LANG_BOOLEAN),

    Date("Date", Boolean.FALSE, 0, ColumnType.LOCAL_DATE, ColumnType.LOCAL_DATE),
    Date32("Date32", Boolean.FALSE, 0, ColumnType.LOCAL_DATE, ColumnType.LOCAL_DATE),
    DateTime("DateTime", Boolean.TRUE, 0, ColumnType.LOCAL_DATETIME, ColumnType.LOCAL_DATETIME),
    DateTime64("DateTime64", Boolean.TRUE, 1, ColumnType.LOCAL_DATETIME, ColumnType.LOCAL_DATETIME),

    Decimal("Decimal", Boolean.TRUE, 0, ColumnType.DECIMAL, ColumnType.DECIMAL) {
        @Override
        public Integer getScale(String dataType) {
            // Decimal Decimal(P) Decimal(P, S)
            if (StrUtil.isBlank(dataType) || !dataType.contains("(")) {
                return null;
            }
            String ps = removeType(dataType, Lists.newArrayList(Nullable, Decimal));
            if (!StrUtil.contains(ps, ",")) {
                // only precision, scale is default 0
                return 0;
            }
            List<String> psSplit = StrUtil.split(ps, ",");
            String sStr = psSplit.get(1).trim();
            if (!NumberUtil.isNumber(sStr)) {
                return null;
            }
            return Integer.parseInt(sStr);
        }

        @Override
        public Integer getPrecision(java.lang.String dataType) {
            // Decimal Decimal(P) Decimal(P, S)
            if (StrUtil.isBlank(dataType) || !dataType.contains("(")) {
                return null;
            }
            String ps = removeType(dataType, Lists.newArrayList(Nullable, Decimal));
            String pStr;
            if (!StrUtil.contains(ps, ",")) {
                pStr = ps;
            } else {
                List<String> psSplit = StrUtil.split(ps, ",");
                pStr = psSplit.get(0).trim();
            }
            if (!NumberUtil.isNumber(pStr)) {
                return null;
            }
            return Integer.parseInt(pStr);
        }
    },
    Decimal32("Decimal32", Boolean.TRUE, 1, ColumnType.DECIMAL, ColumnType.DECIMAL) {
        @Override
        public Integer getScale(String dataType) {
            // Decimal32(S)
            if (StrUtil.isBlank(dataType) || !dataType.contains("(")) {
                return null;
            }
            String sStr = removeType(dataType, Lists.newArrayList(Nullable, Decimal32));
            if (!NumberUtil.isNumber(sStr)) {
                return null;
            }
            return Integer.parseInt(sStr);
        }

        @Override
        public Integer getPrecision(String dataType) {
            Integer scale = getScale(dataType);
            if (Objects.isNull(scale)) {
                return null;
            }
            return 9 - scale;
        }
    },
    Decimal64("Decimal64", Boolean.TRUE, 2, ColumnType.DECIMAL, ColumnType.DECIMAL) {
        @Override
        public Integer getScale(String dataType) {
            // Decimal64(S)
            if (StrUtil.isBlank(dataType) || !dataType.contains("(")) {
                return null;
            }
            String sStr = removeType(dataType, Lists.newArrayList(Nullable, Decimal64));
            if (!NumberUtil.isNumber(sStr)) {
                return null;
            }
            return Integer.parseInt(sStr);
        }

        @Override
        public Integer getPrecision(String dataType) {
            Integer scale = getScale(dataType);
            if (Objects.isNull(scale)) {
                return null;
            }
            return 18 - scale;
        }
    },
    Decimal128("Decimal128", Boolean.TRUE, 3, ColumnType.DECIMAL, ColumnType.DECIMAL) {
        @Override
        public Integer getScale(String dataType) {
            // Decimal128(S)
            if (StrUtil.isBlank(dataType) || !dataType.contains("(")) {
                return null;
            }
            String sStr = removeType(dataType, Lists.newArrayList(Nullable, Decimal128));
            if (!NumberUtil.isNumber(sStr)) {
                return null;
            }
            return Integer.parseInt(sStr);
        }

        @Override
        public Integer getPrecision(String dataType) {
            Integer scale = getScale(dataType);
            if (Objects.isNull(scale)) {
                return null;
            }
            return 38 - scale;
        }
    },
    Decimal256("Decimal256", Boolean.TRUE, 4, ColumnType.DECIMAL, ColumnType.DECIMAL) {
        @Override
        public Integer getScale(String dataType) {
            // Decimal256(S)
            if (StrUtil.isBlank(dataType) || !dataType.contains("(")) {
                return null;
            }
            String sStr = removeType(dataType, Lists.newArrayList(Nullable, Decimal256));
            if (!NumberUtil.isNumber(sStr)) {
                return null;
            }
            return Integer.parseInt(sStr);
        }

        @Override
        public Integer getPrecision(String dataType) {
            Integer scale = getScale(dataType);
            if (Objects.isNull(scale)) {
                return null;
            }
            return 76 - scale;
        }
    },

    UUID("UUID", Boolean.FALSE, 0, ColumnType.STRING, ColumnType.STRING),
    // Enum Enum8 Enum16 -> String
    Enum("Enum", Boolean.TRUE, 0, ColumnType.STRING, ColumnType.STRING),
    // Tuple -> Json String
    Tuple("Tuple", Boolean.TRUE, 0, ColumnType.STRING, ColumnType.STRING);

    private final String name;

    private final Boolean prefixMatch;

    /**
     * when multiple type enumerations are matched,
     * the one with the largest priority is taken.
     */
    private final Integer matchPriority;

    private final ColumnType columnType;

    private final ColumnType nullColumnType;

    /**
     * cache data type map
     */
    private static final Map<String, ClickHouseDataTypeEnum> CK_DATA_TYPE_MAP = Maps.newHashMap();

    public Integer getLength(String dataType) {
        return null;
    }

    public Integer getPrecision(String dataType) {
        return null;
    }

    public Integer getScale(String dataType) {
        return null;
    }

    public static String removeType(String dataType, List<ClickHouseDataTypeEnum> ckColTypeEnums) {
        if (StrUtil.isBlank(dataType) || CollUtil.isEmpty(ckColTypeEnums)) {
            return dataType;
        }
        String dataTypeRep = dataType.replaceAll("\\)", "").toLowerCase();
        for (ClickHouseDataTypeEnum ckColTypeEnum : ckColTypeEnums) {
            dataTypeRep = dataTypeRep.replaceAll(ckColTypeEnum.getName().toLowerCase() + "\\(", "");
        }
        return StrUtil.trim(dataTypeRep);
    }

    private static List<ClickHouseDataTypeEnum> match(String dataType) {
        String finalType =
                ClickHouseDataTypeEnum.removeType(dataType, Lists.newArrayList(ClickHouseDataTypeEnum.Nullable));
        return Arrays.stream(ClickHouseDataTypeEnum.values())
                .filter(clickHouseDataTypeEnum -> {
                    String name = clickHouseDataTypeEnum.getName();
                    Boolean prefixMatch = clickHouseDataTypeEnum.getPrefixMatch();
                    if (BooleanUtil.isTrue(prefixMatch)) {
                        return StrUtil.startWithIgnoreCase(finalType, name);
                    }
                    return StrUtil.equalsIgnoreCase(finalType, name);
                })
                .collect(Collectors.toList());
    }

    /**
     * converts given type name to clickhouse data type.
     */
    public static ClickHouseDataTypeEnum of(String dataType) {
        if (Objects.isNull(dataType)) {
            return ClickHouseDataTypeEnum.String;
        }
        ClickHouseDataTypeEnum ckDataTypeEnum = CK_DATA_TYPE_MAP.get(dataType);
        if (Objects.nonNull(ckDataTypeEnum)) {
            return ckDataTypeEnum;
        }
        ClickHouseDataTypeEnum clickHouseDataTypeEnum =
                Optional.ofNullable(match(dataType)).orElse(Lists.newArrayList()).stream()
                        .max(Comparator.comparing(ClickHouseDataTypeEnum::getMatchPriority))
                        .orElse(ClickHouseDataTypeEnum.String);
        CK_DATA_TYPE_MAP.put(dataType, clickHouseDataTypeEnum);
        return clickHouseDataTypeEnum;
    }

    public static Boolean isNullable(String dataType) {
        if (StrUtil.isBlank(dataType)) {
            return Boolean.FALSE;
        }
        return StrUtil.startWithIgnoreCase(dataType.trim(), Nullable.getName());
    }
}
