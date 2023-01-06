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

package com.dlink.explainer.trans;

import com.dlink.utils.MapParseUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.operations.Operation;
import org.apache.flink.table.operations.OperationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SourceTrans
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class SourceTrans extends AbstractTrans implements Trans {

    private String catalog;
    private String database;
    private String table;
    private List<String> project;
    private List<String> fields;

    public static final String TRANS_TYPE = "Data Source";

    public SourceTrans() {
    }

    public String getCatalog() {
        return catalog;
    }

    public String getDatabase() {
        return database;
    }

    public String getTable() {
        return table;
    }

    public List<String> getProject() {
        return project;
    }

    public List<String> getFields() {
        return fields;
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
    public String asSummaryString() {

        Map<String, Object> params = new LinkedHashMap<>();
        /*params.put("originalQuery", catalogView.getOriginalQuery());
        params.put("expandedQuery", catalogView.getExpandedQuery());
        params.put("identifier", viewIdentifier);
        params.put("ignoreIfExists", ignoreIfExists);
        params.put("isTemporary", isTemporary);*/
        return OperationUtils.formatWithChildren(
                "CREATE VIEW", params, Collections.emptyList(), Operation::asSummaryString);
    }

    @Override
    public void translate() {
        Map map = MapParseUtils.parse(contents);
        ArrayList<ArrayList<Object>> tables = (ArrayList<ArrayList<Object>>) map.get("table");
        ArrayList<Object> names = tables.get(0);
        if (names.size() == 4) {
            project = (ArrayList<String>) ((Map) names.get(3)).get("project");
            names.remove(3);
        }
        name = StringUtils.join(names, ".");
        if (names.size() >= 3) {
            catalog = names.get(0).toString();
            database = names.get(1).toString();
            table = names.get(2).toString();
        } else {
            table = name;
        }
        fields = (ArrayList<String>) map.get("fields");
    }

}
