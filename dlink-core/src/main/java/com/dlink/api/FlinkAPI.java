package com.dlink.api;

import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.dlink.assertion.Asserts;
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
                break;
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
        String triggerid = json.get("request-id").asText();
        while (triggerid != null) {
            try {
                Thread.sleep(1000);
                JsonNode node = get(FlinkRestAPIConstant.JOBS + jobId + FlinkRestAPIConstant.SAVEPOINTS + NetConstant.SLASH + triggerid);
                String status = node.get("status").get("id").asText();
                if(Asserts.isEquals(status,"IN_PROGRESS")){
                    continue;
                }
                if(node.get("operation").has("failure-cause")) {
                    String failureCause = node.get("operation").get("failure-cause").toString();
                    if (Asserts.isNotNullString(failureCause)) {
                        result.fail(failureCause);
                        break;
                    }
                }
                if(node.get("operation").has("location")) {
                    String location = node.get("operation").get("location").asText();
                    List<JobInfo> jobInfos = new ArrayList<>();
                    jobInfo.setSavePoint(location);
                    jobInfos.add(jobInfo);
                    result.setJobInfos(jobInfos);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.fail(e.getMessage());
                break;
            }
        }
        return result;
    }

    public String getVersion() {
        JsonNode result = get(FlinkRestAPIConstant.FLINK_CONFIG);
        return result.get("flink-version").asText();
    }

    public JsonNode getJobInfo(String jobId) {
        return get(FlinkRestAPIConstant.JOBS+jobId);
    }

    public JsonNode getException(String jobId) {
        return get(FlinkRestAPIConstant.JOBS+jobId+FlinkRestAPIConstant.EXCEPTIONS);
    }

    public JsonNode getCheckPoints(String jobId) {
        return get(FlinkRestAPIConstant.JOBS+jobId+FlinkRestAPIConstant.CHECKPOINTS);
    }

    public JsonNode getCheckPointsConfig(String jobId) {
        return get(FlinkRestAPIConstant.JOBS+jobId+FlinkRestAPIConstant.CHECKPOINTS_CONFIG);
    }

    public JsonNode getJobsConfig(String jobId) {
        return get(FlinkRestAPIConstant.JOBS+jobId+FlinkRestAPIConstant.CONFIG);
    }
}
