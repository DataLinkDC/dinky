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


package com.dlink.core;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.dlink.parser.SingleSqlParserFactory;

/**
 * SqlParserTest
 *
 * @author wenmo
 * @since 2021/6/14 17:03
 */
public class SqlParserTest {

    @Test
    public void selectTest() {
        String sql = "insert  into T SElecT id,xm as name frOm people wheRe id=1 And enabled = 1";
        Map<String, List<String>> lists = SingleSqlParserFactory.generateParser(sql);
        System.out.println(lists.toString());
    }

    @Test
    public void createAggTableTest() {
        String sql = "CREATE AGGTABLE agg1 AS \n" +
            "SELECT sid,data\n" +
            "FROM score\n" +
            "WHERE cls = 1\n" +
            "GROUP BY sid\n" +
            "AGG BY toMap(cls,score) as (data)";
        String sql2 = "\r\n" +
            "CREATE AGGTABLE aggscore AS \r\n" +
            "SELECT cls,score,rank\r\n" +
            "FROM score\r\n" +
            "GROUP BY cls\r\n" +
            "AGG BY TOP2(score) as (score,rank)";
        //sql=sql.replace("\n"," ");
        Map<String, List<String>> lists = SingleSqlParserFactory.generateParser(sql2);
        System.out.println(lists.toString());
        System.out.println(StringUtils.join(lists.get("SELECT"), ","));
    }

    @Test
    public void setTest() {
        String sql = "set table.exec.resource.default-parallelism = 2";
        Map<String, List<String>> lists = SingleSqlParserFactory.generateParser(sql);
        System.out.println(lists.toString());
    }

    @Test
    public void regTest() {
        String sql = "--并行度\n" +
            "CREATE TABLE student (\n" +
            "    sid INT,\n" +
            "    name STRING,\n" +
            "    PRIMARY KEY (sid) NOT ENFORCED\n" +
            ") WITH ${tb}";
        sql = sql.replaceAll("--([^'\r\n]{0,}('[^'\r\n]{0,}'){0,1}[^'\r\n]{0,}){0,}", "").trim();
        System.out.println(sql);
    }

    @Test
    public void createCDCSourceTest() {
        String sql = "EXECUTE CDCSOURCE demo WITH (\n" +
            "  'connector' = 'mysql-cdc',\n" +
            "  'hostname' = '10.1.51.25',\n" +
            "  'port' = '3306',\n" +
            "  'username' = 'dfly',\n" +
            "  'password' = 'Dareway@2020',\n" +
            "  'checkpoint' = '3000',\n" +
            "  'scan.startup.mode' = 'initial',\n" +
            "  'parallelism' = '1',\n" +
            "  -- 'database-name'='test',\n" +
            "  'table-name' = 'test\\.student,\n" +
            " test\\.score',\n" +
            "  -- 'sink.connector'='datastream-doris',\n" +
            "  'sink.connector' = 'doris',\n" +
            "  'sink.fenodes' = '10.1.51.26:8030',\n" +
            "  'sink.username' = 'root',\n" +
            "  'sink.password' = 'dw123456',\n" +
            "  'sink.sink.batch.size' = '1',\n" +
            "  'sink.sink.max-retries' = '1',\n" +
            "  'sink.sink.batch.interval' = '60000',\n" +
            "  'sink.sink.db' = 'test',\n" +
            "  'sink.table.prefix' = 'ODS_',\n" +
            "  'sink.table.upper' = 'true',\n" +
            "  'sink.table.identifier' = '${schemaName}.${tableName}',\n" +
            "  'sink.sink.enable-delete' = 'true'\n" +
            ");";
        Map<String, List<String>> lists = SingleSqlParserFactory.generateParser(sql);
        System.out.println(lists.toString());
    }


    @Test
    public void showFragmentTest() {
        String sql = "show fragment test";
        Map<String, List<String>> lists = SingleSqlParserFactory.generateParser(sql);
        System.out.println(lists.toString());
    }
}
