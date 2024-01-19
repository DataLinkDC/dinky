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

package org.dinky.trans;

import org.dinky.trans.parse.CreateTemporalTableFunctionParseStrategy;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CreateTemporalTableFunctionParseStrategyTest {

    @Test
    void getInfo() {
        String statement = "CREATE temporal temporary function func as select price, buyer from orders";
        String[] result = CreateTemporalTableFunctionParseStrategy.getInfo(statement);
        Assertions.assertArrayEquals(new String[] {"temporary", "", "func", "price", "buyer", "orders"}, result);

        statement = "CREATE temporal temporary function abc.def.func as select price, buyer from" + " orders";
        result = CreateTemporalTableFunctionParseStrategy.getInfo(statement);
        Assertions.assertArrayEquals(
                new String[] {"temporary", "", "abc.def.func", "price", "buyer", "orders"}, result);
    }
}
