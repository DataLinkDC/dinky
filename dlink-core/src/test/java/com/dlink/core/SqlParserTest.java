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
        //sql=sql.replace("\n"," ");
        Map<String,List<String>> lists = SingleSqlParserFactory.generateParser(sql);
        System.out.println(lists.toString());
        System.out.println(StringUtils.join(lists.get("SELECT"),","));
    }
}
