/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


package com.dlink.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.common.result.Result;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.UserMapper;
import com.dlink.model.User;
import com.dlink.service.UserService;
import org.springframework.stereotype.Service;

/**
 * UserServiceImpl
 *
 * @author wenmo
 * @since 2021/11/28 13:39
 */
@Service
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements UserService {

    private static final String DEFAULT_PASSWORD = "123456";

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
        User user = getOne(new QueryWrapper<User>().eq("username", username));
        if (Asserts.isNotNull(user)) {
            user.setIsAdmin(Asserts.isEqualsIgnoreCase(username, "admin"));
        }
        return user;
    }
}
