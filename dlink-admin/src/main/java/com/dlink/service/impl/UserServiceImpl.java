package com.dlink.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.common.result.Result;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.UserMapper;
import com.dlink.model.Role;
import com.dlink.model.Tenant;
import com.dlink.model.User;
import com.dlink.model.UserRole;
import com.dlink.service.RoleService;
import com.dlink.service.TenantService;
import com.dlink.service.UserRoleService;
import com.dlink.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserServiceImpl
 *
 * @author wenmo
 * @since 2021/11/28 13:39
 */
@Service
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements UserService {

    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TenantService tenantService;


    @Override
    public Result registerUser(User user) {
        User userByUsername = getUserByUsername(user.getUsername());
        if (Asserts.isNotNull(userByUsername)) {
            return Result.failed("该账号已存在");
        }
        if (Asserts.isNullString(user.getPassword())) {
            user.setPassword(DEFAULT_PASSWORD);
        }
        user.setPassword(SaSecureUtil.md5(user.getPassword()));
        user.setEnabled(true);
        user.setIsDelete(false);
        if (save(user)) {
            return Result.succeed("注册成功");
        } else {
            return Result.failed("该账号已存在");
        }
    }

    @Override
    public boolean modifyUser(User user) {
        if (Asserts.isNull(user.getId())) {
            return false;
        }
        return updateById(user);
    }

    @Override
    public Result modifyPassword(String username, String password, String newPassword) {
        User user = getUserByUsername(username);
        if (Asserts.isNull(user)) {
            return Result.failed("该账号不存在");
        }
        if (!Asserts.isEquals(SaSecureUtil.md5(password), user.getPassword())) {
            return Result.failed("原密码错误");
        }
        user.setPassword(SaSecureUtil.md5(newPassword));
        if (updateById(user)) {
            return Result.succeed("密码修改成功");
        }
        return Result.failed("密码修改失败");
    }

    @Override
    public boolean removeUser(Integer id) {
        User user = new User();
        user.setId(id);
        user.setIsDelete(true);
        return updateById(user);
    }

    @Override
    public Result loginUser(String username, String password, boolean isRemember) {
        User user = getUserByUsername(username);
        if (Asserts.isNull(user)) {
            return Result.failed("账号或密码错误");
        }
        String userPassword = user.getPassword();
        if (Asserts.isNullString(password)) {
            return Result.failed("密码不能为空");
        }
        if (Asserts.isEquals(SaSecureUtil.md5(password), userPassword)) {
            if (user.getIsDelete()) {
                return Result.failed("账号不存在");
            }
            if (!user.getEnabled()) {
                return Result.failed("账号已被禁用");
            }
            StpUtil.login(user.getId(), isRemember);
            StpUtil.getSession().set("user", user);
            return Result.succeed(user, "登录成功");
        } else {
            return Result.failed("账号或密码错误");
        }
    }

    @Override
    public User getUserByUsername(String username) {
        User user = getOne(new QueryWrapper<User>().eq("username", username).eq("is_delete", 0));
        if (Asserts.isNotNull(user)) {
            user.setIsAdmin(Asserts.isEqualsIgnoreCase(username, "admin"));
        }
        return user;
    }

    @Override
    public Result grantRole(JsonNode para) {
        List<UserRole> userRoleList = new ArrayList<>();
        Integer userId = para.get("userId").asInt();
        JsonNode userRoleJsonNode = para.get("roles");

        for (JsonNode ids : userRoleJsonNode) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(ids.asInt());
            userRoleList.add(userRole);
        }
        // save or update user role
        boolean result = userRoleService.saveOrUpdateBatch(userRoleList, 1000);
        if (result) {
            return Result.succeed("用户授权角色成功");
        } else {
            return Result.failed("用户授权角色失败");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result removeGrantRole(JsonNode para) {
        List<UserRole> userRoleList = new ArrayList<>();
        Integer userId = para.get("userId").asInt();
        JsonNode userRoleJsonNode = para.get("roles");

        for (JsonNode ids : userRoleJsonNode) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(ids.asInt());
            userRoleList.add(userRole);
        }
        int result = userRoleService.deleteBathRelation(userRoleList);
        if (result > 0) {
            return Result.succeed("用户撤销授权角色成功");
        } else {
            return Result.failed("用户撤销授权角色失败");
        }
    }


    @Override
    public Result getTenants(String username) {
        User user = getUserByUsername(username);
        if (Asserts.isNull(user)) {
            return Result.failed("该账号不存在");
        }

        List<UserRole> userRoles = userRoleService.getUserRoleByUserId(user.getId());
        if (userRoles.size() == 0) {
            return Result.failed("用户未绑定角色");
        }
        List<Integer> roleIds = new ArrayList<>();
        userRoles.forEach(userRole -> roleIds.add(userRole.getRoleId()));

        List<Role> roles = roleService.getRoleByIds(roleIds);
        if (roles.size() == 0) {
            return Result.failed("用户角色未绑定租户");
        }
        List<Integer> tenantIds = new ArrayList<>();
        roles.forEach(role -> tenantIds.add(role.getTenantId()));
        List<Tenant> tenants = tenantService.getTenantByIds(tenantIds);
        return Result.succeed(tenants, "获取成功");
    }
}
