package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.DataBase;

import java.util.List;

/**
 * DataBaseService
 *
 * @author wenmo
 * @since 2021/7/20 23:47
 */
public interface DataBaseService extends ISuperService<DataBase> {

    boolean checkHeartBeat(DataBase dataBase);

    boolean saveOrUpdateDataBase(DataBase dataBase);

    List<DataBase> listEnabledAll();
}
