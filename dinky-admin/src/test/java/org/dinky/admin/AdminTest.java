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

package org.dinky.admin;

import cn.dev33.satoken.secure.SaSecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.jasper.tagplugins.jstl.core.Url;
import org.assertj.core.util.Arrays;
import org.dinky.ltpa.config.LtpaSetting;
import org.dinky.ltpa.token.LtpaToken;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * SqlParserTest
 *
 * @since 2021/6/14 17:03
 */
@Ignore
@Slf4j
public class AdminTest {

    @Test
    public void adminTest() {
        String admin = SaSecureUtil.md5("admin");
        Assert.assertEquals("21232f297a57a5a743894a0e4a801fc3", admin);
    }

    @Test
    public void ltpaTest() {
        LtpaToken token = ltpaToken();
        log.info("\n\n cookie > Set-Cookie: tenantId=1; Expires={}; Path=/", token.getExpiresDate());
        log.info("\n\n cookie > Set-Cookie: dinky-ltpa={}; Expires={}; Path=/\n\n", token, token.getExpiresDate());
    }

    public LtpaToken ltpaToken(){
        LtpaSetting setting = new LtpaSetting();
        setting.setSecret("2OqzZkZ//RvOLF+X1HqNJWcCBHE=");
        setting.setExpiration(86400);   // 24h
        return LtpaToken.generate("1", setting);
    }

    @Test
    public void ltpaRequestTest() throws IOException {
        URL url = new URL("http://localhost:8888/api/task?id=343");
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json, text/plain, */*");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0");
            conn.setRequestProperty("Cookie", "language=zh-CN; tenantId=1; dinky-ltpa="+ ltpaToken().toString());
            conn.setInstanceFollowRedirects(true);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Response: " + response);
            } else {
                System.out.println("HTTP response code: " + responseCode);
            }
        } finally {
            conn.disconnect();
        }
    }
}
