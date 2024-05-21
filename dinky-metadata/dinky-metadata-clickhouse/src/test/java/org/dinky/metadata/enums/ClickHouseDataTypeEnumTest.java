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

package org.dinky.metadata.enums;

import static org.dinky.metadata.enums.ClickHouseDataTypeEnum.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClickHouseDataTypeEnumTest {

    @Test
    public void removeTypeTest() {
        String dataType = "Array(Int32)";
        String res = ClickHouseDataTypeEnum.removeType(dataType, Lists.newArrayList(Nullable, Array));
        assertEquals("int32", res);
    }

    @Test
    public void removeTypeTest2() {
        String dataType = "FixedString(6)";
        String res = ClickHouseDataTypeEnum.removeType(dataType, Lists.newArrayList(Nullable, FixedString));
        assertEquals("6", res);
    }

    @Test
    public void removeTypeTest3() {
        String dataType = "Decimal(6)";
        String res = ClickHouseDataTypeEnum.removeType(dataType, Lists.newArrayList(Nullable, Decimal));
        assertEquals("6", res);
    }

    @Test
    public void ofTest() {
        String dataType = "Decimal(6)";
        ClickHouseDataTypeEnum res = of(dataType);
        assertEquals(Decimal, res);
    }

    @Test
    public void ofTest2() {
        String dataType = "Decimal32(6)";
        ClickHouseDataTypeEnum res = of(dataType);
        assertEquals(Decimal32, res);
    }

    @Test
    public void ofTest4() {
        String dataType = "UInt32";
        ClickHouseDataTypeEnum res = of(dataType);
        assertEquals(UInt32, res);
    }

    @Test
    public void getLengthTest() {
        Integer length = FixedString.getLength("FixedString(10)");
        assertEquals(10, length.intValue());
    }

    @Test
    public void getScaleTest() {
        Integer scale = Decimal.getScale("Decimal(10, 2)");
        assertEquals(2, scale.intValue());
    }

    @Test
    public void getPrecisionTest() {
        Integer precision = Decimal.getPrecision("Decimal(10, 2)");
        assertEquals(10, precision.intValue());
    }
}
