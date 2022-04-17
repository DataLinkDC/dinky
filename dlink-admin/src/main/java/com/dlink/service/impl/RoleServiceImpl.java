package com.dlink.service.impl;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.RoleMapper;
import com.dlink.model.Role;
import com.dlink.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends SuperServiceImpl<RoleMapper, Role> implements RoleService {

}
