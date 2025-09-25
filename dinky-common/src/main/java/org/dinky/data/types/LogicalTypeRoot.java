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

package org.dinky.data.types;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum LogicalTypeRoot {
    CHAR(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.CHARACTER_STRING),

    VARCHAR(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.CHARACTER_STRING),

    BOOLEAN(LogicalTypeFamily.PREDEFINED),

    BINARY(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.BINARY_STRING),

    VARBINARY(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.BINARY_STRING),

    DECIMAL(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.NUMERIC, LogicalTypeFamily.EXACT_NUMERIC),

    TINYINT(
            LogicalTypeFamily.PREDEFINED,
            LogicalTypeFamily.NUMERIC,
            LogicalTypeFamily.INTEGER_NUMERIC,
            LogicalTypeFamily.EXACT_NUMERIC),

    SMALLINT(
            LogicalTypeFamily.PREDEFINED,
            LogicalTypeFamily.NUMERIC,
            LogicalTypeFamily.INTEGER_NUMERIC,
            LogicalTypeFamily.EXACT_NUMERIC),

    INTEGER(
            LogicalTypeFamily.PREDEFINED,
            LogicalTypeFamily.NUMERIC,
            LogicalTypeFamily.INTEGER_NUMERIC,
            LogicalTypeFamily.EXACT_NUMERIC),

    BIGINT(
            LogicalTypeFamily.PREDEFINED,
            LogicalTypeFamily.NUMERIC,
            LogicalTypeFamily.INTEGER_NUMERIC,
            LogicalTypeFamily.EXACT_NUMERIC),

    FLOAT(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.NUMERIC, LogicalTypeFamily.APPROXIMATE_NUMERIC),

    DOUBLE(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.NUMERIC, LogicalTypeFamily.APPROXIMATE_NUMERIC),

    DATE(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.DATETIME),

    TIME_WITHOUT_TIME_ZONE(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.DATETIME, LogicalTypeFamily.TIME),

    TIMESTAMP_WITHOUT_TIME_ZONE(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.DATETIME, LogicalTypeFamily.TIMESTAMP),

    TIMESTAMP_WITH_TIME_ZONE(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.DATETIME, LogicalTypeFamily.TIMESTAMP),

    TIMESTAMP_WITH_LOCAL_TIME_ZONE(
            LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.DATETIME, LogicalTypeFamily.TIMESTAMP),

    INTERVAL_YEAR_MONTH(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.INTERVAL),

    INTERVAL_DAY_TIME(LogicalTypeFamily.PREDEFINED, LogicalTypeFamily.INTERVAL),

    ARRAY(LogicalTypeFamily.CONSTRUCTED, LogicalTypeFamily.COLLECTION),

    MAP(LogicalTypeFamily.CONSTRUCTED),

    ROW(LogicalTypeFamily.CONSTRUCTED);

    private final Set<LogicalTypeFamily> families;

    LogicalTypeRoot(LogicalTypeFamily firstFamily, LogicalTypeFamily... otherFamilies) {
        this.families = Collections.unmodifiableSet(EnumSet.of(firstFamily, otherFamilies));
    }

    public Set<LogicalTypeFamily> getFamilies() {
        return families;
    }
}
