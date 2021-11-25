package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.Jar;

import java.util.List;

/**
 * JarService
 *
 * @author wenmo
 * @since 2021/11/13
 **/
public interface JarService extends ISuperService<Jar> {

    List<Jar> listEnabledAll();
}
