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
import org.apache.flink.runtime.rest.messages.JobPlanInfo;
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
    public void getJobPlanInfo(){
        String sql = "jdbcconfig:='connector' = 'jdbc',\n" +
                "    'url' = 'jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true',\n" +
                "    'username'='dlink',\n" +
                "    'password'='dlink',;\n" +
                "create temporary function TOP2 as 'com.dlink.ud.udtaf.Top2';\n" +
                "CREATE TABLE student (\n" +
                "    sid INT,\n" +
                "    name STRING,\n" +
                "    PRIMARY KEY (sid) NOT ENFORCED\n" +
                ") WITH (\n" +
                "    ${jdbcconfig}\n" +
                "    'table-name' = 'student'\n" +
                ");\n" +
                "CREATE TABLE score (\n" +
                "    cid INT,\n" +
                "    sid INT,\n" +
                "    cls STRING,\n" +
                "    score INT,\n" +
                "    PRIMARY KEY (cid) NOT ENFORCED\n" +
                ") WITH (\n" +
                "    ${jdbcconfig}\n" +
                "    'table-name' = 'score'\n" +
                ");\n" +
                "CREATE TABLE scoretop2 (\n" +
                "    cls STRING,\n" +
                "    score INT,\n" +
                "    `rank` INT,\n" +
                "    PRIMARY KEY (cls,`rank`) NOT ENFORCED\n" +
                ") WITH (\n" +
                "    ${jdbcconfig}\n" +
                "    'table-name' = 'scoretop2'\n" +
                ");\n" +
                "CREATE AGGTABLE aggscore AS \n" +
                "SELECT cls,score,rank\n" +
                "FROM score\n" +
                "GROUP BY cls\n" +
                "AGG BY TOP2(score) as (score,rank);\n" +
                "\n" +
                "insert into scoretop2\n" +
                "select \n" +
                "b.cls,b.score,b.`rank`\n" +
                "from aggscore b";

        FlinkSqlPlus plus = FlinkSqlPlus.build();
        JobPlanInfo jobPlanInfo = plus.getJobPlanInfo(sql);
        System.out.println(jobPlanInfo.getJsonPlan());
    }
}
