package com.dlink.core;

import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.explainer.ca.CABuilder;
import com.dlink.explainer.ca.TableCANode;
import com.dlink.explainer.ca.TableCAResult;
import com.dlink.job.JobManager;
import com.dlink.parser.SingleSqlParserFactory;
import com.dlink.plus.FlinkSqlPlus;
import com.dlink.result.SubmitResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FlinkSqlPlusTest
 *
 * @author wenmo
 * @since 2021/6/23 10:37
 **/
public class FlinkSqlPlusTest {
    @Test
    public void tableCATest(){
        String sql1 ="CREATE TABLE student (\n" +
                "  sid INT,\n" +
                "  name STRING,\n" +
                "  PRIMARY KEY (sid) NOT ENFORCED\n" +
                ") WITH (\n" +
                "   'connector' = 'jdbc',\n" +
                "   'url' = 'jdbc:mysql://10.1.51.25:3306/dataxweb?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true',\n" +
                "   'username'='dfly',\n" +
                "   'password'='Dareway',\n" +
                "   'table-name' = 'student'\n" +
                ")";
        String sql2 ="CREATE TABLE man (\n" +
                "  pid INT,\n" +
                "  name STRING,\n" +
                "  PRIMARY KEY (pid) NOT ENFORCED\n" +
                ") WITH (\n" +
                "   'connector' = 'jdbc',\n" +
                "   'url' = 'jdbc:mysql://10.1.51.25:3306/dataxweb?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true',\n" +
                "   'username'='dfly',\n" +
                "   'password'='Dareway',\n" +
                "   'table-name' = 'man'\n" +
                ")";
        String sql3 = "INSERT INTO man SELECT sid as pid,name from student";
        List<String> sqls = new ArrayList<>();
        sqls.add(sql1);
        sqls.add(sql2);
        sqls.add(sql3);
        FlinkSqlPlus plus = FlinkSqlPlus.build();
//        List<TableCAResult> tableCAResults = plus.explainSqlTableColumnCA(String.join(";", sqls));
//        List<TableCANode> tableCANodes = CABuilder.getOneTableCASByStatement(String.join(";", sqls));
        List<TableCANode> tableCANodes = CABuilder.getOneTableColumnCAByStatement(String.join(";", sqls));
        System.out.println(tableCANodes.toString());
    }
}
