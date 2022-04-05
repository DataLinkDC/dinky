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
    public AlertResult testAlert(AlertInstance alertInstance) {
        AlertConfig alertConfig = AlertConfig.build(alertInstance.getName(), alertInstance.getType(), JSONUtil.toMap(alertInstance.getParams()));
        Alert alert = Alert.buildTest(alertConfig);
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        String testSendMsg = "[{\"type\":\"Flink 实时监控\"," +
            "\"time\":\"" + currentDateTime + "\"," +
            "\"id\":\"" + UUID.randomUUID() + "\"," +
            "\"name\":\"此信息仅用于测试告警信息是否发送正常 ! 请忽略此信息！\"," +
            "\"status\":\"Test\"," +
            "\"content\" :\"" + UUID.randomUUID() + "\"}]";
        List<AlertMsg> lists = JSONUtil.toList(testSendMsg, AlertMsg.class);
        String title = "任务【测试任务】：" + alertInstance.getType() + " 报警 !";
        String content = JSONUtil.toJsonString(lists);
        return alert.send(title, content);
    }
}
