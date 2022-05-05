package com.dlink.service.impl;

import org.springframework.stereotype.Service;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.RoleResourceMapper;
import com.dlink.model.RoleResource;
import com.dlink.service.RoleResourceService;

@Service
public class RoleResourceServiceImpl extends SuperServiceImpl<RoleResourceMapper, RoleResource> implements RoleResourceService {
}
