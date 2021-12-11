package com.dlink.api;

import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.dlink.constant.FlinkRestAPIConstant;
import com.dlink.constant.NetConstant;
import com.dlink.gateway.GatewayType;
import com.dlink.gateway.config.SavePointType;
import com.dlink.gateway.model.JobInfo;
import com.dlink.gateway.result.SavePointResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private JsonNode get(String route){
        String res = HttpUtil.get(NetConstant.HTTP + address + NetConstant.SLASH + route, NetConstant.SERVER_TIME_OUT_ACTIVE);
        return parse(res);
    }

    private JsonNode post(String route, String body) {
        String res = HttpUtil.post(NetConstant.HTTP + address + NetConstant.SLASH + route, body, NetConstant.SERVER_TIME_OUT_ACTIVE);
        return parse(res);
    }

    private JsonNode patch(String route, String body) {
        String res = HttpUtil.createRequest(Method.PATCH,NetConstant.HTTP + address + NetConstant.SLASH + route).timeout(NetConstant.SERVER_TIME_OUT_ACTIVE).body(body).execute().body();
        return parse(res);
    }

    public List<JsonNode> listJobs() {
        JsonNode result = get(FlinkRestAPIConstant.JOBSLIST);
        JsonNode jobs = result.get("jobs");
        List<JsonNode> joblist = new ArrayList<>();
        if (jobs.isArray()) {
            for (final JsonNode objNode : jobs) {
                joblist.add(objNode);
            }
        }
        return joblist;
    }

    public boolean stop(String jobId){
        get(FlinkRestAPIConstant.JOBS+jobId+FlinkRestAPIConstant.CANCEL);
        return true;
    }

    public SavePointResult savepoints(String jobId, String savePointType){
        SavePointType type = SavePointType.get(savePointType);
        String paramType = null;
        SavePointResult result = SavePointResult.build(GatewayType.YARN_PER_JOB);
        JobInfo jobInfo = new JobInfo(jobId);
        Map<String, Object> paramMap = new HashMap<>();
        switch (type){
            case CANCEL:
                jobInfo.setStatus(JobInfo.JobStatus.CANCEL);
            case STOP:
                paramMap.put("drain",false);
                paramType = FlinkRestAPIConstant.STOP;
                jobInfo.setStatus(JobInfo.JobStatus.STOP);
                break;
            case TRIGGER:
                paramMap.put("cancel-job",false);
                //paramMap.put("target-directory","hdfs:///flink13/ss1");
                paramType = FlinkRestAPIConstant.SAVEPOINTS;
                jobInfo.setStatus(JobInfo.JobStatus.RUN);
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = null;
        try {
            String s = mapper.writeValueAsString(paramMap);
            json = post(FlinkRestAPIConstant.JOBS + jobId + paramType, s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String triggerid = json.get("request-id").asText();
        while (triggerid != null)
        {
            try {
                JsonNode node = get(FlinkRestAPIConstant.JOBS + jobId + FlinkRestAPIConstant.SAVEPOINTS + NetConstant.SLASH + triggerid);
                JsonNode operation = node.get("operation");
                String location = operation.get("location").toString();
                List<JobInfo> jobInfos = new ArrayList<>();
                jobInfo.setSavePoint(location);
                jobInfos.add(jobInfo);
                result.setJobInfos(jobInfos);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                result.fail(e.getMessage());
                break;
            }
        }
        return result;
    }

    public String getVersion() {
        JsonNode result = get(FlinkRestAPIConstant.CONFIG);
        return result.get("flink-version").asText();
    }
}
