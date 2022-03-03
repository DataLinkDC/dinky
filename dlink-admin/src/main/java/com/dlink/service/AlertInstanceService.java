package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.AlertInstance;

import java.util.List;

/**
 * AlertInstanceService
 *
 * @author wenmo
 * @since 2022/2/24 19:52
 **/
public interface AlertInstanceService extends ISuperService<AlertInstance> {

    List<AlertInstance> listEnabledAll();
}
