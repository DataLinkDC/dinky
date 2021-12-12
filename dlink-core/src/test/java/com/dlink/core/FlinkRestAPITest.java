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
    private String address = "node02:45659";

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
}
