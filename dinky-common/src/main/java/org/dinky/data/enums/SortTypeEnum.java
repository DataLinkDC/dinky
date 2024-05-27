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

package org.dinky.data.enums;

import org.dinky.utils.I18n;

import org.apache.commons.codec.binary.StringUtils;

import java.util.Arrays;
import java.util.Comparator;

import cn.hutool.core.util.ObjectUtil;

/**
 * SortType
 *
 * @since 2024/4/25
 */
public enum SortTypeEnum {

    /**
     * SortType
     */
    ASC("asc", "sort.type.asc") {
        @Override
        public <T extends Comparable<T>> Integer compare(T a, T b) {
            return ObjectUtil.compare(a, b);
        }

        @Override
        public <T extends Comparable<T>> Integer compare(T a, T b, Comparator<Object> comparator) {
            return compareByComparator(a, b, comparator);
        }
    },
    DESC("desc", "sort.type.desc") {
        @Override
        public <T extends Comparable<T>> Integer compare(T a, T b) {
            return -ObjectUtil.compare(a, b);
        }

        @Override
        public <T extends Comparable<T>> Integer compare(T a, T b, Comparator<Object> comparator) {
            return -compareByComparator(a, b, comparator);
        }
    };

    private final String name;
    private final String value;

    SortTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getI18nValue() {
        return I18n.getMessage(this.getValue());
    }

    public abstract <T extends Comparable<T>> Integer compare(T a, T b);

    public abstract <T extends Comparable<T>> Integer compare(T a, T b, Comparator<Object> comparator);

    public static SortTypeEnum getByName(String name) {
        return Arrays.stream(SortTypeEnum.values())
                .filter(sortTypeEnum -> StringUtils.equals(sortTypeEnum.getName(), name))
                .findFirst()
                .orElse(ASC);
    }

    private static <T extends Comparable<T>> Integer compareByComparator(T a, T b, Comparator<Object> comparator) {
        if (a == b) {
            return 0;
        } else if (a == null) {
            return -1;
        } else if (b == null) {
            return 1;
        } else {
            return comparator.compare(a, b);
        }
    }
}
