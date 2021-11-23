package com.dlink.core;

import com.dlink.parser.SingleSqlParserFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * SqlParserTest
 *
 * @author wenmo
 * @since 2021/6/14 17:03
 */
public class SqlParserTest {

    @Test
    public void selectTest(){
        String sql = "insert  into T SElecT id,xm as name frOm people wheRe id=1 And enabled = 1";
        Map<String,List<String>> lists = SingleSqlParserFactory.generateParser(sql);
        System.out.println(lists.toString());
    }

    @Test
    public void createAggTableTest(){
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
        Map<String,List<String>> lists = SingleSqlParserFactory.generateParser(sql2);
        System.out.println(lists.toString());
        System.out.println(StringUtils.join(lists.get("SELECT"),","));
    }

    @Test
    public void setTest(){
        String sql = "set table.exec.resource.default-parallelism = 2";
        Map<String,List<String>> lists = SingleSqlParserFactory.generateParser(sql);
        System.out.println(lists.toString());
    }

    @Test
    public void regTest(){
        String sql = "--并行度\n" +
                "CREATE TABLE student (\n" +
                "    sid INT,\n" +
                "    name STRING,\n" +
                "    PRIMARY KEY (sid) NOT ENFORCED\n" +
                ") WITH ${tb}";
        sql=sql.replaceAll("--([^'\r\n]{0,}('[^'\r\n]{0,}'){0,1}[^'\r\n]{0,}){0,}","").trim();
        System.out.println(sql);
    }
}
