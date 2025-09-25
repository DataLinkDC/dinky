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

import org.dinky.data.exception.ValidationException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TimestampType extends LogicalType {
    private static final long serialVersionUID = 1L;

    public static final int MIN_PRECISION = 0;

    public static final int MAX_PRECISION = 9;

    public static final int DEFAULT_PRECISION = 6;

    private static final String FORMAT = "TIMESTAMP(%d)";

    private final int precision;

    public TimestampType(boolean isNullable, int precision) {
        super(isNullable, LogicalTypeRoot.TIMESTAMP_WITHOUT_TIME_ZONE);
        if (precision < MIN_PRECISION || precision > MAX_PRECISION) {
            throw new ValidationException(String.format(
                    "Timestamp precision must be between %d and %d (both inclusive).", MIN_PRECISION, MAX_PRECISION));
        }
        this.precision = precision;
    }

    public TimestampType(int precision) {
        this(true, precision);
    }

    public TimestampType() {
        this(DEFAULT_PRECISION);
    }

    public int getPrecision() {
        return precision;
    }

    @Override
    public LogicalType copy(boolean isNullable) {
        return new TimestampType(isNullable, precision);
    }

    @Override
    public LogicalType copy(LogicalTypeParam param) {
        if (param == null || param.getNullable() == null) {
            return copy();
        }
        if (param.getPrecision() == null) {
            return copy(param.getNullable());
        }
        return new TimestampType(param.getNullable(), param.getPrecision());
    }

    @Override
    public String asSerializableString() {
        return withNullability(FORMAT, precision);
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
        TimestampType that = (TimestampType) o;
        return precision == that.precision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), precision);
    }
}
