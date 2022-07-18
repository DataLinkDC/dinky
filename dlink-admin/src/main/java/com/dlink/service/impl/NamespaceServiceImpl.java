package com.dlink.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.NamespaceMapper;
import com.dlink.model.Namespace;
import com.dlink.model.RoleNamespace;
import com.dlink.service.NamespaceService;
import com.dlink.service.RoleNamespaceService;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class NamespaceServiceImpl extends SuperServiceImpl<NamespaceMapper, Namespace> implements NamespaceService {

    @Autowired
    private RoleNamespaceService roleNamespaceService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteNamespaceById(JsonNode para) {
        for (JsonNode item : para) {
            Integer id = item.asInt();
            Namespace namespace = getById(id);
            if (Asserts.isNull(namespace)) {
                return Result.failed("名称空间不存在");
            }
            ProTableResult<RoleNamespace> roleNamespaceProTableResult = roleNamespaceService.selectForProTable(para);
            if (roleNamespaceProTableResult.getData().size() > 0) {
                return Result.failed("删除名称空间失败，该名称空间被角色绑定");
            }
            boolean result = removeById(id);
            if (result) {
                return Result.succeed("删除名称空间成功");
            } else {
                return Result.failed("删除名称空间失败");
            }
        }
        return Result.failed("名称空间不存在");
    }
}