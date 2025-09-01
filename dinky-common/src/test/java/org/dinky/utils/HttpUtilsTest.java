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

package org.dinky.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.dinky.data.socket.AddressInfo;

import org.junit.jupiter.api.Test;

/**
 * HttpUtils
 */
public class HttpUtilsTest {

    @Test
    public void testParseAddressWithHttpProtocol() {
        AddressInfo result = HttpUtils.parseAddress("http://127.0.0.1:8888");
        assertEquals("127.0.0.1", result.getHost());
        assertEquals(8888, result.getPort());
    }

    @Test
    public void testParseAddressWithoutProtocol() {
        AddressInfo result = HttpUtils.parseAddress("localhost:8080");
        assertEquals("localhost", result.getHost());
        assertEquals(8080, result.getPort());
    }

    @Test
    public void testParseAddressWithHttpsProtocol() {
        AddressInfo result = HttpUtils.parseAddress("https://example.com:443");
        assertEquals("example.com", result.getHost());
        assertEquals(443, result.getPort());
    }

    @Test
    public void testParseAddressWithIpv6() {
        AddressInfo result = HttpUtils.parseAddress("http://[2001:db8::1]:8080");
        assertEquals("2001:db8::1", result.getHost());
        assertEquals(8080, result.getPort());
    }

    @Test
    public void testParseAddressWithIpv6WithoutProtocol() {
        AddressInfo result = HttpUtils.parseAddress("[2001:db8::1]:8080");
        assertEquals("2001:db8::1", result.getHost());
        assertEquals(8080, result.getPort());
    }

    @Test
    public void testParseAddressEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            HttpUtils.parseAddress("");
        });
    }

    @Test
    public void testParseAddressNullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            HttpUtils.parseAddress(null);
        });
    }

    @Test
    public void testParseAddressInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            HttpUtils.parseAddress("invalid-format");
        });
    }

    @Test
    public void testParseAddressInvalidPort() {
        assertThrows(IllegalArgumentException.class, () -> {
            HttpUtils.parseAddress("localhost:abc");
        });
    }

    @Test
    public void testParseAddressPortOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            HttpUtils.parseAddress("localhost:70000");
        });
    }

    @Test
    public void testParseAddressPortZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            HttpUtils.parseAddress("localhost:0");
        });
    }

    @Test
    public void testParseAddressEmptyHost() {
        assertThrows(IllegalArgumentException.class, () -> {
            HttpUtils.parseAddress(":8080");
        });
    }
}
