package com.dlink.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.common.result.Result;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.exception.BusException;
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
        if(Asserts.isNotNull(userByUsername)){
            return Result.failed("该账号已存在");
        }
        if(Asserts.isNullString(user.getPassword())){
            user.setPassword(DEFAULT_PASSWORD);
        }
        user.setPassword(SaSecureUtil.md5(user.getPassword()));
        user.setEnabled(true);
        user.setDelete(false);
        if(save(user)){
            return Result.succeed("注册成功");
        }else{
            return Result.failed("该账号已存在");
        }
    }

    @Override
    public boolean modifyUser(User user) {
        if(Asserts.isNull(user.getId())){
            return false;
        }
        if(Asserts.isNotNull(user.getPassword())){
            user.setPassword(SaSecureUtil.md5(user.getPassword()));
        }
        return updateById(user);
    }

    @Override
    public boolean removeUser(Integer id) {
        User user = new User();
        user.setId(id);
        user.setDelete(true);
        return updateById(user);
    }

    @Override
    public Result loginUser(String username, String password,boolean isRemember) {
        User user = getUserByUsername(username);
        if(Asserts.isNull(user)){
            return Result.failed("账号或密码错误");
        }
        String userPassword = user.getPassword();
        if(Asserts.isNullString(password)){
            return Result.failed("密码不能为空");
        }
        if(Asserts.isEquals(SaSecureUtil.md5(password),userPassword)){
            if(user.isDelete()){
                return Result.failed("账号不存在");
            }
            if(!user.isEnabled()){
                return Result.failed("账号已被禁用");
            }
            StpUtil.login(user.getId(), isRemember);
            StpUtil.getSession().set("user", user);
            return Result.succeed(user, "登录成功");
        }else{
            return Result.failed("账号或密码错误");
        }
    }

    @Override
    public User getUserByUsername(String username) {
        User user = getOne(new QueryWrapper<User>().eq("username", username).eq("is_delete",0));
        if(Asserts.isNotNull(user)){
            user.setAdmin(Asserts.isEqualsIgnoreCase(username,"admin"));
        }
        return user;
    }
}
