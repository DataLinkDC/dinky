package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.AlertGroup;

import java.util.List;

/**
 * AlertGroupService
 *
 * @author wenmo
 * @since 2022/2/24 20:00
 **/
public interface AlertGroupService extends ISuperService<AlertGroup> {

    List<AlertGroup> listEnabledAll();

    AlertGroup getAlertGroupInfo(Integer id);
}
