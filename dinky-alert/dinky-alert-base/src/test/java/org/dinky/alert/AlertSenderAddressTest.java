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

package org.dinky.alert;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests alert sender address handling for HTTP and HTTPS webhook addresses.
 */
public class AlertSenderAddressTest {

    @Test
    public void alertSenderAcceptsHttpsWebhookAddress() {
        String[] addresses = {
            "https://oapi.dingtalk.com/robot/send?access_token=example",
            "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=example",
            "https://open.feishu.cn/open-apis/bot/v2/hook/example",
            "https://hooks.example.com/services/example/webhook",
            "https://192.168.1.10/hook",
            "https://example.com:443/path",
            "  https://hooks.example.com/a/b  "
        };
        for (String address : addresses) {
            Assert.assertTrue(
                    "Expected a well-defined HTTPS webhook address to be accepted: " + address,
                    AlertSenderAddress.isWellDefined(address));
            AlertSenderAddress.requireWellDefined(address);
        }
    }

    @Test
    public void alertSenderAcceptsHttpWebhookAddress() {
        String[] addresses = {
            "http://localhost:8080/hook",
            "http://127.0.0.1:8080/hook",
            "http://hooks.example.com/webhook",
            "http://[::1]/hook",
            "http://my_internal_host/hook"
        };
        for (String address : addresses) {
            Assert.assertTrue(
                    "Expected a well-defined HTTP webhook address to be accepted: " + address,
                    AlertSenderAddress.isWellDefined(address));
            AlertSenderAddress.requireWellDefined(address);
        }
    }

    @Test
    public void alertSenderRejectsUnexpectedScheme() {
        assertRejected("file:///tmp/webhook");
        assertRejected("file://localhost/tmp/webhook");
        assertRejected("ftp://example.com/hook");
        assertRejected("ldap://127.0.0.1");
        assertRejected("gopher://example.com/1");
        assertRejected("jar:http://example.com/foo.jar!/");
    }

    @Test
    public void alertSenderRejectsMissingHost() {
        assertRejected("https://");
        assertRejected("http://");
        assertRejected("https:///hook");
        assertRejected("http:///hook");
        assertRejected("http:example.com/hook");
    }

    @Test
    public void alertSenderRejectsUserInfo() {
        assertRejected("https://user:pass@example.com/hook");
        assertRejected("http://user@localhost:8080/hook");
    }

    @Test
    public void alertSenderRejectsMalformedAddress() {
        assertRejected(null);
        assertRejected("");
        assertRejected("   ");
        assertRejected("not-a-url");
        assertRejected("://example.com/hook");
        assertRejected("http://");
    }

    @Test
    public void alertSenderRejectsBlankAddress() {
        assertRejected(null);
        assertRejected("");
        assertRejected("   ");
    }

    private static void assertRejected(String address) {
        Assert.assertFalse(
                "Expected an unexpected or malformed address to be rejected: " + address,
                AlertSenderAddress.isWellDefined(address));
        try {
            AlertSenderAddress.requireWellDefined(address);
            Assert.fail("Expected a malformed or unexpected address to be rejected: " + address);
        } catch (AlertException ex) {
            Assert.assertEquals(AlertSenderAddress.NOT_WELL_DEFINED_MESSAGE, ex.getMessage());
        }
    }
}
