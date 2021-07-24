package com.dlink.api;

import cn.hutool.http.HttpUtil;
import com.dlink.constant.FlinkRestAPIConstant;
import com.dlink.constant.NetConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * FlinkAPI
 *
 * @author wenmo
 * @since 2021/6/24 13:56
 **/
public class FlinkAPI {
    private String address;
    private String jarsId;
    private String jobId;

    public FlinkAPI(String address) {
        this.address = address;
    }

    public static FlinkAPI build(String address) {
        return new FlinkAPI(address);
    }

    private JsonNode parse(String res) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode result = null;
        try {
            result = mapper.readTree(res);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }


    private JsonNode get(String route) {
        String res = HttpUtil.get(NetConstant.HTTP + address + NetConstant.SLASH + route, NetConstant.SERVER_TIME_OUT_ACTIVE);
        return parse(res);
    }

    private JsonNode post(String route, String body) {
        String res = HttpUtil.post(NetConstant.HTTP + address + NetConstant.SLASH + route, body, NetConstant.SERVER_TIME_OUT_ACTIVE);
        return parse(res);
    }

    public List<JsonNode> listJobs() {
        JsonNode result = get(FlinkRestAPIConstant.JOBS);
        JsonNode jobs = result.get("jobs");
        List<JsonNode> joblist = new ArrayList<>();
        if (jobs.isArray()) {
            for (final JsonNode objNode : jobs) {
                joblist.add(objNode);
            }
        }
        return joblist;
    }
}
