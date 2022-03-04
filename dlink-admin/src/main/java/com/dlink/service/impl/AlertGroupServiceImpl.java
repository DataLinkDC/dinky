package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.AlertGroupMapper;
import com.dlink.model.AlertGroup;
import com.dlink.model.AlertInstance;
import com.dlink.service.AlertGroupService;
import com.dlink.service.AlertInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * AlertGroupServiceImpl
 *
 * @author wenmo
 * @since 2022/2/24 20:01
 **/
@Service
public class AlertGroupServiceImpl extends SuperServiceImpl<AlertGroupMapper, AlertGroup> implements AlertGroupService {

    @Autowired
    private AlertInstanceService alertInstanceService;

    @Override
    public List<AlertGroup> listEnabledAll() {
        return list(new QueryWrapper<AlertGroup>().eq("enabled",1));
    }

    @Override
    public AlertGroup getAlertGroupInfo(Integer id) {
        AlertGroup alertGroup = getById(id);
        if(Asserts.isNull(alertGroup)||Asserts.isNullString(alertGroup.getAlertInstanceIds())){
            return alertGroup;
        }
        String[] alertInstanceIds = alertGroup.getAlertInstanceIds().split(",");
        List<AlertInstance> alertInstanceList = new ArrayList<>();
        for(String alertInstanceId: alertInstanceIds){
            if(Asserts.isNullString(alertInstanceId)||alertInstanceId.equals("0")){
                continue;
            }
            alertInstanceList.add(alertInstanceService.getById(Integer.valueOf(alertInstanceId)));
        }
        alertGroup.setInstances(alertInstanceList);
        return alertGroup;
    }
}
