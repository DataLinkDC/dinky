package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.alert.*;
import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.AlertInstanceMapper;
import com.dlink.model.AlertInstance;
import com.dlink.service.AlertInstanceService;
import com.dlink.utils.JSONUtil;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * AlertInstanceServiceImpl
 *
 * @author wenmo
 * @since 2022/2/24 19:53
 **/
@Service
public class AlertInstanceServiceImpl extends SuperServiceImpl<AlertInstanceMapper, AlertInstance> implements AlertInstanceService {
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
}
