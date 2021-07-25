package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.DataBaseMapper;
import com.dlink.metadata.driver.Driver;
import com.dlink.metadata.driver.DriverConfig;
import com.dlink.model.DataBase;
import com.dlink.model.Schema;
import com.dlink.service.DataBaseService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


/**
 * DataBaseServiceImpl
 *
 * @author wenmo
 * @since 2021/7/20 23:47
 */
@Service
public class DataBaseServiceImpl extends SuperServiceImpl<DataBaseMapper, DataBase> implements DataBaseService {
    @Override
    public boolean checkHeartBeat(DataBase dataBase) {
        boolean isHealthy =  Driver.build(dataBase.getDriverConfig()).test();
        dataBase.setStatus(isHealthy);
        dataBase.setHeartbeatTime(LocalDateTime.now());
        if(isHealthy){
            dataBase.setHealthTime(LocalDateTime.now());
        }
        return isHealthy;
    }

    @Override
    public boolean saveOrUpdateDataBase(DataBase dataBase) {
        if(Asserts.isNull(dataBase)){
            return false;
        }
        if(Asserts.isNull(dataBase.getId())){
            checkHeartBeat(dataBase);
            return save(dataBase);
        }else{
            DataBase dataBaseInfo = getById(dataBase.getId());
            if(Asserts.isNull(dataBase.getUrl())){
                dataBase.setUrl(dataBaseInfo.getUrl());
            }
            if(Asserts.isNull(dataBase.getUsername())){
                dataBase.setUsername(dataBaseInfo.getUsername());
            }
            if(Asserts.isNull(dataBase.getPassword())){
                dataBase.setPassword(dataBaseInfo.getPassword());
            }
            checkHeartBeat(dataBase);
            return updateById(dataBase);
        }
    }

    @Override
    public List<DataBase> listEnabledAll() {
        return this.list(new QueryWrapper<DataBase>().eq("enabled",1));
    }

    @Override
    public List<Schema> getSchemasAndTables(Integer id) {
        DataBase dataBase = getById(id);
        Asserts.checkNotNull(dataBase,"该数据源不存在！");
        Driver driver = Driver.build(dataBase.getDriverConfig()).connect();
        return driver.getSchemasAndTables();
    }
}
