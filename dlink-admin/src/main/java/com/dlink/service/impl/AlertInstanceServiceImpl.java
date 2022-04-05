package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.alert.*;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.AlertInstanceMapper;
import com.dlink.model.AlertInstance;
import com.dlink.service.AlertInstanceService;
import com.dlink.utils.JSONUtil;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
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
    public AlertResult getAlerTesttResult(AlertInstance alertInstance) {
        AlertConfig alertConfig =null;
        Alert alert = null;
        if (!AlertPool.exist(alertInstance.getName())) {
            alertConfig = AlertConfig.build(alertInstance.getName(), alertInstance.getType(), JSONUtil.toMap(alertInstance.getParams()));
            alert = Alert.build(alertConfig);
            AlertPool.push(alertInstance.getName(), alert);
        }else {
            alert = AlertPool.get(alertInstance.getName());
        }
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        String testSendMsg = "[{\"type\":\"Flink 实时监控\"," +
                "\"time\":\""+currentDateTime+"\"," +
                "\"id\":\""+ UUID.randomUUID() +"\"," +
                "\"name\":\"此信息仅用于测试告警信息是否发送正常 ! 请忽略此信息！\"," +
                "\"status\":\"Test\"," +
                "\"content\" :\""+ UUID.randomUUID() +"\"}]";
        List<AlertMsg> lists = JSONUtil.toList(testSendMsg, AlertMsg.class);
        String title = "任务【测试任务】：" + alertInstance.getType() + " 报警 !";
        String content = JSONUtil.toJsonString(lists);
        AlertResult alertResult = alert.send(title, content);
        return alertResult;
    }
}
