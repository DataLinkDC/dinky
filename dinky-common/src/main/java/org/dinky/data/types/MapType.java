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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class MapType extends LogicalType {
    private static final long serialVersionUID = 1L;

    public static final String FORMAT = "MAP<%s, %s>";

    private final LogicalType keyType;

    private final LogicalType valueType;

    public MapType(boolean isNullable, LogicalType keyType, LogicalType valueType) {
        super(isNullable, LogicalTypeRoot.MAP);
        this.keyType = Asserts.checkNotNull(keyType, "Key type must not be null.");
        this.valueType = Asserts.checkNotNull(valueType, "Value type must not be null.");
    }

    public MapType(LogicalType keyType, LogicalType valueType) {
        this(true, keyType, valueType);
    }

    public LogicalType getKeyType() {
        return keyType;
    }

    public LogicalType getValueType() {
        return valueType;
    }

    @Override
    public LogicalType copy(boolean isNullable) {
        return new MapType(isNullable, keyType.copy(), valueType.copy());
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
        return withNullability(FORMAT, keyType.asSummaryString(), valueType.asSummaryString());
    }

    @Override
    public String asSerializableString() {
        return withNullability(FORMAT, keyType.asSerializableString(), valueType.asSerializableString());
    }

    @Override
    public List<LogicalType> getChildren() {
        return Collections.unmodifiableList(Arrays.asList(keyType, valueType));
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
        MapType mapType = (MapType) o;
        return keyType.equals(mapType.keyType) && valueType.equals(mapType.valueType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), keyType, valueType);
    }
}
