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


package com.dlink.metadata.driver;

import com.dlink.metadata.convert.ITypeConvert;
import com.dlink.metadata.convert.MySqlTypeConvert;
import com.dlink.metadata.query.IDBQuery;
import com.dlink.metadata.query.MySqlQuery;

import java.util.HashMap;
import java.util.Map;

/**
 * MysqlDriver
 *
 * @author wenmo
 * @since 2021/7/20 14:06
 **/
public class MySqlDriver extends AbstractJdbcDriver {

    @Override
    public IDBQuery getDBQuery() {
        return new MySqlQuery();
    }

    @Override
    public ITypeConvert getTypeConvert() {
        return new MySqlTypeConvert();
    }

    @Override
    public String getType() {
        return "MySql";
    }

    @Override
    public String getName() {
        return "MySql数据库";
    }

    @Override
    public String getDriverClass() {
        return "com.mysql.cj.jdbc.Driver";
    }

    @Override
    public Map<String, String> getFlinkColumnTypeConversion() {
        HashMap<String, String> map = new HashMap<>();
        map.put("VARCHAR", "STRING");
        map.put("TEXT", "STRING");
        map.put("INT", "INT");
        map.put("DATETIME", "TIMESTAMP");
        return map;
    }
}
