package com.dlink.core;

import com.dlink.api.FlinkAPI;
import com.dlink.gateway.result.SavePointResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import java.util.List;

/**
 * FlinkRestAPITest
 *
 * @author wenmo
 * @since 2021/6/24 14:24
 **/
public class FlinkRestAPITest {

    //private String address = "192.168.123.157:8081";
    private String address = "cdh5:8081";

    @Test
    public void savepointTest(){
        //JsonNode savepointInfo = FlinkAPI.build(address).getSavepointInfo("602ad9d03b872dba44267432d1a2a3b2","04044589477a973a32e7dd53e1eb20fd");
        SavePointResult savepoints = FlinkAPI.build(address).savepoints("243b97597448edbd2e635fc3d25b1064", "trigger");
        System.out.println(savepoints.toString());
    }

    @Test
    public void selectTest(){
        List<JsonNode> jobs = FlinkAPI.build(address).listJobs();
        System.out.println(jobs.toString());
    }

    @Test
    public void stopTest(){
        FlinkAPI.build(address).stop("0727f796fcf9e07d89e724f7e15598cf");
    }


    @Test
    public void getCheckPointsDetailInfoTest(){
        JsonNode checkPointsDetailInfo = FlinkAPI.build(address).getCheckPointsConfig("178e954faaa4bf06cfbda971bb8b2957");
        System.out.println(checkPointsDetailInfo.toString());
    }

    @Test
    public void getConfigurationsDetailsInfoTest(){
        JsonNode configurationsDetailsInfo = FlinkAPI.build(address).getJobsConfig("178e954faaa4bf06cfbda971bb8b2957");
        System.out.println(configurationsDetailsInfo.toString());
    }

    @Test
    public void getExectionsInfoTest(){
        JsonNode exectionsDetailInfo = FlinkAPI.build(address).getException("178e954faaa4bf06cfbda971bb8b2957");
        System.out.println(exectionsDetailInfo.toString());
    }

    @Test
    public void getJobManagerMetricsTest(){
        JsonNode jobManagerMetrics = FlinkAPI.build(address).getJobManagerMetrics();
        System.out.println(jobManagerMetrics.toString());
    }

    @Test
    public void getJobManagerConfigTest(){
        JsonNode jobManagerConfig = FlinkAPI.build(address).getJobManagerConfig();
        System.out.println(jobManagerConfig.toString());
    }


    @Test
    public void getJobManagerLogTest(){
        String jobManagerLog  = FlinkAPI.build(address).getJobManagerLog();
        System.out.println(jobManagerLog);
    }


    @Test
    public void getJobManagerStdOutTest(){
        String jobManagerLogs = FlinkAPI.build(address).getJobManagerStdOut();
        System.out.println(jobManagerLogs);
    }

    @Test
    public void getJobManagerLogListTest(){
        JsonNode jobManagerLogList = FlinkAPI.build(address).getJobManagerLogList();
        System.out.println(jobManagerLogList.toString());
    }

    @Test
    public void getTaskManagersTest(){
        JsonNode taskManagers = FlinkAPI.build(address).getTaskManagers();
        System.out.println(taskManagers.toString());
    }

    @Test
    public void getTaskManagerMetricsTest(){
        JsonNode taskManagerMetrics= FlinkAPI.build(address).getTaskManagerMetrics("container_e34_1646992539398_0004_01_000002");
        System.out.println(taskManagerMetrics.toString());
    }


    @Test
    public void getTaskManagerLogTest(){
        String taskManagerLog= FlinkAPI.build(address).getTaskManagerLog("container_e34_1646992539398_0004_01_000002");
        System.out.println(taskManagerLog);
    }

    @Test
    public void getTaskManagerStdOutTest(){
        String taskManagerStdOut = FlinkAPI.build(address).getTaskManagerStdOut("container_e34_1646992539398_0004_01_000002");
        System.out.println(taskManagerStdOut);
    }

    @Test
    public void getTaskManagerLogListTest(){
        JsonNode taskManagerLogList= FlinkAPI.build(address).getTaskManagerLogList("container_e34_1646992539398_0004_01_000002");
        System.out.println(taskManagerLogList.toString());
    }


    @Test
    public void getTaskManagerThreadDumpTest(){
        JsonNode taskManagerThreadDump= FlinkAPI.build(address).getTaskManagerThreadDump("container_e34_1646992539398_0004_01_000002");
        System.out.println(taskManagerThreadDump.toString());
    }
}
