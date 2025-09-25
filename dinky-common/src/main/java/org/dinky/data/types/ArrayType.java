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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ArrayType extends LogicalType {

    private static final long serialVersionUID = 1L;

    public static final String FORMAT = "ARRAY<%s>";

    private final LogicalType elementType;

    public ArrayType(boolean isNullable, LogicalType elementType) {
        super(isNullable, LogicalTypeRoot.ARRAY);
        this.elementType = Asserts.checkNotNull(elementType, "Element type must not be null.");
    }

    public ArrayType(LogicalType elementType) {
        this(true, elementType);
    }

    public LogicalType getElementType() {
        return elementType;
    }

    @Override
    public LogicalType copy(boolean isNullable) {
        return new ArrayType(isNullable, elementType.copy());
    }

    @Override
    public LogicalType copy(LogicalTypeParam param) {
        if (param == null || param.getNullable() == null) {
            return copy();
        }
        return copy(param.getNullable());
    }

    @Override
    public String asSummaryString() {
        return withNullability(FORMAT, elementType.asSummaryString());
    }

    @Override
    public String asSerializableString() {
        return withNullability(FORMAT, elementType.asSerializableString());
    }

    @Override
    public List<LogicalType> getChildren() {
        return Collections.singletonList(elementType);
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
        ArrayType arrayType = (ArrayType) o;
        return elementType.equals(arrayType.elementType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elementType);
    }
}
