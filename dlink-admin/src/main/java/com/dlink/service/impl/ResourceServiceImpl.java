package com.dlink.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.ResourceMapper;
import com.dlink.model.Resource;
import com.dlink.model.Role;
import com.dlink.model.RoleResource;
import com.dlink.service.ResourceService;
import com.dlink.service.RoleResourceService;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class ResourceServiceImpl extends SuperServiceImpl<ResourceMapper, Resource> implements ResourceService {
    @Autowired
    private RoleResourceService roleResourceService;

    @Override
    public Result deleteResourceById(JsonNode para) {
        for (JsonNode item : para) {
            Integer id = item.asInt();
            Resource resource = getById(id);
            if (Asserts.isNull(resource)) {
                return Result.failed("资源不存在");
            }
            ProTableResult<RoleResource> roleResourceProTableResult = roleResourceService.selectForProTable(para);
            if (roleResourceProTableResult.getData().size() > 0) {
                return Result.failed("删除资源失败，该资源已被角色绑定");
            }
            boolean result = removeById(id);
            if (result) {
                return Result.succeed("删除资源成功");
            } else {
                return Result.failed("删除资源失败");
            }
        }
        return Result.failed("资源不存在");
    }
}
