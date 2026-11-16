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

package org.dinky.alert.http;

import org.dinky.alert.AlertResult;
import org.dinky.alert.http.params.HttpParams;
import org.dinky.data.ext.ConfigItem;
import org.dinky.utils.JsonUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.sun.net.httpserver.HttpServer;

/**
 * Tests HTTP alert sender address handling.
 */
public class HttpSenderAddressTest {

    @Test
    public void alertSenderAcceptsHttpWebhookAddress() throws Exception {
        AtomicInteger hits = new AtomicInteger();
        HttpServer server = startServer(hits, "ok");
        try {
            String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/hook";
            AlertResult result = send(url);
            Assert.assertTrue(result.getSuccess());
            Assert.assertEquals(1, hits.get());
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void alertSenderRejectsUnexpectedScheme() throws Exception {
        AtomicInteger hits = new AtomicInteger();
        HttpServer server = startServer(hits, "ok");
        try {
            AlertResult result = send("file:///tmp/webhook");
            Assert.assertFalse(result.getSuccess());
            Assert.assertEquals(0, hits.get());
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void alertSenderRejectsMalformedAddress() {
        AlertResult blank = send("   ");
        Assert.assertFalse(blank.getSuccess());
        AlertResult malformed = send("not-a-url");
        Assert.assertFalse(malformed.getSuccess());
        AlertResult missingHost = send("https:///hook");
        Assert.assertFalse(missingHost.getSuccess());
    }

    @Test
    public void alertSenderRejectsUserInfoWithoutOutboundCall() throws Exception {
        AtomicInteger hits = new AtomicInteger();
        HttpServer server = startServer(hits, "ok");
        try {
            int port = server.getAddress().getPort();
            AlertResult result = send("http://user@127.0.0.1:" + port + "/hook");
            Assert.assertFalse(result.getSuccess());
            Assert.assertEquals(0, hits.get());
        } finally {
            server.stop(0);
        }
    }

    private static AlertResult send(String url) {
        HttpParams httpParams = new HttpParams();
        httpParams.setUrl(url);
        httpParams.setMethod(HttpConstants.REQUEST_TYPE_POST);
        httpParams.setHeaders(Collections.singletonList(new ConfigItem("Content-Type", "application/json")));
        httpParams.setBody("{}");
        httpParams.setContentFiled("text");
        httpParams.setTitleFiled("title");
        Map<String, Object> config = JsonUtils.parseDict(JsonUtils.toJsonString(httpParams));
        return new HttpSender(config).send("title", "content");
    }

    private static HttpServer startServer(AtomicInteger hits, String responseBody) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            hits.incrementAndGet();
            byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        return server;
    }
}
