package com.dlink.core;

import com.dlink.api.FlinkAPI;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import java.util.List;

/**
 * FlinkRestAPITest
 *
 * @author wenmo
 * @since 2021/6/24 14:24
 **/
public class FlinkRestAPITest {

    private String address = "127.0.0.1:8081";
    @Test
    public void selectTest(){
        List<JsonNode> jobs = FlinkAPI.build(address).listJobs();
        System.out.println(jobs.toString());
    }
}
