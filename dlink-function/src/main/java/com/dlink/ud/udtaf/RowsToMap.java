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

package com.dlink.ud.udtaf;

import org.apache.flink.table.functions.TableAggregateFunction;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * RowsToMap
 *
 * @author wenmo
 * @since 2021/5/25 15:50
 **/
public class RowsToMap extends TableAggregateFunction<String, Map> {
    @Override
    public Map createAccumulator() {
        return new HashMap();
    }

    public void accumulate(Map acc, String cls, Object v, String key) {
        String[] keys = key.split(",");
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(cls)) {
                acc.put(cls, v);
            }
        }
    }

    public void accumulate(Map acc, String cls, Object v) {
        acc.put(cls, v);
    }

    public void merge(Map acc, Iterable<Map> iterable) {
        for (Map otherAcc : iterable) {
            Iterator iter = otherAcc.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                accumulate(acc, entry.getKey().toString(), entry.getValue());
            }
        }
    }

    public void emitValue(Map acc, Collector<String> out) {
        out.collect(acc.toString());
    }
}
