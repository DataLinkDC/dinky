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

import org.dinky.data.constant.CatalogueSortConstant;
import org.dinky.utils.I18n;

/**
 * CatalogueSortValue
 *
 * @since 2024/4/25
 */
public enum CatalogueSortValueEnum {

    /**
     * CatalogueSortType
     */
    FIRST_LETTER(CatalogueSortConstant.STRATEGY_FIRST_LETTER, "catalogue.sort.value.first_letter"),
    CREATE_TIME(CatalogueSortConstant.STRATEGY_CREATE_TIME, "catalogue.sort.value.create_time");

    private final String name;
    private final String value;

    CatalogueSortValueEnum(String name, String value) {
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
}
