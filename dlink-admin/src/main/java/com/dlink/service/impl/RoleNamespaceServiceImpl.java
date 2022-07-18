package com.dlink.service.impl;

import org.springframework.stereotype.Service;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.RoleNamespaceMapper;
import com.dlink.model.RoleNamespace;
import com.dlink.service.RoleNamespaceService;

@Service
public class RoleNamespaceServiceImpl extends SuperServiceImpl<RoleNamespaceMapper, RoleNamespace> implements RoleNamespaceService {

}