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

package org.dinky.explainer.trans;

import org.dinky.utils.MapParseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SinkTrans
 *
 * @author wenmo
 * @since 2021/6/22
 */
public class SinkTrans extends AbstractTrans implements Trans {

    private String catalog;
    private String database;
    private String table;
    private List<String> fields;

    public static final String TRANS_TYPE = "Data Sink";

    public SinkTrans() {}

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    @Override
    public String getHandle() {
        return TRANS_TYPE;
    }

    @Override
    public boolean canHandle(String pact) {
        return TRANS_TYPE.equals(pact);
    }

    @Override
    public void translate() {
        Map map = MapParseUtils.parse(contents);
        ArrayList<String> tables = (ArrayList<String>) map.get("table");
        if (tables != null && tables.size() > 0) {
            name = tables.get(0);
            String[] names = tables.get(0).split("\\.");
            if (names.length >= 3) {
                catalog = names[0];
                database = names[1];
                table = names[2];
            } else {
                table = name;
            }
        }
        fields = (ArrayList<String>) map.get("fields");
    }

    @Override
    public String asSummaryString() {
        return "";
    }
}
