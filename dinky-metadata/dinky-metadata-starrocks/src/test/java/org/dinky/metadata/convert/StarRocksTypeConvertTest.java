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

package org.dinky.metadata.convert;

import org.dinky.data.enums.ColumnType;
import org.dinky.data.model.Column;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StarRocksTypeConvertTest {

    private StarRocksTypeConvert typeConvert;

    @Before
    public void setUp() {
        typeConvert = new StarRocksTypeConvert();
    }

    private Column buildColumn(String type, boolean nullable, boolean keyFlag) {
        Column column = new Column();
        column.setType(type);
        column.setNullable(nullable);
        column.setKeyFlag(keyFlag);
        return column;
    }

    // ---- datetime -> TIMESTAMP (was STRING before fix) ----

    @Test
    public void testDatetimeConvertsToTimestamp() {
        Column column = buildColumn("datetime", false, true);
        Assert.assertEquals(ColumnType.TIMESTAMP, typeConvert.convert(column));
    }

    @Test
    public void testDatetimeNullableConvertsToTimestamp() {
        Column column = buildColumn("datetime", true, false);
        Assert.assertEquals(ColumnType.TIMESTAMP, typeConvert.convert(column));
    }

    // ---- date -> DATE (was STRING before fix) ----

    @Test
    public void testDateConvertsToDate() {
        Column column = buildColumn("date", false, true);
        Assert.assertEquals(ColumnType.DATE, typeConvert.convert(column));
    }

    @Test
    public void testDateNullableConvertsToDate() {
        Column column = buildColumn("date", true, false);
        Assert.assertEquals(ColumnType.DATE, typeConvert.convert(column));
    }

    // ---- other types remain unchanged ----

    @Test
    public void testIntConvertsToInt() {
        Column column = buildColumn("int", false, true);
        Assert.assertEquals(ColumnType.INT, typeConvert.convert(column));
    }

    @Test
    public void testBigintNullableConvertsToLong() {
        Column column = buildColumn("bigint", true, false);
        Assert.assertEquals(ColumnType.JAVA_LANG_LONG, typeConvert.convert(column));
    }

    @Test
    public void testVarcharConvertsToString() {
        Column column = buildColumn("varchar(255)", false, true);
        Assert.assertEquals(ColumnType.STRING, typeConvert.convert(column));
    }

    @Test
    public void testDecimalConvertsToDecimal() {
        Column column = buildColumn("decimal(10,2)", false, true);
        Assert.assertEquals(ColumnType.DECIMAL, typeConvert.convert(column));
    }

    // ---- convertToDB ----

    @Test
    public void testConvertTimestampToDB() {
        // TIMESTAMP has no entry in convertToDB switch -> falls through to varchar
        Assert.assertEquals("varchar", typeConvert.convertToDB(ColumnType.TIMESTAMP));
    }

    @Test
    public void testConvertDateToDB() {
        Assert.assertEquals("varchar", typeConvert.convertToDB(ColumnType.DATE));
    }

    @Test
    public void testConvertStringToDB() {
        Assert.assertEquals("varchar", typeConvert.convertToDB(ColumnType.STRING));
    }

    @Test
    public void testConvertIntToDB() {
        Assert.assertEquals("int", typeConvert.convertToDB(ColumnType.INT));
    }

    @Test
    public void testConvertLongToDB() {
        Assert.assertEquals("bigint", typeConvert.convertToDB(ColumnType.LONG));
    }
}
