package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.DataBase;

/**
 * DataBaseService
 *
 * @author wenmo
 * @since 2021/7/20 23:47
 */
public interface DataBaseService extends ISuperService<DataBase> {

    boolean checkHeartBeat(DataBase dataBase);

    boolean saveOrUpdateDataBase(DataBase dataBase);
}
