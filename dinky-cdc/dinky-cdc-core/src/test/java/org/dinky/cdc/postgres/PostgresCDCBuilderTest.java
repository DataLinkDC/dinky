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

package org.dinky.cdc.postgres;

import static org.mockito.Mockito.when;

import org.dinky.data.model.FlinkCDCConfig;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for {@link PostgresCDCBuilder#parseMetaDataConfig()} and the private
 * {@code composeJdbcProperties} helper introduced in this commit.
 */
public class PostgresCDCBuilderTest {

    @Mock
    private FlinkCDCConfig config;

    private PostgresCDCBuilder builder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        builder = new PostgresCDCBuilder(config);
        when(config.getHostname()).thenReturn("localhost");
        when(config.getPort()).thenReturn(5432);
        when(config.getDatabase()).thenReturn("testdb");
        when(config.getUsername()).thenReturn("user");
        when(config.getPassword()).thenReturn("pass");
    }

    /** No jdbc properties -> URL should not contain '?' */
    @Test
    public void testParseMetaDataConfig_noJdbcProperties() {
        when(config.getJdbc()).thenReturn(null);

        Map<String, String> result = builder.parseMetaDataConfig();

        String url = result.get("url");
        Assert.assertNotNull(url);
        Assert.assertTrue("URL should start with jdbc:postgres://", url.startsWith("jdbc:postgres://"));
        Assert.assertFalse("URL should not contain '?' when jdbc props are empty", url.contains("?"));
    }

    /** Empty jdbc map -> same as null, no query string */
    @Test
    public void testParseMetaDataConfig_emptyJdbcProperties() {
        when(config.getJdbc()).thenReturn(Collections.emptyMap());

        Map<String, String> result = builder.parseMetaDataConfig();

        String url = result.get("url");
        Assert.assertFalse("URL should not contain '?' for empty jdbc map", url.contains("?"));
    }

    /** Single jdbc property -> ?key=value */
    @Test
    public void testParseMetaDataConfig_singleJdbcProperty() {
        Map<String, String> jdbc = Collections.singletonMap("ssl", "true");
        when(config.getJdbc()).thenReturn(jdbc);

        Map<String, String> result = builder.parseMetaDataConfig();

        String url = result.get("url");
        Assert.assertTrue("URL should contain '?ssl=true'", url.contains("?ssl=true"));
        // Must not end with trailing '&'
        Assert.assertFalse("URL must not end with '&'", url.endsWith("&"));
    }

    /** Multiple jdbc properties -> all encoded, no trailing '&' */
    @Test
    public void testParseMetaDataConfig_multipleJdbcProperties() {
        Map<String, String> jdbc = new LinkedHashMap<>();
        jdbc.put("ssl", "true");
        jdbc.put("sslmode", "require");
        when(config.getJdbc()).thenReturn(jdbc);

        Map<String, String> result = builder.parseMetaDataConfig();

        String url = result.get("url");
        Assert.assertTrue("URL should contain ssl param", url.contains("ssl=true"));
        Assert.assertTrue("URL should contain sslmode param", url.contains("sslmode=require"));
        Assert.assertFalse("URL must not end with '&'", url.endsWith("&"));
        // Exactly one '?' in the query string part
        Assert.assertEquals(
                "URL should have exactly one '?'",
                1,
                url.chars().filter(c -> c == '?').count());
    }

    /** Host / port embedded correctly in URL */
    @Test
    public void testParseMetaDataConfig_urlContainsHostAndPort() {
        when(config.getJdbc()).thenReturn(null);

        Map<String, String> result = builder.parseMetaDataConfig();

        String url = result.get("url");
        Assert.assertTrue("URL should contain host", url.contains("localhost"));
        Assert.assertTrue("URL should contain port", url.contains("5432"));
    }
}
