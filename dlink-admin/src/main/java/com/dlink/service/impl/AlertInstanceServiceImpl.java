package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.alert.*;
import com.dlink.assertion.Asserts;
import com.dlink.common.result.Result;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.AlertInstanceMapper;
import com.dlink.model.AlertGroup;
import com.dlink.model.AlertInstance;
import com.dlink.service.AlertGroupService;
import com.dlink.service.AlertInstanceService;
import com.dlink.utils.JSONUtil;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AlertInstanceServiceImpl
 *
 * @author wenmo
 * @since 2022/2/24 19:53
 **/
@Service
public class AlertInstanceServiceImpl extends SuperServiceImpl<AlertInstanceMapper, AlertInstance> implements AlertInstanceService {

    @Autowired
    private AlertGroupService alertGroupService;

    @Override
    public List<AlertInstance> listEnabledAll() {
        return list(new QueryWrapper<AlertInstance>().eq("enabled", 1));
    }

    @Override
    public AlertResult testAlert(AlertInstance alertInstance) {
        AlertConfig alertConfig = AlertConfig.build(alertInstance.getName(), alertInstance.getType(), JSONUtil.toMap(alertInstance.getParams()));
        Alert alert = Alert.buildTest(alertConfig);
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        String uuid = UUID.randomUUID().toString();

        AlertMsg alertMsg = new AlertMsg();
        alertMsg.setAlertType("实时告警监控");
        alertMsg.setAlertTime(currentDateTime);
        alertMsg.setJobID(uuid);
        alertMsg.setJobName("测试任务");
        alertMsg.setJobType("SQL");
        alertMsg.setJobStatus("FAILED");
        alertMsg.setJobStartTime(currentDateTime);
        alertMsg.setJobEndTime(currentDateTime);
        alertMsg.setJobDuration("1 Seconds");
        String linkUrl = "http://cdh1:8081/#/job/"+ uuid+"/overview";
        String exceptionUrl = "http://cdh1:8081/#/job/"+uuid+"/exceptions";

        Map<String, String> map = JSONUtil.toMap(alertInstance.getParams());
        if ( map.get("msgtype").equals(ShowType.MARKDOWN.getValue())) {
            alertMsg.setLinkUrl("[跳转至该任务的 FlinkWeb](" + linkUrl + ")");
            alertMsg.setExceptionUrl("[点击查看该任务的异常日志](" + exceptionUrl + ")");
        }else {
            alertMsg.setLinkUrl(linkUrl);
            alertMsg.setExceptionUrl(exceptionUrl);
        }
        String title = "任务【"+alertMsg.getJobName()+"】：" +alertMsg.getJobStatus() + "!";
        return alert.send(title, alertMsg.toString());
    }


    @Override
    public Result deleteAlertInstance(JsonNode para) {
        if (para.size() > 0) {
            final Map<Integer, Set<Integer>> alertGroupInformation = getAlertGroupInformation();
            final List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!this.removeById(id)) {
                    error.add(id);
                }
                alertGroupInformation.remove(id);
            }
            writeBackGroupInformation(alertGroupInformation);
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error.toString() + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    private void writeBackGroupInformation(Map<Integer, Set<Integer>> alertGroupInformation){
        if (MapUtils.isEmpty(alertGroupInformation)){
            return;
        }
        final Map<Integer, String> result = new HashMap<>(8);
        for (Map.Entry<Integer, Set<Integer>> entry : alertGroupInformation.entrySet()) {
            final Set<Integer> groupIdSet = entry.getValue();
            for (Integer groupId : groupIdSet) {
                final String instanceIdString = result.get(groupId);
                result.put(groupId, instanceIdString == null ? "" + entry.getKey()
                        : instanceIdString + "," + entry.getKey());
            }
        }
        updateAlertGroupInformation(result);
    }

    private void updateAlertGroupInformation(Map<Integer, String> result) {
        final LocalDateTime now = LocalDateTime.now();
        final List<AlertGroup> list = result.entrySet().stream().map(entry -> {
            final AlertGroup alertGroup = new AlertGroup();
            alertGroup.setId(entry.getKey());
            alertGroup.setAlertInstanceIds(entry.getValue());
            alertGroup.setUpdateTime(now);
            return alertGroup;
        }).collect(Collectors.toList());
        alertGroupService.updateBatchById(list);
    }


    private Map<Integer, Set<Integer>> getAlertGroupInformation(){
        final LambdaQueryWrapper<AlertGroup> select = new LambdaQueryWrapper<AlertGroup>()
                .select(AlertGroup::getId, AlertGroup::getAlertInstanceIds);
        final List<AlertGroup> list = alertGroupService.list(select);
        if (CollectionUtils.isEmpty(list)){
            return new HashMap<>(0);
        }
        final Map<Integer, Set<Integer>> map = new HashMap<>(list.size());
        for (AlertGroup alertGroup : list) {
            buildGroup(map, alertGroup);
        }
        return map;
    }

    private void buildGroup(Map<Integer, Set<Integer>> map, AlertGroup alertGroup) {
        if (StringUtils.isBlank(alertGroup.getAlertInstanceIds())){
            return;
        }
        for (String instanceId : alertGroup.getAlertInstanceIds().split(",")) {
            if (StringUtils.isBlank(instanceId)){
                continue;
            }
            final Integer instanceIdInt = Integer.valueOf(instanceId);
            Set<Integer> groupIdSet = map.get(instanceIdInt);
            if (CollectionUtils.isEmpty(groupIdSet)){
                groupIdSet = new HashSet<>();
                map.put(instanceIdInt, groupIdSet);
            }
            groupIdSet.add(alertGroup.getId());
        }
    }
}
