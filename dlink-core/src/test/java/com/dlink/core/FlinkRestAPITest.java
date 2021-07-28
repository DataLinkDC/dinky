package com.dlink.core;

import com.dlink.api.FlinkAPI;
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

    private String address = "192.168.123.157:8081";
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
