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

import org.dinky.assertion.Asserts;
import org.dinky.data.exception.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class YearMonthIntervalType extends LogicalType {
    private static final long serialVersionUID = 1L;

    public static final int MIN_PRECISION = 1;

    public static final int MAX_PRECISION = 4;

    public static final int DEFAULT_PRECISION = 2;

    private static final String YEAR_FORMAT = "INTERVAL YEAR(%d)";

    private static final String YEAR_TO_MONTH_FORMAT = "INTERVAL YEAR(%d) TO MONTH";

    private static final String MONTH_FORMAT = "INTERVAL MONTH";

    public enum YearMonthResolution {
        YEAR,
        YEAR_TO_MONTH,
        MONTH
    }

    private final YearMonthResolution resolution;

    private final int yearPrecision;

    public YearMonthIntervalType(boolean isNullable, YearMonthResolution resolution, int yearPrecision) {
        super(isNullable, LogicalTypeRoot.INTERVAL_YEAR_MONTH);
        Asserts.checkNotNull(resolution);
        if (resolution == YearMonthResolution.MONTH && yearPrecision != DEFAULT_PRECISION) {
            throw new ValidationException(String.format(
                    "Year precision of sub-year intervals must be equal to the default precision %d.",
                    DEFAULT_PRECISION));
        }
        if (yearPrecision < MIN_PRECISION || yearPrecision > MAX_PRECISION) {
            throw new ValidationException(String.format(
                    "Year precision of year-month intervals must be between %d and %d (both inclusive).",
                    MIN_PRECISION, MAX_PRECISION));
        }
        this.resolution = resolution;
        this.yearPrecision = yearPrecision;
    }

    public YearMonthIntervalType(YearMonthResolution resolution, int yearPrecision) {
        this(true, resolution, yearPrecision);
    }

    public YearMonthIntervalType(YearMonthResolution resolution) {
        this(resolution, DEFAULT_PRECISION);
    }

    public YearMonthResolution getResolution() {
        return resolution;
    }

    public int getYearPrecision() {
        return yearPrecision;
    }

    @Override
    public LogicalType copy(boolean isNullable) {
        return new YearMonthIntervalType(isNullable, resolution, yearPrecision);
    }

    @Override
    public LogicalType copy(LogicalTypeParam param) {
        if (param == null || param.getNullable() == null) {
            return copy();
        }
        if (param.getPrecision() == null) {
            return copy(param.getNullable());
        }
        return new YearMonthIntervalType(param.getNullable(), resolution, param.getPrecision());
    }

    @Override
    public String asSerializableString() {
        return withNullability(getResolutionFormat(), yearPrecision);
    }

    @Override
    public List<LogicalType> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        YearMonthIntervalType that = (YearMonthIntervalType) o;
        return yearPrecision == that.yearPrecision && resolution == that.resolution;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resolution, yearPrecision);
    }

    // --------------------------------------------------------------------------------------------

    private String getResolutionFormat() {
        switch (resolution) {
            case YEAR:
                return YEAR_FORMAT;
            case YEAR_TO_MONTH:
                return YEAR_TO_MONTH_FORMAT;
            case MONTH:
                return MONTH_FORMAT;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
