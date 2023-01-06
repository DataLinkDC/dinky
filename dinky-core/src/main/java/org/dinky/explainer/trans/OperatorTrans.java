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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OperatorTrans
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public class OperatorTrans extends AbstractTrans implements Trans {

    private List<Field> select;
    private List<String> table;
    private List<String> fields;
    private List<String> joinType;
    private List<String> lookup;
    private String where;
    private List<String> leftInputSpec;
    private List<String> rightInputSpec;

    public static final String TRANS_TYPE = "Operator";
    private static final String FIELD_AS = " AS ";

    public List<Field> getSelect() {
        return select;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<String> getJoinType() {
        return joinType;
    }

    public String getWhere() {
        return where;
    }

    public List<String> getLeftInputSpec() {
        return leftInputSpec;
    }

    public List<String> getRightInputSpec() {
        return rightInputSpec;
    }

    public List<String> getTable() {
        return table;
    }

    public List<String> getLookup() {
        return lookup;
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
        name = pact;
        Map map = MapParseUtils.parseForSelect(contents);
        translateSelect((ArrayList<String>) map.get("select"));
        fields = (ArrayList<String>) map.get("fields");
        table = (ArrayList<String>) map.get("table");
        joinType = (ArrayList<String>) map.get("joinType");
        lookup = (ArrayList<String>) map.get("lookup");
        where = map.containsKey("where") ? map.get("where").toString() : null;
        leftInputSpec = (ArrayList<String>) map.get("leftInputSpec");
        rightInputSpec = (ArrayList<String>) map.get("rightInputSpec");
    }

    private void translateSelect(ArrayList<String> fieldStrs) {
        if (fieldStrs != null && fieldStrs.size() > 0) {
            select = new ArrayList<>();
            for (int i = 0; i < fieldStrs.size(); i++) {
                String fieldStr = fieldStrs.get(i).trim();
                if (fieldStr.toUpperCase().contains(FIELD_AS)) {
                    String[] fieldNames = fieldStr.split(FIELD_AS);
                    if (fieldNames.length == 2) {
                        select.add(new Field(fieldNames[0].trim(), fieldNames[1].trim()));
                    } else if (fieldNames.length == 1) {
                        select.add(new Field(fieldNames[0].trim()));
                    } else {
                        List<String> fieldNameList = new ArrayList<>();
                        for (int j = 0; j < fieldNames.length - 1; j++) {
                            fieldNameList.add(fieldNames[j]);
                        }
                        select.add(new Field(StringUtils.join(fieldNameList, FIELD_AS).trim(), fieldNames[fieldNames.length - 1].trim()));
                    }
                } else {
                    select.add(new Field(fieldStr));
                }
            }
        }
    }

    @Override
    public String asSummaryString() {
        return null;
    }
}
