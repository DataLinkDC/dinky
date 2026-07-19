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

package org.dinky.trans.ddl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.dinky.cdc.SinkBuilder;
import org.dinky.data.model.Table;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the private static method
 * {@code CreateCDCSourceOperation#setSinkTable} introduced in this commit.
 *
 * <p>The method is exercised via reflection to avoid exposing it as package-visible.
 */
class CreateCDCSourceOperationTest {

    private Method setSinkTableMethod;
    private SinkBuilder sinkBuilder;

    @BeforeEach
    void setUp() throws Exception {
        setSinkTableMethod = CreateCDCSourceOperation.class.getDeclaredMethod(
                "setSinkTable", Table.class, List.class, SinkBuilder.class, org.dinky.metadata.driver.Driver.class);
        setSinkTableMethod.setAccessible(true);

        sinkBuilder = mock(SinkBuilder.class);
    }

    private void invoke(Table table, List<Table> sinkTables, SinkBuilder sb, org.dinky.metadata.driver.Driver driver)
            throws Exception {
        setSinkTableMethod.invoke(null, table, sinkTables, sb, driver);
    }

    /**
     * When the sinkBuilder returns a table name that matches one of the sink tables
     * (plain name, no schema prefix), the source table's sinkTable is set.
     */
    @Test
    void testMatchBySinkTableName_plain() throws Exception {
        Table sourceTable = new Table("orders", "public", null);
        Table sinkTable = new Table("orders", "public", null);

        when(sinkBuilder.getSinkTableName(sourceTable)).thenReturn("orders");

        invoke(sourceTable, Collections.singletonList(sinkTable), sinkBuilder, null);

        assertNotNull(sourceTable.getSinkTable());
        assertEquals("orders", sourceTable.getSinkTable().getName());
    }

    /**
     * When the sink table carries a schema prefix (e.g. "public.orders"), the method
     * must split on '.' and match using only the table-name part.
     */
    @Test
    void testMatchBySinkTableName_withSchemaPrefix() throws Exception {
        Table sourceTable = new Table("orders", "public", null);
        Table sinkTable = new Table("orders", "public", null);
        // getSchemaTableName() returns "public.orders"

        when(sinkBuilder.getSinkTableName(sourceTable)).thenReturn("orders");

        invoke(sourceTable, Collections.singletonList(sinkTable), sinkBuilder, null);

        assertNotNull(sourceTable.getSinkTable());
    }

    /**
     * No matching sink table -> sinkTable stays null.
     */
    @Test
    void testNoMatch_sinkTableRemainsNull() throws Exception {
        Table sourceTable = new Table("orders", "public", null);
        Table sinkTable = new Table("customers", "public", null);

        when(sinkBuilder.getSinkTableName(sourceTable)).thenReturn("orders");

        invoke(sourceTable, Collections.singletonList(sinkTable), sinkBuilder, null);

        assertNull(sourceTable.getSinkTable());
    }

    /**
     * When sinkRealDriver is provided, columns should be loaded from it.
     */
    @Test
    void testMatchWithDriver_columnsAreSet() throws Exception {
        Table sourceTable = new Table("orders", "public", null);
        Table sinkTable = new Table("orders", "public", null);

        when(sinkBuilder.getSinkTableName(sourceTable)).thenReturn("orders");

        org.dinky.metadata.driver.Driver driver = mock(org.dinky.metadata.driver.Driver.class);
        when(driver.listColumnsSortByPK("public", "orders")).thenReturn(Collections.emptyList());

        invoke(sourceTable, Collections.singletonList(sinkTable), sinkBuilder, driver);

        assertNotNull(sourceTable.getSinkTable());
        verify(driver).listColumnsSortByPK("public", "orders");
    }

    /**
     * Empty sink table list -> sinkTable stays null, no exception.
     */
    @Test
    void testEmptySinkTableList() throws Exception {
        Table sourceTable = new Table("orders", "public", null);

        when(sinkBuilder.getSinkTableName(sourceTable)).thenReturn("orders");

        invoke(sourceTable, Collections.emptyList(), sinkBuilder, null);

        assertNull(sourceTable.getSinkTable());
    }

    /**
     * Multiple candidates; only the first match should be used (break after first match).
     */
    @Test
    void testFirstMatchWins() throws Exception {
        Table sourceTable = new Table("orders", "public", null);
        Table sinkTable1 = new Table("orders", "schema1", null);
        Table sinkTable2 = new Table("orders", "schema2", null);

        when(sinkBuilder.getSinkTableName(sourceTable)).thenReturn("orders");

        invoke(sourceTable, Arrays.asList(sinkTable1, sinkTable2), sinkBuilder, null);

        assertNotNull(sourceTable.getSinkTable());
        assertEquals("schema1", sourceTable.getSinkTable().getSchema());
    }
}
