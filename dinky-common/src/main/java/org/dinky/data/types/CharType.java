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

public final class CharType extends LogicalType {
    private static final long serialVersionUID = 1L;

    public static final int EMPTY_LITERAL_LENGTH = 0;

    public static final int DEFAULT_LENGTH = 1;

    private static final String FORMAT = "CHAR(%d)";

    private final int length;

    public CharType(boolean isNullable, int length) {
        super(isNullable, LogicalTypeRoot.CHAR);
        this.length = length;
    }

    /** Helper constructor for {@link #ofEmptyLiteral()} and {@link #copy(boolean)}. */
    private CharType(int length, boolean isNullable) {
        super(isNullable, LogicalTypeRoot.CHAR);
        this.length = length;
    }

    public CharType(int length) {
        this(true, length);
    }

    public CharType() {
        this(DEFAULT_LENGTH);
    }

    public int getLength() {
        return length;
    }

    public static CharType ofEmptyLiteral() {
        return new CharType(EMPTY_LITERAL_LENGTH, false);
    }

    @Override
    public LogicalType copy(boolean isNullable) {
        return new CharType(length, isNullable);
    }

    @Override
    public LogicalType copy(LogicalTypeParam param) {
        if (param == null || param.getNullable() == null) {
            return copy();
        }
        if (param.getLength() == null) {
            return copy(param.getNullable());
        }
        return new CharType(param.getLength(), param.getNullable());
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
        CharType charType = (CharType) o;
        return length == charType.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), length);
    }
}
