package com.dlink.core;

import com.dlink.executor.Executor;
import com.dlink.executor.ExecutorSetting;
import com.dlink.job.JobManager;
import com.dlink.result.RunResult;
import com.dlink.result.SubmitResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * JobManagerTest
 *
 * @author wenmo
 * @since 2021/6/3
 **/
public class JobManagerTest {

    @Test
    public void submitJobTest2(){
        JobManager jobManager = new JobManager("192.168.123.157",8081,"test2",100);
        String sql1 ="CREATE TABLE student (\n" +
                "  sid INT,\n" +
                "  name STRING,\n" +
                "  PRIMARY KEY (sid) NOT ENFORCED\n" +
                ") WITH (\n" +
                "   'connector' = 'jdbc',\n" +
                "   'url' = 'jdbc:mysql://192.168.24.1:3306/data?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true',\n" +
                "   'username'='datalink',\n" +
                "   'password'='datalink',\n" +
                "   'table-name' = 'student'\n" +
                ")";
        String sql2 ="CREATE TABLE man (\n" +
                "  pid INT,\n" +
                "  name STRING,\n" +
                "  PRIMARY KEY (pid) NOT ENFORCED\n" +
                ") WITH (\n" +
                "   'connector' = 'jdbc',\n" +
                "   'url' = 'jdbc:mysql://192.168.24.1:3306/data?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true',\n" +
                "   'username'='datalink',\n" +
                "   'password'='datalink',\n" +
                "   'table-name' = 'man'\n" +
                ")";
        String sql3 = "INSERT INTO man SELECT sid as pid,name from student";
        List<String> sqls = new ArrayList<>();
        sqls.add(sql1);
        sqls.add(sql2);
        sqls.add(sql3);
        ExecutorSetting setting = new ExecutorSetting(Executor.REMOTE);
        SubmitResult result = jobManager.submit(sqls, setting);
        System.out.println(result.isSuccess());
    }

    @Test
    public void executeJobTest(){
        JobManager jobManager = new JobManager("192.168.123.157",8081,"test2",100);
        String sql1 ="CREATE TABLE student (\n" +
                "  sid INT,\n" +
                "  name STRING,\n" +
                "  PRIMARY KEY (sid) NOT ENFORCED\n" +
                ") WITH (\n" +
                "   'connector' = 'jdbc',\n" +
                "   'url' = 'jdbc:mysql://192.168.24.1:3306/data?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true',\n" +
                "   'username'='datalink',\n" +
                "   'password'='datalink',\n" +
                "   'table-name' = 'student'\n" +
                ");";
        String sql2 ="CREATE TABLE man (\n" +
                "  pid INT,\n" +
                "  name STRING,\n" +
                "  PRIMARY KEY (pid) NOT ENFORCED\n" +
                ") WITH (\n" +
                "   'connector' = 'jdbc',\n" +
                "   'url' = 'jdbc:mysql://192.168.24.1:3306/data?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true',\n" +
                "   'username'='datalink',\n" +
                "   'password'='datalink',\n" +
                "   'table-name' = 'man'\n" +
                ");";
        String sql3 = "SELECT sid as pid,name from student";
        List<String> sqls = new ArrayList<>();
        sqls.add(sql1);
        sqls.add(sql2);
        sqls.add(sql3);
        String sql = sql1+sql2+sql3;
        ExecutorSetting setting = new ExecutorSetting(Executor.REMOTE,0,1,false,null);
        RunResult result = jobManager.execute(sql, setting);
        System.out.println(result.isSuccess());
    }
}
