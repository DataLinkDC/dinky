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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SqlUtilTest {

    @Test
    void removeNote() {
        String sql =
                "-- a \n"
                        + " --- b \n"
                        + " --- -- \n"
                        + " # abc \n"
                        + " # \n"
                        + " /* efg */ \n"
                        + " /** xyz*/ \n"
                        + " /**pdf*/ \n"
                        + " 'ccc"
                        + "--'a\n"
                        + "'"
                        + "kcf/*df/*/ \n"
                        + " where is '--abc'";
        String result = SqlUtil.removeNote(sql);
        System.out.println(result);
    }
}
