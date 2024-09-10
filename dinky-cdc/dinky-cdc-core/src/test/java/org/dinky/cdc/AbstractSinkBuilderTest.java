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

package org.dinky.cdc;

import static org.mockito.Mockito.when;

import org.dinky.data.model.FlinkCDCConfig;
import org.dinky.data.model.Table;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * test Sink Builder
 *
 * @author soulmz
 * @since 2024/06/12
 */
public class AbstractSinkBuilderTest {

    @Mock
    private FlinkCDCConfig config;

    @InjectMocks
    private TestSinkBuilder sinkBuilder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        sinkBuilder = new TestSinkBuilder(config);
    }

    @Test
    public void testGetSinkTableNameWithPrefixAndSuffix() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("table.prefix", "pre_");
        sinkConfig.put("table.suffix", "_suf");
        sinkConfig.put("table.lower", "false");
        sinkConfig.put("table.upper", "false");
        when(config.getSink()).thenReturn(sinkConfig);

        Table table = new Table("testTable", "testSchema", null);
        String expectedTableName = "pre_testTable_suf";
        Assert.assertEquals(expectedTableName, sinkBuilder.getSinkTableName(table));
    }

    @Test
    public void testGetSinkTableNameWithOnlyPrefix() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("table.prefix", "pre_");
        sinkConfig.put("table.suffix", "");
        sinkConfig.put("table.lower", "false");
        sinkConfig.put("table.upper", "false");
        when(config.getSink()).thenReturn(sinkConfig);

        Table table = new Table("testTable", "testSchema", null);
        String expectedTableName = "pre_testTable";
        Assert.assertEquals(expectedTableName, sinkBuilder.getSinkTableName(table));
    }

    @Test
    public void testGetSinkTableNameWithOnlySuffix() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("table.prefix", "");
        sinkConfig.put("table.suffix", "_suf");
        sinkConfig.put("table.lower", "false");
        sinkConfig.put("table.upper", "false");
        when(config.getSink()).thenReturn(sinkConfig);

        Table table = new Table("testTable", "testSchema", null);
        String expectedTableName = "testTable_suf";
        Assert.assertEquals(expectedTableName, sinkBuilder.getSinkTableName(table));
    }

    @Test
    public void testGetSinkTableNameWithSchemaPrefixEnabled() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("table.prefix.schema", "true");
        sinkConfig.put("table.prefix", "");
        sinkConfig.put("table.suffix", "");
        sinkConfig.put("table.lower", "false");
        sinkConfig.put("table.upper", "false");
        when(config.getSink()).thenReturn(sinkConfig);

        Table table = new Table("testTable", "testSchema", null);
        String expectedTableName = "testSchema_testTable";
        Assert.assertEquals(expectedTableName, sinkBuilder.getSinkTableName(table));
    }

    @Test
    public void testGetSinkTableNameWithConversionLowerCase() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("table.prefix", "");
        sinkConfig.put("table.suffix", "");
        sinkConfig.put("table.lower", "true");
        sinkConfig.put("table.upper", "false");
        when(config.getSink()).thenReturn(sinkConfig);

        Table table = new Table("TestTable", "TestSchema", null);
        String expectedTableName = "testtable";
        Assert.assertEquals(expectedTableName, sinkBuilder.getSinkTableName(table));
    }

    @Test
    public void testGetSinkTableNameWithConversionUpperCase() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("table.prefix", "");
        sinkConfig.put("table.suffix", "");
        sinkConfig.put("table.lower", "false");
        sinkConfig.put("table.upper", "true");
        when(config.getSink()).thenReturn(sinkConfig);

        Table table = new Table("TestTable", "TestSchema", null);
        String expectedTableName = "TESTTABLE";
        Assert.assertEquals(expectedTableName, sinkBuilder.getSinkTableName(table));
    }

    @Test
    public void testGetSinkTableNameWithConversionUpperAndLowerCase() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("table.prefix", "");
        sinkConfig.put("table.suffix", "");
        sinkConfig.put("table.lower", "true");
        sinkConfig.put("table.upper", "true");
        when(config.getSink()).thenReturn(sinkConfig);

        Table table = new Table("TestTable", "TestSchema", null);
        Assert.assertThrows(IllegalArgumentException.class, () -> sinkBuilder.getSinkTableName(table));
    }

    @Test
    public void testGetSinkTableNameWithNoConfigPrefixOrSuffix() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("table.prefix", "");
        sinkConfig.put("table.suffix", "");
        sinkConfig.put("table.lower", "false");
        sinkConfig.put("table.upper", "false");
        when(config.getSink()).thenReturn(sinkConfig);

        Table table = new Table("testTable", "testSchema", null);
        String expectedTableName = "testTable";
        Assert.assertEquals(expectedTableName, sinkBuilder.getSinkTableName(table));
    }

    @Test
    public void testGetSinkTableNameWithReplace() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("table.replace.pattern", "t_(.*?)_");
        sinkConfig.put("table.replace.with", "ods_$1_");
        sinkConfig.put("table.lower", "false");
        sinkConfig.put("table.upper", "false");
        when(config.getSink()).thenReturn(sinkConfig);

        Table table = new Table("t_example_test", "testSchema", null);
        String expectedTableName = "ods_example_test";
        Assert.assertEquals(expectedTableName, sinkBuilder.getSinkTableName(table));
    }

    @Test
    public void testGetSinkTableNameWithMappingRoute() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("table.mapping-routes", "t_biz_ss:m_biz_ss,t_biz_aa:m_biz_aa,t_biz_bb:");
        when(config.getSink()).thenReturn(sinkConfig);

        Table tableSS = new Table("t_biz_ss", "testSchema", null);
        String expectedTableNameSS = "m_biz_ss";
        Assert.assertEquals(expectedTableNameSS, sinkBuilder.getSinkTableName(tableSS));

        Table tableAA = new Table("t_biz_aa", "testSchema", null);
        String expectedTableNameAA = "m_biz_aa";
        Assert.assertEquals(expectedTableNameAA, sinkBuilder.getSinkTableName(tableAA));
        // If the rules do not match, the original table name will be used.
        Table tableBB = new Table("t_biz_bb", "testSchema", null);
        String expectedTableNameBB = "m_biz_bb";
        Assert.assertNotEquals(expectedTableNameBB, sinkBuilder.getSinkTableName(tableBB));
    }

    @Test
    public void testGetSinkTableNameWithMappingRouteAndReplace() {
        Map<String, String> sinkConfig = new HashMap<>();
        sinkConfig.put("table.mapping-routes", "t_biz_ss:m_biz_ss,t_biz_aa:m_biz_aa,t_biz_bb:");
        sinkConfig.put("table.replace.pattern", "^t_(.*?)");
        sinkConfig.put("table.replace.with", "ods_$1");
        when(config.getSink()).thenReturn(sinkConfig);

        Table tableSS = new Table("t_biz_ss", "testSchema", null);
        String expectedTableNameSS = "m_biz_ss";
        Assert.assertEquals(expectedTableNameSS, sinkBuilder.getSinkTableName(tableSS));

        Table tableAA = new Table("t_biz_aa", "testSchema", null);
        String expectedTableNameAA = "m_biz_aa";
        Assert.assertEquals(expectedTableNameAA, sinkBuilder.getSinkTableName(tableAA));
        // Unmatched rules will use the original table name and match the rule table.replace.pattern="t_(.*?)_", and the
        // target will be replaced by ods
        Table tableBB = new Table("t_biz_bb", "testSchema", null);
        String expectedTableNameBB = "ods_biz_bb";
        Assert.assertEquals(expectedTableNameBB, sinkBuilder.getSinkTableName(tableBB));
        // Unmatched rules will use the original table name and match the rule table.replace.pattern="t_(.*?)_", and the
        // target will be replaced by ods
        Table tableCC = new Table("t_product_spu", "testSchema", null);
        String expectedTableNameCC = "ods_product_spu";
        Assert.assertEquals(expectedTableNameCC, sinkBuilder.getSinkTableName(tableCC));
    }
}

class TestSinkBuilder extends AbstractSinkBuilder {
    public TestSinkBuilder(FlinkCDCConfig config) {
        super(config);
    }

    public TestSinkBuilder() {
        super();
    }

    @Override
    public String getHandle() {
        return "test-sql";
    }

    @Override
    public SinkBuilder create(FlinkCDCConfig config) {
        return new TestSinkBuilder(config);
    }
}
