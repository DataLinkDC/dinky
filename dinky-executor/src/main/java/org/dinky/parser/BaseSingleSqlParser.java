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

package org.dinky.parser;

import org.dinky.assertion.Asserts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BaseSingleSqlParser
 *
 * @author wenmo
 * @since 2021/6/14 16:43
 */
public abstract class BaseSingleSqlParser {

    //原始Sql语句
    protected String originalSql;

    //Sql语句片段
    protected List<SqlSegment> segments;

    /**
     * 构造函数，传入原始Sql语句，进行劈分。
     **/
    public BaseSingleSqlParser(String originalSql) {
        this.originalSql = originalSql;
        segments = new ArrayList<SqlSegment>();
        initializeSegments();
    }

    /**
     * 初始化segments，强制子类实现
     **/
    protected abstract void initializeSegments();

    /**
     * 将originalSql劈分成一个个片段
     **/
    protected Map<String, List<String>> splitSql2Segment() {
        Map<String, List<String>> map = new HashMap<>();
        for (SqlSegment sqlSegment : segments) {
            sqlSegment.parse(originalSql);
            if (Asserts.isNotNullString(sqlSegment.getStart())) {
                map.put(sqlSegment.getType().toUpperCase(), sqlSegment.getBodyPieces());
            }
        }
        return map;
    }

    /**
     * 得到解析完毕的Sql语句
     **/
    public String getParsedSql() {
        StringBuffer sb = new StringBuffer();
        for (SqlSegment sqlSegment : segments) {
            sb.append(sqlSegment.getParsedSqlSegment() + "\n");
        }
        String retval = sb.toString().replaceAll("\n+", "\n");
        return retval;
    }

}
