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

package org.dinky.explainermock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MockStatementExplainerTest {
    public static final String PATTERN_STR =
            "CREATE\\s+TABLE\\s+(\\w+)\\s*\\(\\s*([\\s\\S]*?)\\s*\\)\\s*WITH\\s*\\(\\s*([\\s\\S]*?)\\s*\\)";
    public static final Pattern PATTERN = Pattern.compile(PATTERN_STR, Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {
        String sql = "CREATE " + "   TABLE     skuSink ("
                + "sku_id BIGINT,"
                + "sku_name STRING"
                + ")"
                + "\nWITH ( "
                + "\n'connector' = 'blackhole')";
        Matcher matcher = PATTERN.matcher(sql);

        System.out.println(matcher.find());
        System.out.println(matcher.group(1));
        System.out.println(matcher.group(2));
        System.out.println(matcher.group(3));
    }
}
