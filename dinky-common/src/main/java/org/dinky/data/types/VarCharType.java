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

import org.dinky.data.exception.DinkyException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class VarCharType extends LogicalType {
    private static final long serialVersionUID = 1L;

    public static final int EMPTY_LITERAL_LENGTH = 0;

    public static final int MIN_LENGTH = 1;

    public static final int MAX_LENGTH = Integer.MAX_VALUE;

    public static final int DEFAULT_LENGTH = 1;

    public static final VarCharType STRING_TYPE = new VarCharType(MAX_LENGTH);

    private static final String FORMAT = "VARCHAR(%d)";

    private static final String MAX_FORMAT = "STRING";

    private final int length;

    public VarCharType(boolean isNullable, int length) {
        super(isNullable, LogicalTypeRoot.VARCHAR);
        if (length < MIN_LENGTH) {
            length = MAX_LENGTH;
        }
        this.length = length;
    }

    public VarCharType(int length) {
        this(true, length);
    }

    public VarCharType() {
        this(DEFAULT_LENGTH);
    }

    /** Helper constructor for {@link #ofEmptyLiteral()}. */
    private VarCharType(int length, boolean isNullable) {
        super(isNullable, LogicalTypeRoot.VARCHAR);
        this.length = length;
    }

    /**
     * The SQL standard defines that character string literals are allowed to be zero-length strings
     * (i.e., to contain no characters) even though it is not permitted to declare a type that is
     * zero. This has also implications on variable-length character strings during type inference
     * because any fixed-length character string should be convertible to a variable-length one.
     *
     * <p>This method enables this special kind of character string.
     *
     * <p>Zero-length character strings have no serializable string representation.
     */
    public static VarCharType ofEmptyLiteral() {
        return new VarCharType(EMPTY_LITERAL_LENGTH, false);
    }

    public int getLength() {
        return length;
    }

    @Override
    public LogicalType copy(boolean isNullable) {
        return new VarCharType(length, isNullable);
    }

    @Override
    public LogicalType copy(LogicalTypeParam param) {
        if (param == null || param.getNullable() == null) {
            return copy();
        }
        if (param.getLength() == null) {
            return copy(param.getNullable());
        }
        return new VarCharType(param.getNullable(), param.getLength());
    }

    @Override
    public String asSerializableString() {
        if (length == EMPTY_LITERAL_LENGTH) {
            throw new DinkyException("Zero-length character strings have no serializable string representation.");
        }
        return withNullability(FORMAT, length);
    }

    @Override
    public String asSummaryString() {
        if (length == MAX_LENGTH) {
            return withNullability(MAX_FORMAT);
        }
        return withNullability(FORMAT, length);
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
        VarCharType that = (VarCharType) o;
        return length == that.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), length);
    }
}
