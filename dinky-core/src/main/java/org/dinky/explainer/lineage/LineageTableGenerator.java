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

package org.dinky.explainer.lineage;

import org.dinky.assertion.Asserts;
import org.dinky.explainer.ca.TableCA;
import org.dinky.explainer.trans.OperatorTrans;
import org.dinky.explainer.trans.SinkTrans;
import org.dinky.explainer.trans.SourceTrans;
import org.dinky.explainer.trans.Trans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LineageTableGenerator
 *
 * @author wenmo
 * @since 2022/3/16 19:56
 */
public class LineageTableGenerator {

    private Map<Integer, Trans> transMaps;
    private List<TableCA> tables = new ArrayList<>();

    public LineageTableGenerator() {}

    public static LineageTableGenerator build(List<Trans> transList) {
        LineageTableGenerator generator = new LineageTableGenerator();
        Map<Integer, Trans> map = new HashMap<>();
        for (Trans trans : transList) {
            map.put(trans.getId(), trans);
        }
        generator.setTransMaps(map);
        return generator;
    }

    public void translate() {
        for (Map.Entry<Integer, Trans> entry : transMaps.entrySet()) {
            if (entry.getValue() instanceof SourceTrans) {
                tables.add(TableCA.build(entry.getValue()));
            } else if (entry.getValue() instanceof SinkTrans) {
                tables.add(TableCA.build(entry.getValue()));
            } else if (entry.getValue() instanceof OperatorTrans) {
                TableCA tableCA = TableCA.build(entry.getValue());
                if (Asserts.isNotNull(tableCA)) {
                    tables.add(tableCA);
                }
            }
        }
    }

    public Map<Integer, Trans> getTransMaps() {
        return transMaps;
    }

    public void setTransMaps(Map<Integer, Trans> transMaps) {
        this.transMaps = transMaps;
    }

    public List<TableCA> getTables() {
        return tables;
    }

    public void setTables(List<TableCA> tables) {
        this.tables = tables;
    }
}
