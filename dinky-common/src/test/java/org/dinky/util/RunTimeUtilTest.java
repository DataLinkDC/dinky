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

package org.dinky.util;

import org.dinky.utils.RunTimeUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

public class RunTimeUtilTest {
    public static final List<String> RESULT1 = Lists.newArrayList(
            "merge_into",
            "--warehouse",
            "hdfs:///tmp/paimon",
            "--database",
            "default",
            "--table",
            "T",
            "--source_table",
            "S",
            "--on",
            "T.id = S.order_id",
            "--merge_actions",
            "matched-upsert,matched-delete",
            "--matched_upsert_condition",
            "T.price > 100",
            "--matched_upsert_set",
            "mark = 'important'",
            "--matched_delete_condition",
            "T.price < 10");

    @Test
    void extractArgs() {
        List<String> args1 = RunTimeUtil.extractArgs(
                "merge_into --warehouse hdfs:///tmp/paimon --database default --table T --source_table S --on \"T.id = S.order_id\" --merge_actions matched-upsert,matched-delete --matched_upsert_condition \"T.price > 100\" --matched_upsert_set \"mark = 'important'\"  --matched_delete_condition \"T.price < 10\"");
        Assert.assertArrayEquals(args1.toArray(new String[0]), RESULT1.toArray(new String[0]));
    }
}
