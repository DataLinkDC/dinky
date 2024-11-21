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

package org.dinky.cdc.convert;

import org.apache.flink.table.types.logical.TimestampType;

import java.time.ZoneId;

import org.junit.Assert;

public class DataTypeConverterTest {

    // todo: check the time zone of Timestamp
    //    @Test
    public void testConvertToRowWithTimestamp() {
        Object value0 = DataTypeConverter.convertToRow(1688946316L, new TimestampType(0), ZoneId.systemDefault());
        Object value3 = DataTypeConverter.convertToRow(1688946316123L, new TimestampType(3), ZoneId.systemDefault());
        Object value6 = DataTypeConverter.convertToRow(1688946316123456L, new TimestampType(6), ZoneId.systemDefault());
        String target0 = "2023-07-10T07:45:16";
        String target3 = "2023-07-10T07:45:16.123";
        String target6 = "2023-07-10T07:45:16.123";
        Assert.assertEquals(target0, value0.toString());
        Assert.assertEquals(target3, value3.toString());
        Assert.assertEquals(target6, value6.toString());
    }

    //    @Test
    public void testConvertToRowDataWithTimestamp() {
        Object value0 = DataTypeConverter.convertToRowData(1688946316L, new TimestampType(0), ZoneId.systemDefault());
        Object value3 =
                DataTypeConverter.convertToRowData(1688946316123L, new TimestampType(3), ZoneId.systemDefault());
        Object value6 =
                DataTypeConverter.convertToRowData(1688946316123456L, new TimestampType(6), ZoneId.systemDefault());
        String target0 = "2023-07-10T07:45:16";
        String target3 = "2023-07-10T07:45:16.123";
        String target6 = "2023-07-10T07:45:16.123";
        Assert.assertEquals(target0, value0.toString());
        Assert.assertEquals(target3, value3.toString());
        Assert.assertEquals(target6, value6.toString());
    }
}
