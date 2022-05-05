package com.dlink.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.TenantMapper;
import com.dlink.model.Namespace;
import com.dlink.model.Resource;
import com.dlink.model.Role;
import com.dlink.model.Tenant;
import com.dlink.service.NamespaceService;
import com.dlink.service.ResourceService;
import com.dlink.service.RoleService;
import com.dlink.service.TenantService;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class TenantServiceImpl extends SuperServiceImpl<TenantMapper, Tenant> implements TenantService {
    @Autowired
    private RoleService roleService;

    @Autowired
    private NamespaceService namespaceService;

    @Autowired
    private ResourceService resourceService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> deleteTenantById(JsonNode para) {
        for (JsonNode item : para) {
            Integer id = item.asInt();
            Tenant tenant = getById(id);
            if (Asserts.isNull(tenant)) {
                return Result.failed("租户不存在");
            }

            ProTableResult<Role> roleProTableResult = roleService.selectForProTable(para);
            if (roleProTableResult.getData().size() > 0) {
                return Result.failed("删除租户失败，该租户已绑定角色");
            }

            ProTableResult<Namespace> namespaceProTableResult = namespaceService.selectForProTable(para);
            if (namespaceProTableResult.getData().size() > 0) {
                return Result.failed("删除租户失败，该租户已绑定名称空间");
            }

            ProTableResult<Resource> resourceProTableResult = resourceService.selectForProTable(para);
            if (resourceProTableResult.getData().size() > 0) {
                return Result.failed("删除租户失败，该租户已绑资源");
            }

            boolean result = removeById(id);
            if (result) {
                return Result.succeed("删除成功");
            } else {
                return Result.failed("删除失败");
            }
        }
        return Result.failed("删除租户不存在");
    }

}
