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

import org.dinky.assertion.Asserts;
import org.dinky.parse.SqlSegment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlSegmentUtil {
    public static Map<String, List<String>> splitSql2Segment(List<SqlSegment> segments, String statement) {
        Map<String, List<String>> map = new HashMap<>();
        for (SqlSegment sqlSegment : segments) {
            sqlSegment.parse(statement);
            if (Asserts.isNotNullString(sqlSegment.getStart())) {
                map.put(sqlSegment.getType().toUpperCase(), sqlSegment.getBodyPieces());
            }
        }
        return map;
    }
}