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
import java.util.List;

public final class DateType extends LogicalType {

    private static final long serialVersionUID = 1L;

    private static final String FORMAT = "DATE";

    public DateType(boolean isNullable) {
        super(isNullable, LogicalTypeRoot.DATE);
    }

    public DateType() {
        this(true);
    }

    @Override
    public LogicalType copy(boolean isNullable) {
        return new DateType(isNullable);
    }

    @Override
    public LogicalType copy(LogicalTypeParam param) {
        if (param == null || param.getNullable() == null) {
            return copy();
        }
        return copy(param.getNullable());
    }

    @Override
    public String asSerializableString() {
        return withNullability(FORMAT);
    }

    @Override
    public List<LogicalType> getChildren() {
        return Collections.emptyList();
    }
}
